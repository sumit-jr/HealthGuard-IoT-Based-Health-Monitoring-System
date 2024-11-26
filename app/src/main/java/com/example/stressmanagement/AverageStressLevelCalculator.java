package com.example.stressmanagement;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AverageStressLevelCalculator {




        private static final String TAG = "AverageCalculator";
        private DatabaseReference userRef;
        private Context context;
        private ProgressDialog progressDialog;
        private static double averageStressLevel = 0;
        private static boolean isAverageCalculated = false;

        public AverageStressLevelCalculator(Context context) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String userId = auth.getCurrentUser().getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("user").child(userId);
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Calculating average stress level...");
            progressDialog.setCancelable(false);
            this.context = context;
        }

        public void checkStressLevelAndCalculate(final AverageDataListener listener) {
            progressDialog.show(); // Show ProgressDialog when checking stress level

            userRef.child("stress_level").addValueEventListener(new ValueEventListener() {
                    @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "onDataChange: Stress level data exists");
                        calculateAverageStressLevel(listener, dataSnapshot);
                    } else {
                        Log.d(TAG, "onDataChange: Stress level data does not exist");
                        listener.onNoStressLevelFound();
                        progressDialog.dismiss(); // Dismiss ProgressDialog when no data found
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onError(databaseError.toException());
                    progressDialog.dismiss(); // Dismiss ProgressDialog on error
                }
            });
        }

        private void calculateAverageStressLevel(final AverageDataListener listener, DataSnapshot dataSnapshot) {
            Log.d(TAG, "calculateAverageStressLevel: SECOND calculation...");
            double totalStressLevel = 0;
            int count = 0;
            Log.d(TAG, "calculateAverageStressLevel: THIRD calculation...");


            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                // Retrieve stress level value from the current snapshot
                Double stressLevel = snapshot.getValue(Double.class);
                if (stressLevel != null) {
                    totalStressLevel += stressLevel;
                    count++;
                    Log.d(TAG, "calculateAverageStressLevel: Stress level value = " + stressLevel);
                }


                progressDialog.dismiss();

            }
            Log.d(TAG, "calculateAverageStressLevel: Total Stress Level = " + totalStressLevel);
            Log.d(TAG, "calculateAverageStressLevel: Count = " + count);
            averageStressLevel = count > 0 ? totalStressLevel / count : 0;
            isAverageCalculated = true;
            listener.onAverageDataCalculated(averageStressLevel);
            progressDialog.dismiss(); // Dismiss ProgressDialog after calculation
            Log.d(TAG, "Average Stress Level calculated: " + averageStressLevel);
        }

        public interface AverageDataListener {
            void onAverageDataCalculated(double averageStressLevel);

            void onNoStressLevelFound();

            void onError(Exception e);
        }
    }
