package com.example.dataupload;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dataupload.rider.RiderActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfirmOrderActivity extends AppCompatActivity {

    private EditText destinationAddressEditText, customerNameEditText, customerNumberEditText;
    private Button saveOrderButton, getLocationButton;
    private double latitude, longitude;

    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        destinationAddressEditText = findViewById(R.id.DestinationAddress);
        customerNameEditText = findViewById(R.id.CustomerName);
        customerNumberEditText = findViewById(R.id.CustomerNumber);
        saveOrderButton = findViewById(R.id.ConfirmOrder);
        getLocationButton = findViewById(R.id.getLocationBtn);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);

        saveOrderButton.setOnClickListener(v -> {
            String destinationAddress = destinationAddressEditText.getText().toString();
            String customerName = customerNameEditText.getText().toString();
            String customerPhone = customerNumberEditText.getText().toString();

            if (destinationAddress.isEmpty() || customerName.isEmpty() || customerPhone.isEmpty()) {
                Toast.makeText(ConfirmOrderActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Order order = new Order(destinationAddress, customerName, customerPhone, latitude, longitude);

            String orderId = ordersRef.push().getKey();  // Generate unique ID for the order
            if (orderId != null) {
                ordersRef.child(orderId).setValue(order)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ConfirmOrderActivity.this, "Order saved successfully!", Toast.LENGTH_SHORT).show();
                            Intent riderIntent = new Intent(ConfirmOrderActivity.this, RiderActivity.class);
                            riderIntent.putExtra("orderId", orderId); // Pass the orderId
                            startActivity(riderIntent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ConfirmOrderActivity.this, "Failed to save order.", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        getLocationButton.setOnClickListener(v -> {
            Intent mapIntent = new Intent(ConfirmOrderActivity.this, MapActivity.class);
            startActivity(mapIntent);
        });
    }

    public static class Order {
        public String destinationAddress, customerName, customerPhone;
        public double latitude, longitude;

        public Order(String destinationAddress, String customerName, String customerPhone, double latitude, double longitude) {
            this.destinationAddress = destinationAddress;
            this.customerName = customerName;
            this.customerPhone = customerPhone;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
