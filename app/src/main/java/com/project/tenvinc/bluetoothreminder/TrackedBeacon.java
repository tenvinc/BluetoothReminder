package com.project.tenvinc.bluetoothreminder;

import android.util.Log;

import org.altbeacon.beacon.Beacon;

public class TrackedBeacon {

    private static final long MILLIS_PER_SECOND = 1000;
    private final String TAG = this.getClass().getName();
    private long sleepDuration = 10;  // Specify how long beacon notifications will sleep for after a notification in seconds
    private long lastUpdateTime;  // last time when notification was fired in seconds
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

    public void updateState(Boolean isInRange) {
        switch (currState) {
            case UNKNOWN:
            case IN_RANGE:
                currState = (isInRange) ? State.IN_RANGE : State.JUST_OUT_OF_RANGE;
                break;
            case JUST_OUT_OF_RANGE:
            case STILL_OUT_OF_RANGE:
                currState = (isInRange) ? State.IN_RANGE : State.STILL_OUT_OF_RANGE;
                break;
            default:
                Log.d(TAG, "This should not be happening. Please check your enums again.");
        }
    }

    public Boolean isNotificationNeeded() {
        if (currState.equals(State.JUST_OUT_OF_RANGE)) {
            return true;
        }
        Long waitDuration = BeaconApplication.getInstance().getWaitDuration();
        if (currState.equals(State.STILL_OUT_OF_RANGE) && System.currentTimeMillis() / MILLIS_PER_SECOND
                - lastUpdateTime >= waitDuration && waitDuration != -1) {
            Log.d(TAG, String.format("%s", System.currentTimeMillis() / MILLIS_PER_SECOND - lastUpdateTime));
            return true;
        }
        return false;
    }

    /**
     * Updates the lastUpdateTime with the current time
     */
    public void updateTimer() {
        long timeNow = System.currentTimeMillis() / MILLIS_PER_SECOND;
        lastUpdateTime = timeNow;
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

    public boolean isSameBeaconAs(Beacon toTest) {
        return toTest.getId1().toString().equals(getUuid())
                && toTest.getId2().toString().equals(getMinor())
                && toTest.getId3().toString().equals(getMajor());
    }

    @Override
    public String toString() {
        return String.format("%s :: %s :: %s :: %s", beaconName, beacon, currState, lastUpdateTime);
    }
}
