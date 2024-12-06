package com.example.dataupload.owner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dataupload.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class OwnerRegisterActivity extends AppCompatActivity {

    ImageView ownerRegisterProfileImage;
    EditText ownerRegisterName, ownerRegisterAddress, ownerRegisterNumber, ownerRegisterEmail, ownerRegisterPassword, ownerRegisterConfirmPassword;
    Button ownerRegisterBtn;

    FirebaseAuth fAuth;
    DatabaseReference dRef;

    private static final int GALLERY_REQUEST_CODE = 1000;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 1001;

    private Uri imageUrl;

    String ownerName, ownerAddress, ownerNumber, ownerEmail, ownerPassword, ownerConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_register);

        ownerRegisterProfileImage = findViewById(R.id.ownerRegisterProfileImage);
        ownerRegisterName = findViewById(R.id.ownerRegisterName);
        ownerRegisterAddress = findViewById(R.id.ownerRegisterAddress);
        ownerRegisterNumber = findViewById(R.id.ownerRegisterNumber);
        ownerRegisterEmail = findViewById(R.id.ownerRegisterEmail);
        ownerRegisterPassword = findViewById(R.id.ownerRegisterPassword);
        ownerRegisterConfirmPassword = findViewById(R.id.ownerRegisterConfirmPassword);
        ownerRegisterBtn = findViewById(R.id.ownerRegisterBtn);

        fAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference("Owner");

        ownerRegisterProfileImage.setOnClickListener(v -> openGallery());

        ownerRegisterBtn.setOnClickListener(v -> {
            ownerName = ownerRegisterName.getText().toString().trim();
            ownerAddress = ownerRegisterAddress.getText().toString().trim();
            ownerNumber = ownerRegisterNumber.getText().toString().trim();
            ownerEmail = ownerRegisterEmail.getText().toString().trim();
            ownerPassword = ownerRegisterPassword.getText().toString().trim();
            ownerConfirmPassword = ownerRegisterConfirmPassword.getText().toString().trim();

            if (validateFields()) {
                registerOwner(ownerEmail, ownerPassword);
            }
        });
    }

    private void openGallery() {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        } else {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean validateFields() {
        if (ownerName.isEmpty()) {
            ownerRegisterName.setError("Enter name");
            return false;
        }
        if (ownerAddress.isEmpty()) {
            ownerRegisterAddress.setError("Enter address");
            return false;
        }
        if (ownerNumber.isEmpty()) {
            ownerRegisterNumber.setError("Enter number");
            return false;
        }
        if (ownerEmail.isEmpty()) {
            ownerRegisterEmail.setError("Enter email");
            return false;
        }
        if (ownerPassword.isEmpty()) {
            ownerRegisterPassword.setError("Enter password");
            return false;
        }
        if (!ownerPassword.equals(ownerConfirmPassword)) {
            ownerRegisterConfirmPassword.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    private void registerOwner(String email, String password) {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = fAuth.getCurrentUser();
                if (user != null) {
                    sendEmailVerification(user);
                    String userID = user.getUid();
                    uploadImageToFirebase(userID);
                }
            } else {
                Toast.makeText(OwnerRegisterActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(OwnerRegisterActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(OwnerRegisterActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageToFirebase(String userID) {
        if (imageUrl != null) {
            StorageReference fileRef = FirebaseStorage.getInstance().getReference("Owner/Profile Images/" + userID + ".jpg");
            fileRef.putFile(imageUrl).addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> saveDataToRealtimeDB(userID, uri.toString()));
            }).addOnFailureListener(e -> {
                Toast.makeText(OwnerRegisterActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            saveDataToRealtimeDB(userID, null);
        }
    }

    private void saveDataToRealtimeDB(String userID, String imageUrl) {
        Map<String, Object> ownerMap = new HashMap<>();
        ownerMap.put("Name", ownerName);
        ownerMap.put("Address", ownerAddress);
        ownerMap.put("Number", ownerNumber);
        ownerMap.put("Email", ownerEmail);
        ownerMap.put("Password", ownerPassword);

        if (imageUrl != null) {
            ownerMap.put("ImageUrl", imageUrl);
        }

        dRef.child(userID).setValue(ownerMap).addOnSuccessListener(unused -> {
            Toast.makeText(OwnerRegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
            clearFields();
            Intent intent = new Intent(OwnerRegisterActivity.this, PostActivity.class);
            startActivity(intent);
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(OwnerRegisterActivity.this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void clearFields() {
        ownerRegisterName.setText("");
        ownerRegisterAddress.setText("");
        ownerRegisterNumber.setText("");
        ownerRegisterEmail.setText("");
        ownerRegisterPassword.setText("");
        ownerRegisterConfirmPassword.setText("");
        ownerRegisterProfileImage.setImageResource(R.drawable.logo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUrl = data.getData();
            ownerRegisterProfileImage.setImageURI(imageUrl);
        }
    }
}
