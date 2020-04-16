package com.example.custodian.ui.recents;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.custodian.R;
import com.example.custodian.model.caretaker.Hired;
import com.example.custodian.model.recents.RecentBookingsAdapter;
import com.example.custodian.model.recents.SwipeToDeleteCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecentBookings extends Fragment implements ValueEventListener {

    Activity activity;
    Hired hired;
    RecentBookingsAdapter adapter;
    RecyclerView recyclerView;
    FirebaseUser fUser;
    DatabaseReference databaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recent_bookings, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        activity=getActivity();
        recyclerView = view.findViewById(R.id.bookings_list);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        if(fUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("hired_" + fUser.getUid());
            databaseReference.addValueEventListener(this);
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        hired = dataSnapshot.getValue(Hired.class);
        adapter=new RecentBookingsAdapter(hired);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        enableSwipeToDeleteAndUndo();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.i("Recents", "Data Read Cancelled to populate Recents");
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(activity) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                hired=adapter.removeItem(position);
                View view = getActivity().findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(view, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hired = adapter.restoreItem(position);
                        recyclerView.scrollToPosition(position);
                    }
                });

                databaseReference.child("hired_" + fUser.getUid()).setValue(hired).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Failed", e.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("Success","data"+hired.getCaretaker().size());
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

}