package com.project.tenvinc.bluetoothreminder;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
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

        validatePermissions(this);
    }

    private void validatePermissions(final Activity activity) {
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
            builder.setTitle("This app requires location access");
            builder.setMessage("Please grant location access so this app can detect beacons.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });
                    builder.show();
                }
                return;
            default:
                Log.e(TAG, "Something has gone wrong");
        }
    }

    @Override
    public void applyTexts(String name, String uuid, String minor, String major) {
        TrackedAdapter.TrackRecord newBeacon = new TrackedAdapter.TrackRecord(uuid, major, minor,
                name);
        BeaconApplication.getInstance().trackedBeaconRecord.add(newBeacon);
        BeaconApplication.getInstance().startManualRangingScan(this);
    }

    @Override
    public void refresh(List<Beacon> data) {
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();
    }
}
