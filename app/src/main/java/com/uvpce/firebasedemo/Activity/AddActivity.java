package com.uvpce.firebasedemo.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.uvpce.firebasedemo.MainActivity;
import com.uvpce.firebasedemo.R;

import java.util.Calendar;
import java.util.HashMap;

import static com.uvpce.firebasedemo.Constants.Constants.STORAGE_PATH_UPLOADS;

public class AddActivity extends AppCompatActivity {

    Button xBtnAdd;
    MaterialEditText xEdtTaskName, xEdtDesc;
    TextView xTvSelectDate, xBtnSelectImage;
    ImageView xBtnSelectDate, xIvSelectedImage;
    Calendar calendar;
    private static final int CAMERA_REQUEST_CODE = 100;
    int flag = 0;
    FirebaseAuth auth;
    DatabaseReference reference;
    Intent intent;
    String userId;
    ProgressBar xProgressBar;
    private Uri filePath;
    private String uploadImageId;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        xBtnAdd = findViewById(R.id.xBtnAdd);
        xEdtTaskName = findViewById(R.id.xEdtTaskName);
        xEdtDesc = findViewById(R.id.xEdtDesc);
        xTvSelectDate = findViewById(R.id.xTvDate);
        xBtnSelectImage = findViewById(R.id.xBtnSelectImage);
        xBtnSelectDate = findViewById(R.id.xBtnCalender);
        xIvSelectedImage = findViewById(R.id.xIvSelectedImage);
        xProgressBar = findViewById(R.id.xProgressBar);

        intent = getIntent();
        userId = intent.getStringExtra("userId");

        auth = FirebaseAuth.getInstance();
        calendar = Calendar.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference();

        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        }

        int month = calendar.get(Calendar.MONTH);
        String selectedDate = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (month+1)+ "/" + calendar.get(Calendar.YEAR);
        xTvSelectDate.setText(selectedDate);

        xBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtTaskName = xEdtTaskName.getText().toString();
                String txtDesc = xEdtDesc.getText().toString();

                if (TextUtils.isEmpty(txtTaskName) || TextUtils.isEmpty(txtDesc)) {
                    Toast.makeText(getApplicationContext(), "Enter all fields", Toast.LENGTH_SHORT).show();
                }
                else if (xIvSelectedImage.getDrawable() == null) {
                    Toast.makeText(getApplicationContext(), "Select image", Toast.LENGTH_SHORT).show();
                }
                else {
                    String key = reference.push().getKey();
                    reference = FirebaseDatabase.getInstance().getReference("Task").child(userId).child(key);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("user_id", userId);
                    hashMap.put("id", key);
                    hashMap.put("task_name", txtTaskName);
                    hashMap.put("task_desc", txtDesc);
                    hashMap.put("date", xTvSelectDate.getText().toString());
                    hashMap.put("image_id", uploadImageId);

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                xProgressBar.setVisibility(View.GONE);
                                finish();
                            }
                        }
                    });
                }
            }
        });

        xBtnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivity.this,
                        ( view, year, monthOfYear, dayOfMonth) -> {
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, monthOfYear);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            xTvSelectDate.setText(selectedDate);
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        xBtnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    if (flag == 1) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 0);
                    }
                    else {
                        Toast.makeText(AddActivity.this, "Camera permission not granted.", Toast.LENGTH_LONG).show();
                    }
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    filePath = imageReturnedIntent.getData();
                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    xIvSelectedImage.setVisibility(View.VISIBLE);
                    xIvSelectedImage.setImageBitmap(imageBitmap);
                    alertDialog();
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    filePath = imageReturnedIntent.getData();
                    Uri selectedImage = imageReturnedIntent.getData();
                    xIvSelectedImage.setVisibility(View.VISIBLE);
                    xIvSelectedImage.setImageURI(selectedImage);
                    alertDialog();
                }
                break;
        }
    }

    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to add this image?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                uploadImages();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted.", Toast.LENGTH_LONG).show();
                flag = 1;
            } else {
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_LONG).show();
                flag = 0;
            }
        }
    }

    public void uploadImages() {
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            String uploadId = reference.push().getKey();
            uploadImageId = uploadId;
            //getting the storage reference
            StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + uploadImageId + "." + getFileExtension(filePath));

            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();
                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            //display an error if no file is selected
        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}