package com.example.dataupload;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dataupload.customer.CustomerLoginActivity;
import com.example.dataupload.owner.OwnerLoginActivity;
import com.example.dataupload.rider.RiderActivity;
import com.example.dataupload.rider.RiderLoginActivity;

public class ChooseActivity extends AppCompatActivity {

    Button farmerBtn, customerBtn, riderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose);

        farmerBtn = findViewById(R.id.farmerBtn);
        customerBtn = findViewById(R.id.customerBtn);
        riderBtn = findViewById(R.id.riderBtn);

        farmerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseActivity.this, OwnerLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        customerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseActivity.this, CustomerLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        riderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseActivity.this, RiderActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}