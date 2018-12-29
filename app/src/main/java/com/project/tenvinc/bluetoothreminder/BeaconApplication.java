package com.project.tenvinc.bluetoothreminder;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.Preference;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.project.tenvinc.bluetoothreminder.interfaces.IRefresh;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.project.tenvinc.bluetoothreminder.SettingsActivity.GeneralPreferenceFragment.PREF_KEY_SWITCH_BG;
import static com.project.tenvinc.bluetoothreminder.SettingsActivity.GeneralPreferenceFragment.PREF_KEY_SWITCH_FG;
import static com.project.tenvinc.bluetoothreminder.SettingsActivity.GeneralPreferenceFragment.PREF_KEY_SWITCH_SCAN;
import static com.project.tenvinc.bluetoothreminder.SettingsActivity.NotificationPreferenceFragment.PREF_KEY_NOTIF_FREQUENCY;
import static com.project.tenvinc.bluetoothreminder.SettingsActivity.NotificationPreferenceFragment.PREF_KEY_SWITCH_NOTIF;
import static com.project.tenvinc.bluetoothreminder.SettingsActivity.ScanPreferenceFragment.PREF_KEY_BG_DELAY;
import static com.project.tenvinc.bluetoothreminder.SettingsActivity.ScanPreferenceFragment.PREF_KEY_BG_PERIOD;
import static com.project.tenvinc.bluetoothreminder.SettingsActivity.ScanPreferenceFragment.PREF_KEY_FG_DELAY;
import static com.project.tenvinc.bluetoothreminder.SettingsActivity.ScanPreferenceFragment.PREF_KEY_FG_PERIOD;

public class BeaconApplication extends Application implements BeaconConsumer {

    // To be removed
    public MyBeaconSimulator simulator;

    private static final String IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String TAG = "BeaconApplication";
    private static final Region myRegion = new Region("myRegion", null, null, null);
    private BeaconManager beaconManager;
    private static BeaconApplication instance;
    private BackgroundPowerSaver backgroundPowerSaver;

    public List<Beacon> beacons = new ArrayList<>();
    public List<TrackedBeacon> trackedBeacons;

    private ConstScanNotifHelper constScanNotifHelper;
    private BeaconOorNotifHelper oorNotifHelper;
    private Long waitDuration;
    private Boolean isNotifOn;

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
        //BeaconManager.setBeaconSimulator(simulator);

        constScanNotifHelper = new ConstScanNotifHelper(this);
        NotificationCompat.Builder constBuilder = constScanNotifHelper.getChannelNotificationBuilder();

        oorNotifHelper = new BeaconOorNotifHelper(this);

        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);

        waitDuration = Long.parseLong(sharedPreferences.getString(PREF_KEY_NOTIF_FREQUENCY, "-1"));


        if (sharedPreferences.getBoolean(PREF_KEY_SWITCH_FG, false)) {

            Long fgDelay = Long.parseLong(sharedPreferences.getString(PREF_KEY_BG_DELAY, "-1"));
            if (fgDelay != -1) {
                beaconManager.setForegroundScanPeriod(fgDelay);
            }
            Long fgPeriod = Long.parseLong(sharedPreferences.getString(PREF_KEY_FG_PERIOD, "-1"));
            if (fgPeriod != -1) {
                beaconManager.setForegroundScanPeriod(fgPeriod);
            }
            beaconManager.enableForegroundServiceScanning(constBuilder.build(), 456);

        } else {

            Long bgPeriod = Long.parseLong(sharedPreferences.getString(PREF_KEY_BG_PERIOD, "-1"));
            if (bgPeriod != -1) {
                beaconManager.setBackgroundScanPeriod(bgPeriod);
            }
            Long bgDelay = Long.parseLong(sharedPreferences.getString(PREF_KEY_BG_DELAY, "-1"));
            if (bgDelay != -1) {
                beaconManager.setBackgroundBetweenScanPeriod(bgDelay);
            }
        }

        beaconManager.bind(this);
        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        stopAllScans();
        startAutoScan();
    }

    public void updateBeaconManagerSettings(Preference preference, Object value) {
        String key = preference.getKey();
        try {
            switch (key) {
                case PREF_KEY_BG_PERIOD:
                    beaconManager.setBackgroundBetweenScanPeriod(Long.parseLong((String) value));
                    beaconManager.updateScanPeriods();
                    break;
                case PREF_KEY_BG_DELAY:
                    beaconManager.setBackgroundScanPeriod(Long.parseLong((String) value));
                    beaconManager.updateScanPeriods();
                    break;
                case PREF_KEY_FG_PERIOD:
                    beaconManager.setForegroundBetweenScanPeriod(Long.parseLong((String) value));
                    beaconManager.updateScanPeriods();
                    break;
                case PREF_KEY_FG_DELAY:
                    beaconManager.setForegroundScanPeriod(Long.parseLong((String) value));
                    beaconManager.updateScanPeriods();
                    break;
                case PREF_KEY_SWITCH_BG:
                    beaconManager.unbind(this);
                    if ((Boolean) value) {
                        beaconManager.disableForegroundServiceScanning();
                    } else {
                        beaconManager.enableForegroundServiceScanning(
                                constScanNotifHelper.getChannelNotificationBuilder().build(), 456);
                    }
                    beaconManager.bind(this);
                    break;
                case PREF_KEY_SWITCH_FG:
                    beaconManager.unbind(this);
                    if ((Boolean) value && key.equals(PREF_KEY_SWITCH_FG)) {
                        beaconManager.enableForegroundServiceScanning(
                                constScanNotifHelper.getChannelNotificationBuilder().build(), 456);
                    } else {
                        beaconManager.disableForegroundServiceScanning();
                    }
                    beaconManager.bind(this);
                    break;
                default:
                    Log.e(TAG, "It should not reach here.");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to range beacons in region" + e.getStackTrace());
        }
        Log.d(TAG, "updateBeaconManagerSettings");
    }

    public void onOffBeaconScan(Preference preference, Object value) {
        if (!preference.getKey().equals(PREF_KEY_SWITCH_SCAN)) {
            return;
        }
        if ((Boolean) value && !beaconManager.isBound(this)) {
            beaconManager.bind(this);
        } else if (!(Boolean) value) {
            beaconManager.unbind(this);
        }
    }

    public void updateNotifSettings(Preference preference, Object value) {
        if (preference.getKey().equals(PREF_KEY_NOTIF_FREQUENCY)) {
            waitDuration = Long.parseLong((String) value);
        } else if (preference.getKey().equals(PREF_KEY_SWITCH_NOTIF)) {
            isNotifOn = (Boolean) value;
        }
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

    public Long getWaitDuration() {
        return waitDuration;
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
                Log.d(TAG, isInRange.toString());
                if (isNotifOn && tb.isNotificationNeeded()) {
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
