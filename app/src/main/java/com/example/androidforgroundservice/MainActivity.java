package com.example.androidforgroundservice;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidforgroundservice.utils.LocationUtil;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button start, stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.btn_start);
        stop = findViewById(R.id.btn_stop);
        start.setOnClickListener(this::serviceStart);
        stop.setOnClickListener(this::serviceStop);
    }

    private void serviceStart(View view) {
        String[] strings = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        mPermissionResult.launch(strings);
    }

    private void serviceStop(View view) {
        LocationUtil.stopLocationService(MainActivity.this);
    }

    private final ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        boolean grantedAllPermission = true;
        for (Map.Entry<String, Boolean> entry : result.entrySet()) {
            if (!entry.getValue()) {
                grantedAllPermission = false;
            }
        }
        if (grantedAllPermission) {
            LocationUtil.startLocationService(MainActivity.this);
        } else {
            LocationUtil.stopLocationService(MainActivity.this);
        }
    });
}