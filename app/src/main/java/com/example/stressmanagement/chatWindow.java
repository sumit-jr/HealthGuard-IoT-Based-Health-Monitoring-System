package com.example.stressmanagement;

import static com.example.stressmanagement.R.id;
import static com.example.stressmanagement.R.layout;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatWindow extends AppCompatActivity {

    String reciverimg, reciverUid,reciverName,SenderUID;
    CircleImageView profile;

    TextView reciverNName, statusOfChat;

    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    public  static String senderImg;
    public  static String reciverIImg;
    CardView sendbtn;
    EditText textmsg;

    String senderRoom,reciverRoom;
    RecyclerView messageAdpter;
    ArrayList<msgModelclass> messagesArrayList;
    messagesAdpter mmessagesAdpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_chatwindow);
        profile = findViewById(id.profileimgg);
        reciverNName = findViewById(id.recivername);
        statusOfChat = findViewById(id.statusOfChat);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        reciverName = getIntent().getStringExtra("nameeee");
       reciverimg = getIntent().getStringExtra("reciverImg");
        reciverUid = getIntent().getStringExtra("uid");


        Picasso.get().load(reciverimg).into(profile);


         messagesArrayList = new ArrayList<>();

                sendbtn = findViewById(id.sendbtnn);
                textmsg = findViewById(id.textmsg);
                reciverNName = findViewById(id.recivername);
                profile = findViewById(id.profileimgg);
                messageAdpter = findViewById(id.msgadpter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setStackFromEnd(true);
                messageAdpter.setLayoutManager(linearLayoutManager);
                mmessagesAdpter = new messagesAdpter(chatWindow.this,messagesArrayList);
                messageAdpter.setAdapter(mmessagesAdpter);


                Picasso.get().load(reciverimg).into(profile);
                reciverNName.setText(""+reciverName);

                SenderUID =  firebaseAuth.getUid();

                senderRoom = SenderUID+reciverUid;
                reciverRoom = reciverUid+SenderUID;



                DatabaseReference  reference = database.getReference().child("user").child(reciverUid);
                DatabaseReference  chatreference = database.getReference().child("chats").child(senderRoom).child("messages");



        chatreference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesArrayList.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
                            messagesArrayList.add(messages);
                        }
                        mmessagesAdpter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        senderImg= snapshot.child("profilepic").getValue().toString();
                        reciverIImg=reciverimg;


                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Check if the stress_level field exists for the receiver
                    if (dataSnapshot.hasChild("stress_level")) {
                        Long stressLevel = (Long) dataSnapshot.child("stress_level").getValue();
                        int color;
                        // You can adjust this threshold as needed
                        if (stressLevel != null && stressLevel < 50) {
                            statusOfChat.setText("Stress:cool");
                            color = chatWindow.this.getResources().getColor(R.color.branjal);
                            statusOfChat.setTextColor(color);
                        } else if(stressLevel<50) {
                            statusOfChat.setText("Stress:Normal");
                            color = chatWindow.this.getResources().getColor(R.color.green);
                            statusOfChat.setTextColor(color);
                        }else {
                            statusOfChat.setText("Stress:Emergency");
                            color = chatWindow.this.getResources().getColor(R.color.ten);
                            statusOfChat.setTextColor(color);

                        }
                    } else {
                        statusOfChat.setText("Wear you Device..");
                    }
                }
            }



                @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
                 sendbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String message = textmsg.getText().toString();
                        if (message.isEmpty()){
                            Toast.makeText(chatWindow.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        textmsg.setText("");
                        Date date = new Date();
                        msgModelclass messagess = new msgModelclass(message,SenderUID,date.getTime());

                        database=FirebaseDatabase.getInstance();
                        database.getReference().child("chats")
                                .child(senderRoom)
                                .child("messages")
                                .push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        database.getReference().child("chats")
                                                .child(reciverRoom)
                                                .child("messages")
                                                .push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                    }
                                                });
                                    }
                                });
                    }
                });

            }

        }

