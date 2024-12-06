package com.example.dataupload.customer;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dataupload.Adapters.CustomerRvAdapter;
import com.example.dataupload.Models.CustomerUserModel;
import com.example.dataupload.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerHomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CustomerRvAdapter customerRvAdapter;
    List<CustomerUserModel> customerList;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        customerList = new ArrayList<>();
        customerRvAdapter = new CustomerRvAdapter(this, customerList, user -> {
            // Handle item click
            Toast.makeText(this, "Clicked: " + user.getName(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(customerRvAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child("customer_id").child("products");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customerList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CustomerUserModel customer = dataSnapshot.getValue(CustomerUserModel.class);
                    customerList.add(customer);
                }
                customerRvAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerHomeActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
