package com.example.dataupload.customer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.dataupload.Adapters.CustomerRvAdapter;
import com.example.dataupload.Models.CustomerUserModel;
import com.example.dataupload.R;
import com.example.dataupload.customer.DetailedActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView catRv, newProductRv, popularProductRv, random_rv;
    CustomerRvAdapter categoryAdapter, newProductAdapter, popularProductAdapter, randomProductAdapter;
    List<CustomerUserModel> categoryList, newProductList, popularProductList, randomProductList;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    ViewPager2 sliderViewPager;
    List<Integer> sliderImages;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize RecyclerViews
        catRv = root.findViewById(R.id.rec_category);
        newProductRv = root.findViewById(R.id.new_product_rec);
        popularProductRv = root.findViewById(R.id.popular_rec);
        random_rv = root.findViewById(R.id.random_rv);

        sliderViewPager = root.findViewById(R.id.slider_viewpager);
        progressBar = root.findViewById(R.id.progressBar);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize slider images with hardcoded resource IDs
        sliderImages = new ArrayList<>();
        sliderImages.add(R.drawable.ic_launcher_foreground);
        sliderImages.add(R.drawable.ic_launcher_background);
        sliderImages.add(R.drawable.logo);

        // Set up the ViewPager2 adapter for the slider
        SliderAdapter sliderAdapter = new SliderAdapter(sliderImages);
        sliderViewPager.setAdapter(sliderAdapter);

        // Set up the auto slider
        startAutoSlider();

        // Set up RecyclerViews and Adapters
        setUpRecyclerViews();

        // Fetch data from Firebase
        fetchData("Unknown_User", categoryList, categoryAdapter);
        fetchData("wau", newProductList, newProductAdapter);
        fetchData("zCwQEiQFT1a9c52Dn5Vbj0mtYQ12", popularProductList, popularProductAdapter);
        fetchData("wau", randomProductList, randomProductAdapter);

        return root;
    }

    private void setUpRecyclerViews() {
        catRv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        categoryList = new ArrayList<>();
        categoryAdapter = new CustomerRvAdapter(getContext(), categoryList, user -> {
            startActivity(new Intent(getContext(), DetailedActivity.class));
        });
        catRv.setAdapter(categoryAdapter);

        newProductRv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        newProductList = new ArrayList<>();
        newProductAdapter = new CustomerRvAdapter(getContext(), newProductList, user -> {
            startActivity(new Intent(getContext(), DetailedActivity.class));
        });
        newProductRv.setAdapter(newProductAdapter);

        popularProductRv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        popularProductList = new ArrayList<>();
        popularProductAdapter = new CustomerRvAdapter(getContext(), popularProductList, user -> {
            startActivity(new Intent(getContext(), DetailedActivity.class));

        });
        popularProductRv.setAdapter(popularProductAdapter);

        random_rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        randomProductList = new ArrayList<>();
        randomProductAdapter = new CustomerRvAdapter(getContext(), randomProductList, user -> {
            startActivity(new Intent(getContext(), DetailedActivity.class));
        });
        random_rv.setAdapter(randomProductAdapter);
    }

    private void fetchData(String userId, List<CustomerUserModel> list, CustomerRvAdapter adapter) {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.child("users").child(userId).child("userDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CustomerUserModel user = snapshot.getValue(CustomerUserModel.class);
                    list.add(user);
                }
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void startAutoSlider() {
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = sliderViewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % sliderImages.size();
                sliderViewPager.setCurrentItem(nextItem, true);
                handler.postDelayed(sliderRunnable, 2000);
            }
        };
        handler.post(sliderRunnable);
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(sliderRunnable);
    }

    private class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

        private List<Integer> images;

        public SliderAdapter(List<Integer> images) {
            this.images = images;
        }

        @Override
        public SliderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, parent, false);
            return new SliderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SliderViewHolder holder, int position) {
            holder.sliderImage.setImageResource(images.get(position));
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        public class SliderViewHolder extends RecyclerView.ViewHolder {
            ImageView sliderImage;

            public SliderViewHolder(View itemView) {
                super(itemView);
                sliderImage = itemView.findViewById(R.id.slider_image);
            }
        }
    }
}
