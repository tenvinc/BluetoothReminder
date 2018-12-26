package com.project.tenvinc.bluetoothreminder;

import org.altbeacon.beacon.Beacon;

public class TrackedBeacon {

    private long sleepDuration;  // Specify how long beacon notifications will sleep for after a notification in seconds
    private long lastUpdateTime;
    private Beacon beacon;
    private String beaconName;
    private State currState;

    public TrackedBeacon(Beacon beacon, String beaconName, long sleepDuration) {
        this.beacon = beacon;
        this.beaconName = beaconName;
        this.sleepDuration = sleepDuration;
        currState = State.UNKNOWN;
        lastUpdateTime = -1;  //Time is not updated until first notification
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public String getUuid() {
        return beacon.getId1().toString();
    }

    public String getMajor() {
        return beacon.getId2().toString();
    }

    public String getMinor() {
        return beacon.getId3().toString();
    }

    public String getBeaconName() {
        return beaconName;
    }

    public enum State {
        UNKNOWN, IN_RANGE, JUST_OUT_OF_RANGE, STILL_OUT_OF_RANGE
    }
}
