package com.uvpce.firebasedemo.Activity;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.uvpce.firebasedemo.MainActivity;
import com.uvpce.firebasedemo.R;

public class SignInActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    MaterialEditText xEdtEmail, xEdtPassword;
    Button btnLogin;
    FirebaseAuth auth;
    TextView btnRegister;
    ProgressBar xProgressBar;

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
                                        xProgressBar.setVisibility(View.GONE);
                                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
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
}