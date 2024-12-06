package com.example.dataupload.customer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dataupload.Adapters.MyCartAdapter;
import com.example.dataupload.Models.MyCartModel;
import com.example.dataupload.R;
import com.example.dataupload.customer.DetailedActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyCartAdapter adapter;
    private List<MyCartModel> cartItemList;
    private FirebaseAuth fAuth;

    private static final String FCM_SERVER_KEY = "hzKyz7_CkHKi8zTwACsSBdifhPF8dY-TLo9DA4U0YpY"; // Replace with your Firebase server key
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Subscribe to the topic "Farmers" for notifications
        FirebaseMessaging.getInstance().subscribeToTopic("b ");

        recyclerView = view.findViewById(R.id.cart_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartItemList = new ArrayList<>();
        adapter = new MyCartAdapter(getContext(), cartItemList, item -> {
            // Open DetailedActivity on cart item click
            Intent intent = new Intent(getActivity(), DetailedActivity.class);
            intent.putExtra("product_name", item.getProductName());
            intent.putExtra("product_price", String.valueOf(item.getTotalPrice()));
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        fAuth = FirebaseAuth.getInstance();

        loadCartItems();

        // Handle checkout button click
        view.findViewById(R.id.checkOut).setOnClickListener(v -> checkout());

        return view;
    }

    private void loadCartItems() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItemList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    MyCartModel item = itemSnapshot.getValue(MyCartModel.class);
                    if (item != null) {
                        cartItemList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CartFragment", "Error loading cart items", error.toException());
            }
        });
    }

    private void checkout() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference orderReference = FirebaseDatabase.getInstance().getReference("Orders");

        for (MyCartModel item : cartItemList) {
            DatabaseReference newOrder = orderReference.push();  // Create a unique order ID
            newOrder.setValue(item).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    notifyFarmer(item);
                    FirebaseDatabase.getInstance().getReference("Cart").child(userId).removeValue();
                    Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to place order", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void notifyFarmer(MyCartModel item) {
        new Thread(() -> {
            try {
                // Create FCM message payload
                Map<String, Object> notification = new HashMap<>();
                notification.put("title", "New Order Received");
                notification.put("body", "Order for " + item.getProductName() + " has been placed!");

                Map<String, Object> payload = new HashMap<>();
                payload.put("to", "/topics/farmers"); // Assuming farmers are subscribed to this topic
                payload.put("notification", notification);

                // Send POST request to FCM
                URL url = new URL(FCM_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "key=" + FCM_SERVER_KEY);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(new com.google.gson.Gson().toJson(payload));
                writer.flush();
                writer.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("CartFragment", "Notification sent successfully");
                } else {
                    Log.e("CartFragment", "Error sending notification: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("CartFragment", "Error sending notification", e);
            }
        }).start();
    }
}
