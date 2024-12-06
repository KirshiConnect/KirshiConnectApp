package com.example.dataupload.owner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dataupload.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OwnerLoginActivity extends AppCompatActivity {

    EditText ownerLoginEmail, ownerLoginPassword;
    Button ownerLoginBtn;
    TextView registerTxt, forgotPass;
    ProgressBar progressBar;

    FirebaseAuth fAuth;
    DatabaseReference dRef;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_owner_login);

        ownerLoginEmail = findViewById(R.id.ownerLoginEmail);
        ownerLoginPassword = findViewById(R.id.ownerLoginPassword);
        ownerLoginBtn = findViewById(R.id.ownerLoginBtn);
        registerTxt = findViewById(R.id.registerTxt);
        forgotPass = findViewById(R.id.forgotPass);

        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();



        ownerLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateandProceed();
            }
        });

        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OwnerLoginActivity.this, OwnerRegisterActivity.class);
                startActivity(intent);
            }
        });


        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage("Enter your email");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString().trim();
                        if (TextUtils.isEmpty(mail)) {
                            Toast.makeText(OwnerLoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        fAuth.sendPasswordResetEmail(mail)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(OwnerLoginActivity.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(OwnerLoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                passwordResetDialog.create().show();
            }
        });

    }

    private void validateandProceed(){
        String Email = ownerLoginEmail.getText().toString();
        String Password = ownerLoginPassword.getText().toString();

        if(TextUtils.isEmpty(Email)){
            ownerLoginEmail.setError("Emter the email");
            return;
        }
        if(TextUtils.isEmpty(Password)){
            ownerLoginPassword.setError("Enter the correct password");
            return;
        }
        login(Email, Password);

    }

    private void login(String email, String password){
        progressBar.setVisibility(View.VISIBLE);
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(OwnerLoginActivity.this, "Successfully logged In", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(OwnerLoginActivity.this, OwnerActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(OwnerLoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


    }
}