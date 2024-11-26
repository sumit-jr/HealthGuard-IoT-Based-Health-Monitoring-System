package com.example.stressmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Setting extends AppCompatActivity {


        ImageView setprofile;
        EditText setname, setaddresh;
        Button donebut;
        FirebaseAuth auth;
        FirebaseDatabase database;
        FirebaseStorage storage;
        Uri setImageUri;
        ProgressDialog progressDialog;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_setting);

            auth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();
            storage = FirebaseStorage.getInstance();
            setprofile = findViewById(R.id.settingprofile);
            setname = findViewById(R.id.settingname);
            setaddresh = findViewById(R.id.settingaddress);
            donebut = findViewById(R.id.donebutt);

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Saving...");
            progressDialog.setCancelable(false);

            DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
            StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("userName").getValue(String.class);
                        String profile = snapshot.child("profilepic").getValue(String.class);
                        String addresh = snapshot.child("addresh").getValue(String.class);

                        if (name != null && profile != null && addresh != null) {
                            setname.setText(name);
                            setaddresh.setText(addresh);
                            Picasso.get().load(profile).into(setprofile);
                        } else {
                            // Handle the case when one or more values are null
                            Toast.makeText(Setting.this, "One or more values are null", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            setprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
                }
            });

            donebut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.show();

                    String name = setname.getText().toString();
                    String addresh = setaddresh.getText().toString();

                    DatabaseReference userReference = database.getReference().child("user").child(auth.getUid());

                    if (setImageUri != null) {
                        // Upload image and update profile pic
                        storageReference.putFile(setImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String finalImageUri = uri.toString();
                                            userReference.child("profilepic").setValue(finalImageUri)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(Setting.this, "Profile Picture updated", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(Setting.this, "Failed to update Profile Picture", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(Setting.this, "Failed to upload Image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    // Update name and addresh
                    userReference.child("userName").setValue(name);
                    userReference.child("addresh").setValue(addresh)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Setting.this, "Data Is saved", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Setting.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(Setting.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 10 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                setImageUri = data.getData();
                setprofile.setImageURI(setImageUri);
            }
        }}



