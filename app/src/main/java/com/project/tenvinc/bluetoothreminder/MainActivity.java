package com.project.tenvinc.bluetoothreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.altbeacon.beacon.Beacon;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Button scanBtn;
    private Button listTrackedBtn;
    private Button settingsBtn;

    // Todo: to be removed (For simulation only)
    private Button removeBtn;
    private Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanningActivity.class);
                startActivity(intent);
            }
        });

        listTrackedBtn = findViewById(R.id.listTrackedBtn);
        listTrackedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListTrackedActivity.class);
                startActivity(intent);
            }
        });

        // Todo: to be removed (For simulation only)
        removeBtn = findViewById(R.id.remove);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeaconApplication.getInstance().simulator.beacons.remove(6);
            }
        });

        settingsBtn = findViewById(R.id.settings);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        addBtn = findViewById(R.id.add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Beacon newBeacon = new Beacon.Builder()
                        .setId1("6fb0e0e9-2ae6-49d3-bba3-3cb7698c77e2")
                        .setId2(Integer.toString(6))
                        .setId3(Integer.toString(6))
                        .setManufacturer(0x0000)
                        .setTxPower(-59)
                        .setDataFields(Arrays.asList(0l))
                        .build();
                BeaconApplication.getInstance().simulator.beacons.add(6, newBeacon);
            }
        });
    }
}
