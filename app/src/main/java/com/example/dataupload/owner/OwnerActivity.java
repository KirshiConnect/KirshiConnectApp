package com.example.dataupload.owner;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.dataupload.R;
import com.example.dataupload.databinding.ActivityOwnerBinding;
import com.example.dataupload.owner.ownerFragments.OwnerOrderFragment;
import com.example.dataupload.owner.ownerFragments.OwnerPostFragment;
import com.example.dataupload.owner.ownerFragments.OwnerProfileFragment;

public class OwnerActivity extends AppCompatActivity {

    ActivityOwnerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnerBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            // Use if-else instead of switch
            if (item.getItemId() == R.id.post) {
                selectedFragment = new OwnerPostFragment();
            } else if (item.getItemId() == R.id.order) {
                selectedFragment = new OwnerOrderFragment();
            } else if (item.getItemId() == R.id.profile) {
                selectedFragment = new OwnerProfileFragment();
            }

            // Replace the fragment
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.container.getId(), selectedFragment)
                        .commit();
            }
            return true;
        });

// Set HomeFragment as default when activity starts
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(binding.container.getId(), new OwnerPostFragment())
                    .commit();
            binding.bottomNavigation.setSelectedItemId(R.id.post); // Highlight 'Home' as selected
        }


    }
}