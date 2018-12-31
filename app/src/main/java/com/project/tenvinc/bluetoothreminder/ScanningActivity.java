package com.project.tenvinc.bluetoothreminder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.project.tenvinc.bluetoothreminder.interfaces.IRefresh;

import org.altbeacon.beacon.Beacon;

import java.util.List;

public class ScanningActivity extends AppCompatActivity implements AddTrackedDialog.FavouritesDialogListener, IRefresh {

    private static final String TAG = "RangingActivity";
    private BeaconListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);

        ListView list = findViewById(R.id.list);
        mAdapter = new BeaconListAdapter(this, BeaconApplication.getInstance().beacons);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AddTrackedDialog dialog = new AddTrackedDialog();
                Bundle data = new Bundle();

                Beacon currBeacon = (Beacon) parent.getAdapter().getItem(position);

                data.putString("uuid", currBeacon.getId1().toString());
                data.putString("minor", currBeacon.getId2().toString());
                data.putString("major", currBeacon.getId3().toString());
                data.putInt("position", position);

                dialog.setArguments(data);
                dialog.show(getSupportFragmentManager(), "add to tracked");
            }
        });

        Button refreshBtn = findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IRefresh context = ScanningActivity.this;
                    BeaconApplication.getInstance().startManualRangingScan(context);
                } catch (ClassCastException e) {
                    Log.e(TAG, "Activity referenced does not inherit from IRefresh");
                }
            }
        });
    }

    @Override
    public void applyAddition(Beacon beacon, String beaconName) {
        TrackedBeacon newBeacon = new TrackedBeacon(beacon, beaconName, 10);
        BeaconApplication.getInstance().trackedBeacons.add(newBeacon);
        BeaconApplication.getInstance().startManualRangingScan(this);

        BeaconApplication.getInstance().saveTrackedBeacons(BeaconApplication.getInstance().trackedBeacons);
    }

    @Override
    public void refresh(List<Beacon> data) {
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();
    }
}
