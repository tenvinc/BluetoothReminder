package com.project.tenvinc.bluetoothreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.project.tenvinc.bluetoothreminder.interfaces.IListListener;

import java.util.List;

public class ListTrackedActivity extends AppCompatActivity {

    private static final String TAG = "ListTrackedActivity";
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

        setupActionBar();
    }

    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("List tracked beacons");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackedAdapter.setData(BeaconApplication.getInstance().trackedBeacons.getList());
        trackedAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
