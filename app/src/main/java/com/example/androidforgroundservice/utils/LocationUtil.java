package com.example.androidforgroundservice.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.androidforgroundservice.service.LocationService;

import java.util.Objects;

public class LocationUtil {
    public static boolean isLocationServiceRunning(Activity activity) {
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        if (Objects.nonNull(activityManager)) {
            for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(serviceInfo.service.getClassName())) {
                    if (serviceInfo.foreground) {
                        return true;
                    }
                }
            }
        } else {
            return false;
        }
        return false;
    }

    public static void startLocationService(Activity activity){
        if(!isLocationServiceRunning(activity)){
            Intent intent = new Intent(activity.getApplicationContext(),LocationService.class);
            intent.setAction(Constraint.ACTION_START_LOCATION_SERVICE);
            activity.startService(intent);
            Toast.makeText(activity, "Location service started", Toast.LENGTH_SHORT).show();
        }
    }

    public static void stopLocationService(Activity activity){
        if(isLocationServiceRunning(activity)){
            Intent intent = new Intent(activity.getApplicationContext(),LocationService.class);
            intent.setAction(Constraint.ACTION_STOP_LOCATION_SERVICE);
            activity.startService(intent);
            Toast.makeText(activity, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }
}
