package com.example.dataupload.rider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.dataupload.R;
import com.example.dataupload.customer.ViewLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RiderActivity extends AppCompatActivity {

    private TextView addressTextView, phoneTextView;
    private Button btnViewLocation, btnDeliverySuccess, btnDeliveryFailed;
    private FirebaseDatabase database;
    private DatabaseReference ordersRef;
    private String orderId; // Get this from the Intent or wherever it's passed from

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider); // Make sure this is the correct layout file

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        ordersRef = database.getReference("orders"); // Assuming orders are stored under "orders" node


        // Get orderId (this could come from Intent or wherever it is passed)
        orderId = getIntent().getStringExtra("orderId");
        Log.d("OrderID", "Passing orderId: " + orderId);

        if (orderId == null) {
            Toast.makeText(this, "Order ID is missing", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if the order ID is null
            return;
        }

        // Initialize Views
        addressTextView = findViewById(R.id.address);
        phoneTextView = findViewById(R.id.phone);
        btnViewLocation = findViewById(R.id.btn_view_location);
        btnDeliverySuccess = findViewById(R.id.btn_delivery_success);
        btnDeliveryFailed = findViewById(R.id.btn_delivery_failed);

        // Fetch Order Data from Firebase
        getOrderDetails();

        // Set OnClickListener for View Location Button
        btnViewLocation.setOnClickListener(v -> viewLocation());

        // Set OnClickListener for Delivery Success Button
        btnDeliverySuccess.setOnClickListener(v -> updateDeliveryStatus("Success"));

        // Set OnClickListener for Delivery Failed Button
        btnDeliveryFailed.setOnClickListener(v -> updateDeliveryStatus("Failed"));
    }

    private void getOrderDetails() {
        ordersRef.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Check if order data exists
                if (snapshot.exists()) {
                    String address = snapshot.child("destinationAddress").getValue(String.class);
                    String phone = snapshot.child("customerPhone").getValue(String.class);

                    if (address != null && phone != null) {
                        // Set values to TextViews
                        addressTextView.setText("Address: " + address);
                        phoneTextView.setText("Phone: " + phone);
                    } else {
                        Toast.makeText(RiderActivity.this, "Missing order details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RiderActivity.this, "Order not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
                Toast.makeText(RiderActivity.this, "Error fetching order data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void viewLocation() {
        ordersRef.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Check if latitude and longitude are present
                if (snapshot.exists()) {
                    Double latitude = snapshot.child("latitude").getValue(Double.class);
                    Double longitude = snapshot.child("longitude").getValue(Double.class);

                    if (latitude != null && longitude != null && latitude != 0 && longitude != 0) {
                        openMap(latitude, longitude);
                    } else {
                        Toast.makeText(RiderActivity.this, "Invalid location data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RiderActivity.this, "Location data missing", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
                Toast.makeText(RiderActivity.this, "Error fetching location data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMap(double latitude, double longitude) {
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "Google Maps is not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDeliveryStatus(String status) {
        ordersRef.child(orderId).child("status").setValue(status)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RiderActivity.this, "Delivery Status Updated: " + status, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RiderActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
