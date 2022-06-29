package com.example.androidforgroundservice.service;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.androidforgroundservice.R;
import com.example.androidforgroundservice.utils.Constraint;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class LocationService extends Service {
    Timer standTimer;
    int count = 0;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (Objects.nonNull(locationResult) && Objects.nonNull(locationResult.getLastLocation())) {
                Double latitude = locationResult.getLastLocation().getLatitude();
                Double longitude = locationResult.getLastLocation().getLongitude();
                Toast.makeText(LocationService.this, "Lat:: " + latitude + " \n" + "Long:: " + longitude, Toast.LENGTH_SHORT).show();
                Log.d("SERVICE RUNNING:: ", "Lat:: " + latitude + " \n" + "Long:: " + longitude);
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      //  startWork();
        startWork2(intent);
        return START_STICKY;
    }

    private void startWork2(Intent intent) {
        if (Objects.nonNull(intent)) {
            String action = intent.getAction();
            if (Objects.nonNull(action)) {
                if (action.equals(Constraint.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
                    Log.d("SERVICE :: "," START");

                } else if (action.equals(Constraint.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                    Log.d("SERVICE :: "," STOP");

                }
            }
        }
    }

    private void startWork() {
        if (standTimer != null) {

            standTimer.cancel();
            standTimer = null;
        }

        standTimer = new Timer();
        standTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                count++;
                sendNotification();
            }
        }, 1000, 4000);
    }

    private void sendNotification() {
        String NOTIFICATION_CHANNEL_ID = "Nilesh_channel 2";
        long pattern[] = {0, 1000, 500, 1000};

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Your Notifications",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(pattern);
            notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        // to diaplay notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
            channel.canBypassDnd();
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder
                .setColor(ContextCompat.getColor(this, R.color.black))
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Hi there " + count)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText("Hi"))
                .setAutoCancel(true)
        ;


        mNotificationManager.notify(1000, notificationBuilder.build());
        startForeground(536, notificationBuilder.build());
     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(536, notificationBuilder.build(),
                    FOREGROUND_SERVICE_TYPE_LOCATION);
        }*/
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("MissingPermission")
    private void startLocationService() {
        final String channelId = "location notification channel";
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);

        builder.setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle("Location service")
        .setContentText("Running")
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setContentIntent(pendingIntent)
        .setAutoCancel(false)
        .setPriority(NotificationCompat.PRIORITY_MAX);

        long pattern[] = {0, 1000, 500, 1000};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Objects.nonNull(notificationManager) && Objects.isNull(notificationManager.getNotificationChannel(channelId))) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId, "Location service", NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This channel is used by location service");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(pattern);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        // to diaplay notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            channel.canBypassDnd();
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    //    startForeground(Constraint.LOCATION_SERVICE_ID, builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(536, builder.build(),
                    FOREGROUND_SERVICE_TYPE_LOCATION);
        }
    }

    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (standTimer != null) {

            standTimer.cancel();
            standTimer = null;

        }
    }
}
