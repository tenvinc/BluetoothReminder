package com.project.tenvinc.bluetoothreminder;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.tenvinc.bluetoothreminder.exceptions.DuplicateNameException;
import com.project.tenvinc.bluetoothreminder.interfaces.IListListener;
import com.project.tenvinc.bluetoothreminder.interfaces.IRefresh;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.preference.PreferenceManager.KEY_HAS_SET_DEFAULT_VALUES;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.project.tenvinc.bluetoothreminder.activities.ListTrackedActivity.KEY_TRACKED_BEACONS;
import static com.project.tenvinc.bluetoothreminder.activities.ListTrackedActivity.TRACKED_BEACON_STORAGE;
import static com.project.tenvinc.bluetoothreminder.activities.SettingsActivity.GeneralPreferenceFragment.PREF_KEY_SWITCH_BG;
import static com.project.tenvinc.bluetoothreminder.activities.SettingsActivity.GeneralPreferenceFragment.PREF_KEY_SWITCH_FG;
import static com.project.tenvinc.bluetoothreminder.activities.SettingsActivity.GeneralPreferenceFragment.PREF_KEY_SWITCH_SCAN;
import static com.project.tenvinc.bluetoothreminder.activities.SettingsActivity.NotificationPreferenceFragment.PREF_KEY_NOTIF_FREQUENCY;
import static com.project.tenvinc.bluetoothreminder.activities.SettingsActivity.NotificationPreferenceFragment.PREF_KEY_SWITCH_NOTIF;
import static com.project.tenvinc.bluetoothreminder.activities.SettingsActivity.NotificationPreferenceFragment.PREF_KEY_SWITCH_VIBRATION;
import static com.project.tenvinc.bluetoothreminder.activities.SettingsActivity.ScanPreferenceFragment.PREF_KEY_BG_DELAY;
import static com.project.tenvinc.bluetoothreminder.activities.SettingsActivity.ScanPreferenceFragment.PREF_KEY_BG_PERIOD;
import static com.project.tenvinc.bluetoothreminder.activities.SettingsActivity.ScanPreferenceFragment.PREF_KEY_FG_DELAY;
import static com.project.tenvinc.bluetoothreminder.activities.SettingsActivity.ScanPreferenceFragment.PREF_KEY_FG_PERIOD;

public class BeaconApplication extends Application implements BeaconConsumer {

    private static final String IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String TAG = "BeaconApplication";
    private static final Region myRegion = new Region("myRegion", null, null, null);
    private static BeaconApplication instance;
    // To be removed
    public MyBeaconSimulator simulator;
    public List<Beacon> beacons = new ArrayList<>();
    public UniqueTrackedBeaconList trackedBeacons;
    private BeaconManager beaconManager;
    private BackgroundPowerSaver backgroundPowerSaver;

