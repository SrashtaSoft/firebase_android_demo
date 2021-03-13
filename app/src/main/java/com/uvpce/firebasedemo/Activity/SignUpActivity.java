package com.uvpce.firebasedemo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.uvpce.firebasedemo.MainActivity;
import com.uvpce.firebasedemo.R;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    MaterialEditText xEdtUserName, xEdtEmail, xEdtPassword;
    Button xBtnRegister;
    FirebaseAuth auth;
    TextView xBtnLogin;
    DatabaseReference reference;
    ProgressBar xProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        xEdtUserName = findViewById(R.id.xEdtUserName);
        xEdtEmail = findViewById(R.id.xEdtEmail);
        xEdtPassword = findViewById(R.id.xEdtPassword);
        xBtnRegister = findViewById(R.id.xBtnRegister);
        xBtnLogin = findViewById(R.id.xBtnBackToLogin);
        xProgressBar = findViewById(R.id.xProgressBar);

        auth = FirebaseAuth.getInstance();

        xBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtUsername = xEdtUserName.getText().toString();
                String txtEmail = xEdtEmail.getText().toString();
                String txtPassword = xEdtPassword.getText().toString();

                if (TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)) {
                    Toast.makeText(getApplicationContext(), "Enter all fields", Toast.LENGTH_SHORT).show();
                }
                else if (txtPassword.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password must be atleast 6 character", Toast.LENGTH_SHORT).show();
                }
                else {
                    register(txtUsername, txtEmail, txtPassword);
                }
            }
        });

        xBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void register (final String username, final String email, String password) {

        xProgressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    assert firebaseUser != null;
                    String userId = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userId);
                    hashMap.put("username", username);
                    hashMap.put("email", email);
                    hashMap.put("password", password);

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                xProgressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.putExtra("userId", userId);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
                else {
                    xProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "You can't register with this email", Toast.LENGTH_SHORT).show();
                }
            });
    }
}