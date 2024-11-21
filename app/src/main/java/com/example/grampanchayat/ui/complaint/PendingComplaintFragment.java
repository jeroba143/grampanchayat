package com.example.grampanchayat.ui.complaint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.grampanchayat.R;
import com.example.grampanchayat.data.model.Complaint;

import java.util.ArrayList;
import java.util.List;

public class PendingComplaintFragment extends Fragment {

    private RecyclerView recyclerView;
    private ComplaintAdapter complaintAdapter;
    private List<Complaint> complaintList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_pending_complaint, container, false);

        recyclerView = view.findViewById(R.id.recyclerUserComplaint);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        complaintList = new ArrayList<>();
        complaintAdapter = new ComplaintAdapter(getContext(), complaintList);
        recyclerView.setAdapter(complaintAdapter);

        readPendingComplaints();

        return view;
    }

    public void readPendingComplaints() {
        FirebaseDatabase.getInstance().getReference().child("Complaints").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                complaintList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Complaint complaint = dataSnapshot.getValue(Complaint.class);

                    //add to list if email matches the current user

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (complaint.getStatus().equals("In Progress")) {
                        complaintList.add(complaint);
                    }


                }

                complaintAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}