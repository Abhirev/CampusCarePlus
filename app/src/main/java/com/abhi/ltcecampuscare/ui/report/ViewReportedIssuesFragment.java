package com.abhi.ltcecampuscare.ui.report;

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

public class ViewReportedIssuesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private List<ReportModel> reportList;
    private ReportAdapter adapter;

    public ViewReportedIssuesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_reported_issues, container, false); // ✅ reusing events layout

        recyclerView = view.findViewById(R.id.eventRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reportList = new ArrayList<>();
        adapter = new ReportAdapter(reportList);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        loadReport();

        return view;
    }

    private void loadReport() {
        firestore.collection("Issues") // ✅ corrected collection name
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    reportList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        ReportModel item = doc.toObject(ReportModel.class);
                        reportList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load posts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
