package com.abhi.ltcecampuscare.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.abhi.ltcecampuscare.Login_Activity;
import com.abhi.ltcecampuscare.R;
import com.abhi.ltcecampuscare.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    String role;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        if (getArguments() != null) {
            role = getArguments().getString("role", "USER"); // Default to USER
        }

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = currentUser.getUid();
        TextView welcome = root.findViewById(R.id.welcome);

        db.collection("Users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        welcome.setText("Welcome, " + name);
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });





        LinearLayout postEvents = root.findViewById(R.id.postEvents);
        LinearLayout lost_found = root.findViewById(R.id.lost_found);
        LinearLayout report_issues = root.findViewById(R.id.report_issues);
        LinearLayout log_out = root.findViewById(R.id.log_out);
        LinearLayout viewEvents = root.findViewById(R.id.view_events);


        if ("ADMIN".equalsIgnoreCase(role)) {
            viewEvents.setVisibility(View.GONE);
        } else {
            postEvents.setVisibility(View.GONE);
        }



        viewEvents.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.nav_viewevents));

        postEvents.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.nav_events));

        lost_found.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.nav_post_lostfound));

        report_issues.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.nav_report_issue));

        log_out.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), Login_Activity.class));
            getActivity().finish();
        });




        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}