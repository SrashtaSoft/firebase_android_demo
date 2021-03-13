package com.uvpce.firebasedemo.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.uvpce.firebasedemo.MainActivity;
import com.uvpce.firebasedemo.Model.ModelTask;
import com.uvpce.firebasedemo.Model.ModelUser;
import com.uvpce.firebasedemo.R;

import java.util.ArrayList;
import java.util.List;

import static com.uvpce.firebasedemo.Constants.Constants.STORAGE_PATH_UPLOADS;

public class SignInActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    MaterialEditText xEdtEmail, xEdtPassword;
    Button btnLogin;
    private List<ModelUser> userList;
    FirebaseAuth auth;
    TextView btnRegister;
    ProgressBar xProgressBar;
    String userId;

    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseUser != null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            intent.putExtra("userId", firebaseUser.getUid());
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        xEdtEmail = findViewById(R.id.xEdtEmail);
        xEdtPassword = findViewById(R.id.xEdtPassword);
        btnLogin = findViewById(R.id.xBtnLogin);
        btnRegister = findViewById(R.id.xBtnRegisterHere);
        xProgressBar = findViewById(R.id.xProgressBar);

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtEmail = xEdtEmail.getText().toString();
                String txtPassword = xEdtPassword.getText().toString();

                if (TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)) {
                    Toast.makeText(getApplicationContext(), "Enter all fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    xProgressBar.setVisibility(View.VISIBLE);
                    auth.signInWithEmailAndPassword(txtEmail, txtPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        fetchUserId();
                                    }
                                    else {
                                        xProgressBar.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchUserId() {
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                xProgressBar.setVisibility(View.GONE);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    ModelUser list = dataSnapshot1.getValue(ModelUser.class);
                    userList.add(list);
                }

                for (int i=0; i < userList.size(); i++) {
                    if (xEdtEmail.getText().toString().equals(userList.get(i).getEmail())) {
                        userId = userList.get(i).getId();
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        intent.putExtra("userId", userId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
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