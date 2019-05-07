package com.group5.atoms;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class AddEventActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = findViewById(R.id.add_event_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


}
