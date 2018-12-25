package com.project.tenvinc.bluetoothreminder;

import android.app.Application;
import android.os.RemoteException;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BeaconApplication extends Application implements BeaconConsumer {

    public static final String IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String TAG = "BeaconApplication";
    private static BeaconApplication instance = new BeaconApplication();
    public List<Beacon> beacons = new ArrayList<>();
    public List<TrackedAdapter.TrackRecord> trackedBeacons = new ArrayList<>();
    private BeaconManager beaconManager;

    public static BeaconApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_LAYOUT));
        beaconManager.bind(this);
        BeaconManager.setBeaconSimulator(new MyBeaconSimulator());
    }


    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                List<Beacon> beaconList = new ArrayList<>();
                for (Beacon b : beacons) {
                    if (!isTracked(b)) {
                        beaconList.add(b);
                    }
                }
                getInstance().beacons = beaconList;
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {

        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        beaconManager.unbind(this);
    }


    private boolean isTracked(Beacon toTest) {
        Boolean isTracked = false;
        for (TrackedAdapter.TrackRecord r : getInstance().trackedBeacons) {
            if (toTest.getId1().toString().equals(r.getUuid()) &&
                    toTest.getId2().toString().equals(r.getMinor()) &&
                    toTest.getId3().toString().equals(r.getMajor())) {
                isTracked = true;
                break;
            }
        }
        return isTracked;
    }
}
