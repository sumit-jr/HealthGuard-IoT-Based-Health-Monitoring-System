package com.example.stressmanagement;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyFirebaseMessagingService extends BroadcastReceiver {




        private static final String TAG = "MyFirebaseService";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Received broadcast");

            String action = intent.getAction();
            if (action != null && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                // Check system online status
                if (isSystemOnline(context)) {
                    Log.d(TAG, "onReceive: System is online");

                    // Retrieve stress levels for all users from Firebase
                    FirebaseDatabase.getInstance().getReference("user").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: DataSnapshot received");

                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                // Retrieve stress level for each user
                                String userId = userSnapshot.child("userId").getValue(String.class);
                                Integer stressLevel = userSnapshot.child("stress_level").getValue(Integer.class);

                                // Check if stress level is above threshold (75)
                                if (stressLevel != null && stressLevel > 75) {
                                    // Trigger alarm alert with notification for this user
                                    Log.d(TAG, "onDataChange: Stress level above threshold for user: " + userId);
                                    triggerAlarmAndNotification(context, userId);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: Error fetching data from Firebase: " + databaseError.getMessage());
                        }
                    });
                } else {
                    Log.d(TAG, "onReceive: System is not online");
                }
            }
        }

        private boolean isSystemOnline(Context context) {
            // Retrieve system connectivity service
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                // Check network connectivity
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            }
            return false;
        }

        private void triggerAlarmAndNotification(Context context, String userId) {
            // Play emergency alarm
            playEmergencyAlarm(context);

            // Implement alarm alert with notification for the user

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Create notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                    .setSmallIcon(R.drawable.baseline_notifications_24)
                    .setContentTitle("High Stress Level Alert")
                    .setContentText("The stress level is above the threshold for user: " + userId)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            // Show notification
            notificationManager.notify(userId.hashCode(), builder.build());
        }

        private void playEmergencyAlarm(Context context) {
            try {
                // Play emergency alarm sound
                Ringtone ringtone = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
                ringtone.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
