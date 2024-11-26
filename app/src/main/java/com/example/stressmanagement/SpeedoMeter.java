package com.example.stressmanagement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class SpeedoMeter extends AppCompatActivity implements AverageStressLevelCalculator.AverageDataListener  {




        private static final String TAG = "SpeedoMeter";

        private HalfGauge halfGauge;
        private DatabaseReference stressRateRef;
        private TextView statusUpdate;
        private Button backButton, ShareButton;
        private String userId;
        private double averageStressLevel;

        private AverageStressLevelCalculator averageStressLevelCalculator;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_speedo_meter);

            halfGauge = findViewById(R.id.halfGauge);
            statusUpdate = findViewById(R.id.statusUpdate);
            backButton = findViewById(R.id.Back);
            ShareButton = findViewById(R.id.Share);

            addColorRanges();

            halfGauge.setMinValue(0.0);
            halfGauge.setMaxValue(100.0);
            halfGauge.setValue(0.0);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            stressRateRef = database.getReference().child("user").child(userId).child("stress_level");

//            averageStressLevelCalculator = new AverageStressLevelCalculator(this);
//            averageStressLevelCalculator.checkStressLevelAndCalculate(this);

            Log.d(TAG, "onCreate: Checking stress level and calculating average...");

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Finish the current activity to go back
                }
            });

            ShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String stressRateMessage = statusUpdate.getText().toString();
                    double stressRateValue = halfGauge.getValue();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "Your Stress Rate is " + " " + stressRateValue + " " + "so you are" + " " + stressRateMessage);
                    intent.setType("text/plain");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });

            stressRateRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        try {
                            double stressRate = dataSnapshot.getValue(Double.class);
                            updateStressRate(stressRate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(SpeedoMeter.this, "No stress rate data found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SpeedoMeter.this, "Wear your device.... " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    private void addColorRanges() {

        Range range1 = new Range();
        range1.setColor(Color.parseColor("#049387"));
        range1.setFrom(0.0);
        range1.setTo(10.0);

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#248F36"));
        range2.setFrom(10.0);
        range2.setTo(20.0);

        Range range3 = new Range();
        range3.setColor(Color.parseColor("#3DEF29"));
        range3.setFrom(20.0);
        range3.setTo(30.0);

        Range range4 = new Range();
        range4.setColor(Color.parseColor("#598823"));
        range4.setFrom(30.0);
        range4.setTo(40.0);

        Range range5 = new Range();
        range5.setColor(Color.parseColor("#9FA169"));
        range5.setFrom(40.0);
        range5.setTo(50.0);

        Range range6 = new Range();
        range6.setColor(Color.parseColor("#EDBC0D"));
        range6.setFrom(50.0);
        range6.setTo(60.0);

        Range range7 = new Range();
        range7.setColor(Color.parseColor("#D59B55"));
        range7.setFrom(60.0);
        range7.setTo(70.0);

        Range range8 = new Range();
        range8.setColor(Color.parseColor("#E1611C"));
        range8.setFrom(70.0);
        range8.setTo(80.0);


        Range range9 = new Range();
        range9.setColor(Color.parseColor("#DD4415"));
        range9.setFrom(80.0);
        range9.setTo(90.0);

        Range range10 = new Range();
        range10.setColor(Color.parseColor("#FF0404"));
        range10.setFrom(90.0);
        range10.setTo(150.0);

        halfGauge.addRange(range1);
        halfGauge.addRange(range2);
        halfGauge.addRange(range3);
        halfGauge.addRange(range4);
        halfGauge.addRange(range5);
        halfGauge.addRange(range6);
        halfGauge.addRange(range7);
        halfGauge.addRange(range8);
        halfGauge.addRange(range9);
        halfGauge.addRange(range10);
        // Add your other ranges here...
    }

        private void updateStressRate(double stressRate) {
            halfGauge.setValue((int) stressRate);
            if (stressRate == 0) {
                statusUpdate.setText("Wear your device...");
                statusUpdate.setTextColor(Color.BLACK);
            } else if (stressRate <= 70) {
                statusUpdate.setText("Normal");
                statusUpdate.setTextColor(Color.BLUE);
            } else {
                statusUpdate.setText("Emergency");
                statusUpdate.setTextColor(Color.RED);
            }
        }

        @Override
        public void onAverageDataCalculated(double averageStressLevel) {
            this.averageStressLevel = averageStressLevel;
        }

        @Override
        public void onNoStressLevelFound() {
            Toast.makeText(this, "No stress level data found", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
