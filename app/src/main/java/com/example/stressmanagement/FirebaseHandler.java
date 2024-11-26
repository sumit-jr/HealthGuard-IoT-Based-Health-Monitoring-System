package com.example.stressmanagement;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHandler {
    private DatabaseReference stressRateRef;
    private DatabaseReference nameRef; // Reference for fetching the name
    private ValueEventListener stressRateEventListener;
    private ValueEventListener nameEventListener; // Listener for name updates
    private FirebaseListener listener;
    private String userId;

    // Modify the constructor to accept userId parameter
    public FirebaseHandler(FirebaseListener listener, String userId) {
        this.userId = userId; // Assign userId
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        stressRateRef = database.getReference().child("user").child(userId).child("stress_level");
        nameRef = database.getReference().child("user").child(userId).child("userName"); // Reference for fetching the name
        this.listener = listener;
    }

    public void startListening() {
        // Listener for stress level updates
        stressRateEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Long stressLevel = dataSnapshot.getValue(Long.class);
                    if (stressLevel != null) {
                        listener.onStressLevelChanged(stressLevel);
                    } else {
                        listener.onError("Stress level from Firebase is null");
                    }
                } else {
                    listener.onError("No data exists in Firebase");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        };
        stressRateRef.addValueEventListener(stressRateEventListener);

        // Listener for name updates
        nameEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.getValue(String.class);
                    if (name != null) {
                        listener.onNameChanged(name); // Notify listener about name update
                    } else {
                        listener.onError("Name from Firebase is null");
                    }
                } else {
                    listener.onError("No name exists in Firebase");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        };
        nameRef.addValueEventListener(nameEventListener); // Start listening for name updates
    }

    public void stopListening() {
        if (stressRateEventListener != null) {
            stressRateRef.removeEventListener(stressRateEventListener);
        }
        if (nameEventListener != null) {
            nameRef.removeEventListener(nameEventListener);
        }
    }

    public interface FirebaseListener {
        void onStressLevelChanged(Long stressLevel);
        void onNameChanged(String name);
        void onError(String message);
    }
}
