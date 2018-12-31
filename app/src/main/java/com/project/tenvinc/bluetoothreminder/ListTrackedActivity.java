package com.project.tenvinc.bluetoothreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.project.tenvinc.bluetoothreminder.interfaces.IListListener;

import java.util.List;

public class ListTrackedActivity extends AppCompatActivity {

    private ListView trackedList;
    private Button returnBtn;
    private TrackedAdapter trackedAdapter;
    public static String TRACKED_BEACON_STORAGE = "TB storage";
    public static String KEY_TRACKED_BEACONS = "tracked beacons";

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

        BeaconApplication.getInstance().trackedBeacons.addListeners(new IListListener<TrackedBeacon>() {
            @Override
            public void trigger(List<TrackedBeacon> list) {
                trackedAdapter.setData(list);
                trackedAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackedAdapter.setData(BeaconApplication.getInstance().trackedBeacons.getList());
        trackedAdapter.notifyDataSetChanged();
    }
}
