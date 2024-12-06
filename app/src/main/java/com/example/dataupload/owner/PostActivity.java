package com.example.dataupload.owner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dataupload.R;
import com.example.dataupload.customer.CustomerHomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 1000;

    private ImageView profileImage;
    private ProgressBar progressBar;
    private EditText name, price, des;
    private Button btn, retriveBtn;

    private FirebaseAuth fAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        profileImage = findViewById(R.id.profileImage);
        progressBar = findViewById(R.id.progressBar);
        name = findViewById(R.id.name);
        des = findViewById(R.id.des);
        price = findViewById(R.id.price);
        btn = findViewById(R.id.btn);
        retriveBtn = findViewById(R.id.retriveBtn);

        // Initialize Firebase instances
        fAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");

        retriveBtn.setOnClickListener(v -> {
            Intent intent = new Intent(PostActivity.this, CustomerHomeActivity.class);
            startActivity(intent);
        });

        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        });

        btn.setOnClickListener(v -> {
            String Name = name.getText().toString().trim();
            String Description = des.getText().toString().trim();
            String Price = price.getText().toString().trim();

            if (TextUtils.isEmpty(Name)) {
                name.setError("Name is required");
                return;
            }

            if (TextUtils.isEmpty(Price)) {
                price.setError("Phone is required");
                return;
            }

            if (TextUtils.isEmpty(Description)){
                des.setError("Description is required");
                return;
            }

            if (imageUri == null) {
                Toast.makeText(PostActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            uploadImageToFirebase(imageUri, Name,Description, Price);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri uri, String name,String des, String price) {
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + ".jpg");

        fileRef.putFile(uri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                if (downloadUri != null) {
                    saveUserDataToRealtimeDB(name, des, price, downloadUri.toString());
                }
            }
        })).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(PostActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveUserDataToRealtimeDB(String name, String des, String price, String imageUrl) {
        String userID = fAuth.getCurrentUser() != null ? fAuth.getCurrentUser().getUid() : "Unknown_User";

        // Generate a unique key for the entry (can also use a timestamp)
        String entryKey = databaseReference.child(userID).child("userDetails").push().getKey();

        if (entryKey != null) {
            // Prepare the user data to be saved
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("description", des);
            userData.put("phone", price);
            userData.put("imageUrl", imageUrl);

            // Save the data under the generated entry key
            databaseReference.child("wau").child("userDetails").child(entryKey).setValue(userData)
                    .addOnSuccessListener(unused -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(PostActivity.this, "User data saved successfully", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }
}
