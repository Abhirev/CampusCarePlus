package com.abhi.ltcecampuscare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.abhi.ltcecampuscare.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private String role = null; // store globally

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);



        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Set up Navigation
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_view_reported,
                R.id.nav_view_lostfound,
                R.id.nav_events
        ).setOpenableLayout(drawer).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        // Get user info and set sidebar header
        View headerView = navigationView.getHeaderView(0);
        TextView navName = headerView.findViewById(R.id.name);
        TextView navEmail = headerView.findViewById(R.id.sideEmail);




        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            String email = currentUser.getEmail();
            navEmail.setText(email);

            FirebaseFirestore.getInstance().collection("Users").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            navName.setText(name);
                        }
                        binding.appBarMain.fab.setOnClickListener(view -> {
                            String name = documentSnapshot.getString("name");
                            Toast.makeText(MainActivity.this,"Welcome to CampusCare+ "+name , Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> navName.setText("User"));
        }





        // ✅ Determine role from intent (only on first load)
        if (savedInstanceState == null) {
            role = getIntent().getStringExtra("role");
            Bundle bundle = new Bundle();
            bundle.putString("role", role); // role = "ADMIN" or "USER"
            navController.navigate(R.id.nav_home, bundle); // Use only nav_home now
        }

        // ✅ Custom Drawer Navigation Handling
        binding.navView.setNavigationItemSelectedListener(item -> {
            NavController navControlle = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            boolean handled = NavigationUI.onNavDestinationSelected(item, navControlle);
            if (handled) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }


            return true;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