    // Notification settings
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
        initSharedPreferences();
        instance = this;
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_LAYOUT));

        // For debugging purposes
        /*simulator = new MyBeaconSimulator();
        BeaconManager.setBeaconSimulator(simulator);*/

        constScanNotifHelper = new ConstScanNotifHelper(this);
        NotificationCompat.Builder constBuilder = constScanNotifHelper.getChannelNotificationBuilder();

        oorNotifHelper = new BeaconOorNotifHelper(this);

        trackedBeacons = new UniqueTrackedBeaconList(loadTrackedBeacons());

        initSettings(constBuilder);

        beaconManager.bind(this);
        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        stopAllScans();
        startAutoScan();
    }

    public void onDestroy() {
        beaconManager.unbind(this);
    }

    public void onStart() {
        if (!beaconManager.isBound(this)) {
            beaconManager.bind(this);
        }
    }

    /**
     * Only initialises the default shared preferences only if it has not been initialised
     */
    private void initSharedPreferences() {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(KEY_HAS_SET_DEFAULT_VALUES, false)) {
            PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
            PreferenceManager.setDefaultValues(this, R.xml.pref_notification, true);
            PreferenceManager.setDefaultValues(this, R.xml.pref_scan, true);
        }
    }

    public void initSettings(NotificationCompat.Builder builder) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);

        waitDuration = Long.parseLong(sharedPreferences.getString(PREF_KEY_NOTIF_FREQUENCY, null));
        isNotifOn = sharedPreferences.getBoolean(PREF_KEY_SWITCH_NOTIF, true);

        waitDuration = Long.parseLong(sharedPreferences.getString(PREF_KEY_NOTIF_FREQUENCY, "-1"));

        Long bgPeriod = Long.parseLong(sharedPreferences.getString(PREF_KEY_BG_PERIOD, "-1"));
        if (bgPeriod != -1) {
            beaconManager.setBackgroundScanPeriod(bgPeriod);
        }

        Long bgDelay = Long.parseLong(sharedPreferences.getString(PREF_KEY_BG_DELAY, "-1"));
        if (bgDelay != -1) {
            beaconManager.setBackgroundBetweenScanPeriod(bgDelay);
        }

        Long fgPeriod = Long.parseLong(sharedPreferences.getString(PREF_KEY_FG_PERIOD, "-1"));
        if (fgPeriod != -1) {
            beaconManager.setForegroundScanPeriod(fgPeriod);
        }

        Long fgDelay = Long.parseLong(sharedPreferences.getString(PREF_KEY_FG_DELAY, "-1"));
        if (fgDelay != -1) {
            beaconManager.setForegroundBetweenScanPeriod(fgDelay);
        }

        if (sharedPreferences.getBoolean(PREF_KEY_SWITCH_FG, false)) {
            beaconManager.enableForegroundServiceScanning(builder.build(), 456);
        }
    }

    public void updateBeaconManagerSettings(Preference preference, Object value) {
        String key = preference.getKey();
        try {
            switch (key) {
                case PREF_KEY_BG_PERIOD:
                    beaconManager.setBackgroundScanPeriod(Long.parseLong((String) value));
                    beaconManager.updateScanPeriods();
                    break;
                case PREF_KEY_BG_DELAY:
                    beaconManager.setBackgroundBetweenScanPeriod(Long.parseLong((String) value));
                    beaconManager.updateScanPeriods();
                    break;
                case PREF_KEY_FG_PERIOD:
                    beaconManager.setForegroundScanPeriod(Long.parseLong((String) value));
                    beaconManager.updateScanPeriods();
                    break;
                case PREF_KEY_FG_DELAY:
                    beaconManager.setForegroundBetweenScanPeriod(Long.parseLong((String) value));
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
        } else if (preference.getKey().equals(PREF_KEY_SWITCH_VIBRATION)) {
            oorNotifHelper.setVibration((Boolean) value);
        }
    }

    private Beacon findTrackedInRange(TrackedBeacon toTest, Collection<Beacon> beacons) {
        for (Beacon b : beacons) {
            if (toTest.isSameBeaconAs(b)) {
                return b;
            }
        }
        return null;
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

    public void saveTrackedBeacons(List<TrackedBeacon> trackedBeacons) {
        SharedPreferences sharedPreferences = getSharedPreferences(TRACKED_BEACON_STORAGE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(trackedBeacons);
        editor.putString(KEY_TRACKED_BEACONS, json);
        editor.apply();
    }

    public List<TrackedBeacon> loadTrackedBeacons() {
        SharedPreferences sharedPreferences = getSharedPreferences(TRACKED_BEACON_STORAGE, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_TRACKED_BEACONS, null);
        Type type = new TypeToken<ArrayList<TrackedBeacon>>() {
        }.getType();
        ArrayList<TrackedBeacon> data = gson.fromJson(json, type);
        return (data == null) ? new ArrayList<TrackedBeacon>() : data;
    }

    public interface ObservableListWrapper<E> {

        void triggerListener();

        void addListeners(IListListener<E> listener);

        void removeListener(IListListener<E> listener);

        void removeAllListeners();
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
                if (!trackedBeacons.indexOf(b)) {
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
            for (int i = 0; i < trackedBeacons.getSize(); i++) {
                TrackedBeacon tb = trackedBeacons.getTrackedBeacon(i);
                if (!tb.isEnabled()) {
                    continue;
                }
                Beacon beaconInRange = findTrackedInRange(tb, beacons);
                Boolean isInRange;
                isInRange = (beaconInRange != null);
                tb.updateDist(beaconInRange, isInRange);
                tb.updateState(isInRange);
                Log.d(TAG, isInRange.toString());
                if (isNotifOn & tb.isNotificationNeeded()) {
                    tb.updateTimer();
                    NotificationCompat.Builder builder = oorNotifHelper.getChannelNotificationBuilder(tb.getBeaconName());
                    oorNotifHelper.sendNotification(builder.build());
                    Log.d(TAG, String.format("Notifying the following ==== %s", tb.toString()));
                }
                Log.d(TAG, tb.toString());
                try {
                    trackedBeacons.edit(i, tb);
                } catch (DuplicateNameException e) {
                    Log.e(TAG, "This should not happen");
                }
            }
            trackedBeacons.triggerListener();
        }
    }
}
