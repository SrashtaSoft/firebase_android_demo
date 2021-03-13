package com.uvpce.firebasedemo.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uvpce.firebasedemo.Model.ModelTask;
import com.uvpce.firebasedemo.R;
import java.util.ArrayList;
import java.util.List;
import static com.uvpce.firebasedemo.Constants.Constants.STORAGE_PATH_UPLOADS;

public class TaskDetailActivity extends AppCompatActivity {

    TextView xTvName, xTvDesc, xTvDate;
    ImageView xIvImage;
    Intent intent;
    String userId, taskId;
    private List<ModelTask> taskData;
    ProgressBar xProgressBar;
    private StorageReference storageReference;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        xTvName = findViewById(R.id.xTvTaskName);
        xTvDesc = findViewById(R.id.xTvTaskDesc);
        xTvDate = findViewById(R.id.xTvDate);
        xIvImage = findViewById(R.id.xIvSelectedImage);
        xProgressBar = findViewById(R.id.xProgressBar);
        linearLayout = findViewById(R.id.xLl);

        storageReference = FirebaseStorage.getInstance().getReference();

        xProgressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);

        intent = getIntent();
        userId = intent.getStringExtra("userId");
        taskId = intent.getStringExtra("taskId");

        taskData = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Task").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("hloe", dataSnapshot.getChildren().toString());
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    ModelTask list = dataSnapshot1.getValue(ModelTask.class);
                    taskData.add(list);
                }

                for (int i=0; i < taskData.size(); i++) {
                    if (taskId.equals(taskData.get(i).getId())) {
                        xProgressBar.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                        StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + taskData.get(i).getImage_id() + ".jpg");
                        sRef.getFile(Uri.parse(STORAGE_PATH_UPLOADS+taskData.get(i).getImage_id()+".jpg"));
                        xTvName.setText(taskData.get(i).getTask_name());
                        xTvDate.setText(taskData.get(i).getDate());
                        xTvDesc.setText(taskData.get(i).getTask_desc());
                        Glide.with(getApplicationContext()).load(sRef).into(xIvImage);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}