package com.abhi.ltcecampuscare.ui.lost_and_found;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abhi.ltcecampuscare.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ViewLostFoundFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private List<LostFoundModel> lostFoundList;
    private LostFoundAdapter adapter;

    public ViewLostFoundFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_lost_found, container, false); // ✅ reusing events layout

        recyclerView = view.findViewById(R.id.eventRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        lostFoundList = new ArrayList<>();
        adapter = new LostFoundAdapter(lostFoundList);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        loadLostFound();

        return view;
    }

    private void loadLostFound() {
        firestore.collection("LostFound") // ✅ corrected collection name
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    lostFoundList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        LostFoundModel item = doc.toObject(LostFoundModel.class);
                        lostFoundList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load posts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
