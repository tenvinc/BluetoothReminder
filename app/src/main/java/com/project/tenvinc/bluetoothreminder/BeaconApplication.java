package com.project.tenvinc.bluetoothreminder;

import android.app.Application;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
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

    // To be removed
    public MyBeaconSimulator simulator;

    private static final String IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String TAG = "BeaconApplication";
    private static final Region myRegion = new Region("myRegion", null, null, null);
    private BeaconManager beaconManager;
    private static BeaconApplication instance;

    public List<Beacon> beacons = new ArrayList<>();
    public List<TrackedBeacon> trackedBeacons;

    private ConstScanNotifHelper constScanNotifHelper;
    private BeaconOorNotifHelper oorNotifHelper;

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
        simulator = new MyBeaconSimulator();
        BeaconManager.setBeaconSimulator(simulator);

        constScanNotifHelper = new ConstScanNotifHelper(this);
        NotificationCompat.Builder constBuilder = constScanNotifHelper.getChannelNotificationBuilder();
        beaconManager.enableForegroundServiceScanning(constBuilder.build(), 456);
        beaconManager.bind(this);

        oorNotifHelper = new BeaconOorNotifHelper(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        stopAllScans();
        startAutoScan();
    }

    private boolean isAddedToTracked(Beacon toTest) {
        for (TrackedBeacon r : trackedBeacons) {
            if (r.isSameBeaconAs(toTest)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTrackedInRange(TrackedBeacon toTest, Collection<Beacon> beacons) {
        for (Beacon b : beacons) {
            if (toTest.isSameBeaconAs(b)) {
                return true;
            }
        }
        return false;
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
        beaconManager.addRangeNotifier(new AutoNotifier());

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
                if (!isAddedToTracked(b)) {
                    untrackedBeaconsFound.add(b);
                }
            }
            stopAllScans();
            startAutoScan();
            getInstance().beacons = untrackedBeaconsFound;
            context.refresh(untrackedBeaconsFound);
        }
    }

    private class AutoNotifier implements RangeNotifier {

        @Override
        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
            for (TrackedBeacon tb : trackedBeacons) {
                Boolean isInRange = isTrackedInRange(tb, beacons);
                tb.updateState(isInRange);
                if (tb.isNotificationNeeded()) {
                    tb.updateTimer();
                    NotificationCompat.Builder builder = oorNotifHelper.getChannelNotificationBuilder(tb.getBeaconName());
                    oorNotifHelper.sendNotification(builder.build());
                    Log.d(TAG, String.format("Notifying the following ==== %s", tb.toString()));
                }
                Log.d(TAG, tb.toString());
            }
        }
    }
}
