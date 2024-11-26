package com.example.stressmanagement;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {
  private Button logoutButton;
    private ImageView profileImageView ,setting ;
    private TextView profileNameTextView , Addreshtextview , mailtextview ,statussetup;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_profile, container, false);


            mAuth = FirebaseAuth.getInstance();
            storage = FirebaseStorage.getInstance();
            profileImageView = rootView.findViewById(R.id.profilesetup);
            logoutButton = rootView.findViewById(R.id.logoutButton);
            profileNameTextView = rootView.findViewById(R.id.profileName22);
            Addreshtextview =  rootView.findViewById(R.id.AddressofUser);
            mailtextview = rootView.findViewById(R.id.emailOfUser);
            statussetup = rootView.findViewById(R.id.statussetup);
            setting = rootView.findViewById(R.id.changeSetting);
             setting.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     Intent intent =new Intent(requireActivity(),Setting.class);
                     startActivity(intent);
                 }
             });

            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });
            loadProfile();

            return rootView;
        }

    private void loadProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve user data
                        String userName = dataSnapshot.child("userName").getValue(String.class);
                        String addresh = dataSnapshot.child("addresh").getValue(String.class);
                        String mail = dataSnapshot.child("mail").getValue(String.class);
                        String profileImageUrl = dataSnapshot.child("profilepic").getValue(String.class);
                        Long stressLevel = dataSnapshot.child("stress_level").getValue(Long.class);

                        // Set user name, address, and email

                        // Set user name
                        profileNameTextView.setText(userName);
                        Addreshtextview.setText(addresh);
                        mailtextview.setText(mail);

                        // Load profile image using Picasso
                        Picasso.get().load(profileImageUrl).into(profileImageView);
                        if (stressLevel != null) {
                            int stressLevelValue = stressLevel.intValue();
                            String status;
                            int color;

                            if (stressLevelValue >= 0 && stressLevelValue < 50) {
                                status = "Good";
                                color = getResources().getColor(R.color.branjal); // Change to your color resource
                            } else if (stressLevelValue >= 50 && stressLevelValue < 75) {
                                status = "Normal";
                                color = getResources().getColor(R.color.green); // Change to your color resource
                            } else {
                                status = "High";
                                color = getResources().getColor(R.color.ten);

                            }



                            statussetup.setText(status);
                            statussetup.setTextColor(color);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle potential errors
                }
            });
        }
    }

    private void showDialog() {
        Dialog dialog = new Dialog(requireActivity(), R.style.dialoge);
        dialog.setContentView(R.layout.dialoge_layout);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT; // Adjust width as needed
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; // Adjust height as needed
        params.gravity = Gravity.CENTER; // Center the dialog box
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white); // Set background color
        Button no = dialog.findViewById(R.id.nobnt);
        Button yes = dialog.findViewById(R.id.yesbnt);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireActivity(), Login.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();


            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();




        }
    }

