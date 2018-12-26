package com.project.tenvinc.bluetoothreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class ListTrackedActivity extends AppCompatActivity {

    private ListView trackedList;
    private Button returnBtn;
    private TrackedAdapter trackedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tracked);

        trackedList = findViewById(R.id.trackedList);
        trackedAdapter = new TrackedAdapter(this);
        trackedList.setAdapter(trackedAdapter);

        returnBtn = findViewById(R.id.returnButton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListTrackedActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackedAdapter.setData(BeaconApplication.getInstance().trackedBeacons);
        trackedAdapter.notifyDataSetChanged();
    }
}
