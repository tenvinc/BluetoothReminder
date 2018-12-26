package com.project.tenvinc.bluetoothreminder;

import android.app.Application;
import android.os.RemoteException;
import android.util.Log;

import com.project.tenvinc.bluetoothreminder.interfaces.IRefresh;

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

    private static final String IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String TAG = "BeaconApplication";
    private static final Region myRegion = new Region("myRegion", null, null, null);
    public List<Beacon> beacons = new ArrayList<>();
    private BeaconManager beaconManager;
    private static BeaconApplication instance;
    public List<TrackedBeacon> trackedBeacons;

    public static BeaconApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        trackedBeacons = new ArrayList<>();
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_LAYOUT));
        beaconManager.bind(this);
        BeaconManager.setBeaconSimulator(new MyBeaconSimulator());
    }

    @Override
    public void onBeaconServiceConnect() {
        stopAllScans();
        startAutoScan();
    }

    private boolean isAddedToTracked(Beacon toTest) {
        Boolean isTracked = true;
        for (TrackedBeacon r : trackedBeacons) {
            if (toTest.getId1().toString().equals(r.getUuid()) &&
                    toTest.getId2().toString().equals(r.getMinor()) &&
                    toTest.getId3().toString().equals(r.getMajor())) {
                isTracked = false;
                break;
            }
        }
        return isTracked;
    }

    private void stopAllScans() {
        try {
            beaconManager.stopRangingBeaconsInRegion(myRegion);
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to range beacons in region" + e.getStackTrace());
        }
        beaconManager.removeAllRangeNotifiers();
    }

    public void startManualRangingScan(IRefresh context) {
        stopAllScans();
        ManualNotifier notifier = new ManualNotifier(context);
        beaconManager.addRangeNotifier(notifier);
        try {
            beaconManager.startRangingBeaconsInRegion(myRegion);
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to range beacons in region" + e.getStackTrace());
        }
    }

    private void startAutoScan() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.d(TAG, "just did a scan on ranging");
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(myRegion);
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to range beacons in region" + e.getStackTrace());
        }
    }

    /**
     * This class receives a context when it is first created. It is a variant of RangeNotifier which does only a single
     * scan before implementing a callback to the context. Requires context to implement IRefresh.z
     */
    private class ManualNotifier implements RangeNotifier {
        private IRefresh context;

        public ManualNotifier(IRefresh context) {
            this.context = context;
        }

        @Override
        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
            List<Beacon> untrackedBeaconsFound = new ArrayList<>();
            for (Beacon b : beacons) {
                if (isAddedToTracked(b)) {
                    untrackedBeaconsFound.add(b);
                }
            }
            stopAllScans();
            startAutoScan();
            getInstance().beacons = untrackedBeaconsFound;
            context.refresh(untrackedBeaconsFound);
        }
    }
}
