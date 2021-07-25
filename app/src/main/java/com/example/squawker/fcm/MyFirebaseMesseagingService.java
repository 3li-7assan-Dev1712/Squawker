package com.example.squawker.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.squawker.MainActivity;
import com.example.squawker.R;
import com.example.squawker.data.MyContract;
import com.example.squawker.provider.SquawkerContract;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMesseagingService extends FirebaseMessagingService {
    private static final String SQUACKER_NOTIFICATOIN_ID_STRING = "id";
    private static final int SQUAWKER_NOTIFICATION_ID_INTEGER = 1293;

    @Override
    public void onNewToken(@NonNull String s) {
        Log.d("Token Refreshed ", s);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("MyFirebaseMessage.class", "Message Received");
        Map<String, String> data = remoteMessage.getData();
        sendNotification(data);
        insertSquawk(data);
        Log.d("MyFirebaseMessage.class", "Message Received");

        // I'm going to show a notification for the user :)
        // hi there I'm Ali Hassan, I'm an Android developer and I've built lots of application
        // so I'm here to help you develop brilliant android apps, stay tuned *_-
       }

    private void sendNotification(Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // build createad the first intent to create a pedning intent so we can open the app through it
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String author = data.get(MyContract.SquawkEntry.COLUMN_AUTHOR);
        String message = data.get(MyContract.SquawkEntry.COLUMN_MESSAGE);
        if (message == null) {
            message = "Null";
        }
        if (message.length() > 30) {
            message = message.substring(0, 30) + "\u2026";
        }
        NotificationManager notificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // because from android 10 will not show notification without channel we create it to add it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(SQUACKER_NOTIFICATOIN_ID_STRING,
                    getApplicationContext().getResources().getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),
                SQUACKER_NOTIFICATOIN_ID_STRING
        ).setContentTitle(author)
                .setColor(getApplicationContext().getResources().getColor(R.color.colorAccent))
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_duck);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(SQUAWKER_NOTIFICATION_ID_INTEGER, builder.build());

    }

    private void insertSquawk ( final Map<String, String> data){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ContentValues values2 = new ContentValues();
                values2.put(SquawkerContract.COLUMN_DATE, data.get(MyContract.SquawkEntry.COLUMN_DATE));
                values2.put(SquawkerContract.COLUMN_AUTHOR_KEY, data.get(MyContract.SquawkEntry.COLUMN_AUTHOR_KEY));
                values2.put(SquawkerContract.COLUMN_AUTHOR, data.get(MyContract.SquawkEntry.COLUMN_AUTHOR));
                values2.put(SquawkerContract.COLUMN_MESSAGE, data.get(MyContract.SquawkEntry.COLUMN_MESSAGE));
                getContentResolver().insert(MyContract.SquawkEntry.CONTENT_URI, values2);
                return null;
            }
        };
        task.execute();
    }
}
