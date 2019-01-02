package com.project.tenvinc.bluetoothreminder;

import android.util.Log;

import org.altbeacon.beacon.Beacon;

import static com.project.tenvinc.bluetoothreminder.BeaconStringUtils.getDistString;

public class TrackedBeacon {

    private static final long MILLIS_PER_SECOND = 1000;
    private static final int THRESHOLD_COUNT = 5;
    private final String TAG = this.getClass().getName();
    private long lastUpdateTime;  // last time when notification was fired in seconds
    private Beacon beacon;
    private String beaconName;
    private State currState;
    private Double currDist;
    private int disconnectCount = 0;

    public TrackedBeacon(Beacon beacon, String beaconName, long sleepDuration) {
        this.beacon = beacon;
        this.beaconName = beaconName;
        currState = State.UNKNOWN;
        lastUpdateTime = -1;  //Time is not updated until first notification
        this.currDist = beacon.getDistance();
    }

    public TrackedBeacon(TrackedBeacon trackedBeacon) {
        this.lastUpdateTime = trackedBeacon.lastUpdateTime;
        this.beacon = trackedBeacon.beacon;
        this.beaconName = trackedBeacon.beaconName;
        this.currState = trackedBeacon.currState;
        this.currDist = trackedBeacon.currDist;
        this.disconnectCount = trackedBeacon.disconnectCount;
    }

    public void updateState(Boolean isInRange) {
        switch (currState) {
            case UNKNOWN:
            case IN_RANGE:
                if (isInRange) {
                    currState = State.IN_RANGE;
                    disconnectCount = 0;
                } else if (!isInRange && disconnectCount <= THRESHOLD_COUNT) {
                    currState = State.UNKNOWN;
                    disconnectCount++;
                } else {
                    currState = State.JUST_OUT_OF_RANGE;
                }
                break;
            case JUST_OUT_OF_RANGE:
            case STILL_OUT_OF_RANGE:
                currState = (isInRange) ? State.IN_RANGE : State.STILL_OUT_OF_RANGE;
                break;
            default:
                Log.d(TAG, "This should not be happening. Please check your enums again.");
        }
    }

    public void updateDist(Beacon beacon, Boolean isInRange) {
        currDist = isInRange ? beacon.getDistance() : -1.0;
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
        lastUpdateTime = System.currentTimeMillis() / MILLIS_PER_SECOND;
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

    public String getDistance() {
        return getDistString(currDist);
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

    public enum State {
        UNKNOWN, IN_RANGE, JUST_OUT_OF_RANGE, STILL_OUT_OF_RANGE
    }

    public Boolean isSameNameAs(TrackedBeacon other) {
        return beaconName.equals(other.beaconName);
    }
}
