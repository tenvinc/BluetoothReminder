package com.project.tenvinc.bluetoothreminder;

import com.project.tenvinc.bluetoothreminder.interfaces.IListListener;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UniqueTrackedBeaconList implements BeaconApplication.ObservableListWrapper<TrackedBeacon> {

    private List<TrackedBeacon> trackedBeacons;
    private List<IListListener<TrackedBeacon>> listeners;

    public UniqueTrackedBeaconList() {
        trackedBeacons = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public UniqueTrackedBeaconList(List<TrackedBeacon> trackedBeacons) {
        this.trackedBeacons = trackedBeacons;
        listeners = new ArrayList<>();
    }

    public void add(TrackedBeacon trackedBeacon) {
        if (isInList(trackedBeacon) < 0) {
            trackedBeacons.add(trackedBeacon);
        }
    }

    public void edit(int index, TrackedBeacon trackedBeacon) {
        trackedBeacons.remove(index);
        trackedBeacons.add(index, trackedBeacon);
    }

    public TrackedBeacon getTrackedBeacon(int index) {
        TrackedBeacon copy = new TrackedBeacon(trackedBeacons.get(index));
        return copy;
    }

    private void remove(int index) {
        trackedBeacons.remove(index);
    }

    public void remove(String beaconName) {
        int index = findIndexOf(beaconName);
        remove(index);
    }

    public int findIndexOf(String name) {
        for (int i = 0; i < trackedBeacons.size(); i++) {
            if (trackedBeacons.get(i).getBeaconName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private int isInList(TrackedBeacon toTest) {
        for (int i = 0; i < trackedBeacons.size(); i++) {
            if (trackedBeacons.get(i).isSameNameAs(toTest)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isInList(Beacon toTest) {
        for (int i = 0; i < trackedBeacons.size(); i++) {
            if (trackedBeacons.get(i).isSameBeaconAs(toTest)) {
                return true;
            }
        }
        return false;
    }

    public int getSize() {
        return trackedBeacons.size();
    }

    public List<TrackedBeacon> getList() {
        return Collections.unmodifiableList(trackedBeacons);
    }

    public void setList(List<TrackedBeacon> trackedBeacons) {
        this.trackedBeacons = trackedBeacons;
    }

    /* ======================== Listener methods =================================================*/

    @Override
    public void triggerListener() {
        for (IListListener listener : listeners) {
            listener.trigger(trackedBeacons);
        }
    }

    @Override
    public void addListeners(IListListener<TrackedBeacon> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(IListListener<TrackedBeacon> listener) {
        listeners.remove(listener);
    }

    @Override
    public void removeAllListeners() {
        listeners.clear();
    }
}
