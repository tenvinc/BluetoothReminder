package com.project.tenvinc.bluetoothreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button scanBtn;
    private Button listTrackedBtn;

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
    }
}
