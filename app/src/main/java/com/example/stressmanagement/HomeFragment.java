package com.example.stressmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;

public class HomeFragment extends Fragment implements FirebaseHandler.FirebaseListener {

    private GraphView graph;
    private TextView valueOfStressRate , textViewname;
    private FirebaseHandler firebaseHandler;
    private LineGraphSeries<DataPoint> series;
    private int dataCount = 0;
    private LinearLayout stressLayer;
    private TextView greetingTextView;
    private String userId; // User ID obtained from Firebase Authentication

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        graph = view.findViewById(R.id.linegraphStress);
        valueOfStressRate = view.findViewById(R.id.valueofstressrate);
        stressLayer = view.findViewById(R.id.stressLayer); // Change to your LinearLayout ID

        greetingTextView = view.findViewById(R.id.textViewgood);
        textViewname  = view.findViewById(R.id.textViewname);




        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        // Set the appropriate greeting message based on the current time
        if (hourOfDay >= 3 && hourOfDay < 12) {
            greetingTextView.setText("Good Morning");
        } else if (hourOfDay >= 12 && hourOfDay < 15) {
            greetingTextView.setText("Good Afternoon");
        } else if (hourOfDay >= 15 && hourOfDay < 21) {
            greetingTextView.setText("Good Evening");
        } else {
            greetingTextView.setText("Good Night");
        }

        // Set click listener for the LinearLayout
        stressLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SpeedoMeter.class);
                startActivity(intent);
            }
        });

        // Obtain user ID from Firebase Authentication
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize FirebaseHandler with the listener and userId
        firebaseHandler = new FirebaseHandler(this, userId);

        // Setup GraphView
        setupGraph();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        firebaseHandler = new FirebaseHandler(this, userId);
        firebaseHandler.startListening();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop listening to Firebase updates when fragment is destroyed
        firebaseHandler.stopListening();
    }

    private void setupGraph() {

        // Create new series
        series = new LineGraphSeries<>();
        // Add series to graph
        graph.addSeries(series);

        // Set manual Y bounds to ensure that the graph displays all data points
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);

    }

    @Override
    public void onStressLevelChanged(Long stressLevel) {
        if (stressLevel != null) {
            // Update UI with new stress level
            valueOfStressRate.setText(String.valueOf(stressLevel));
            addDataPoint(stressLevel);
        }
    }

    @Override
    public void onError(String message) {

    }

    private void addDataPoint(Long stressLevel) {
        // Add new data point to the series
        series.appendData(new DataPoint(dataCount++, stressLevel), true, 100);
        // Refresh graph
        graph.onDataChanged(true, true);
    }
    @Override
    public void onNameChanged(String name) {
        textViewname.setText(name); // Assuming greetingTextView is the TextView where you want to display the name
    }
}