package com.example.stressmanagement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;


        public class ClinicFragment extends Fragment {

            // Constructor
            public ClinicFragment() {
                // Required empty public constructor
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                // Inflate the layout for this fragment
                View view = inflater.inflate(R.layout.fragment_clinic, container, false);

                // Get the ListView reference
                ListView listView = view.findViewById(R.id.yogaSuggestionsListView);

                // Define an array of yoga suggestions
                String[] suggestions = {
                        "Try high-stress yoga poses like inversions and backbends to release tension and promote relaxation.\n" +
                                "",
                        "Practice deep breathing exercises such as pranayama to calm the mind and reduce stress levels.\n",
                        "Incorporate meditation into your yoga routine to enhance mindfulness and alleviate stress.",

                };

                // Create an ArrayAdapter to populate the ListView with suggestions
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, suggestions);

                // Set the adapter for the ListView
                listView.setAdapter(adapter);

                return view;
            }
        }