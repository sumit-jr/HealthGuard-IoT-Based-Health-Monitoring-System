package com.example.stressmanagement;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class userAdapter extends RecyclerView.Adapter<userAdapter.viewholder> {
    Context context;
    ArrayList<Users> usersArrayList;

    public userAdapter(Context context, ArrayList<Users> usersArrayList) {
        this.context = context;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        Users users = usersArrayList.get(position);
        holder.username.setText(users.userName);
        Picasso.get().load(users.profilepic).into(holder.userimg);

        if (users.getstress_level() != null ) {
            long stressLevelLong = users.getstress_level();
            int stressLevel = (int) stressLevelLong;
            String status;
            int color;

            if (stressLevel >= 0 && stressLevel < 50) {
                status = "Good";
                color = context.getResources().getColor(R.color.branjal); // Change to your color resource
            } else if (stressLevel >= 50 && stressLevel < 75) {
                status = "Normal";
                color = context.getResources().getColor(R.color.green); // Change to your color resource
            } else {
                status = "High";
                color = context.getResources().getColor(R.color.ten); // Change to your color resource
            }
            Log.d("StatusDebug", "Status: " + status);
            holder.userStausFinal.setText(status);
            holder.userStausFinal.setTextColor(color);
        } else {
            Log.d("StatusDebug", "stress_level is null or empty");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context != null) {
                    Intent intent = new Intent(context, chatWindow.class);
                    intent.putExtra("nameeee", users.getUserName());
                    intent.putExtra("reciverImg", users.getProfilepic());
                    intent.putExtra("uid", users.getUserId());
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        CircleImageView userimg;
        TextView username, userStausFinal;


        public viewholder(@NonNull View itemView) {
            super(itemView);
            userimg = itemView.findViewById(R.id.userimg);
            username = itemView.findViewById(R.id.username);
            userStausFinal = itemView.findViewById(R.id.userStausFinal);
        }
    }
}
