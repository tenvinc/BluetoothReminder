package com.project.tenvinc.bluetoothreminder;

import org.altbeacon.beacon.Beacon;

public class BeaconStringUtils {

    public static String getUuidString(Beacon beacon) {
        return beacon.getId1().toString();
    }

    public static String getMinorString(Beacon beacon) {
        return beacon.getId2().toString();
    }

    public static String getMajorString(Beacon beacon) {
        return beacon.getId3().toString();
    }

    public static String getCombinedIdString(Beacon beacon) {
        return String.format("%s/ %s/ %s", beacon.getId1().toString(), beacon.getId2().toString(), beacon.getId3().toString());
    }

    public static String getDistString(Beacon beacon) {
        Double dist = beacon.getDistance();
        if (beacon.getDistance() >= 0) {
            return String.format("Dist. of %.2f m away", dist);
        } else {
            return "Not in range";
        }
    }

}
