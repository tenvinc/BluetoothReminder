package com.project.tenvinc.bluetoothreminder;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.simulator.BeaconSimulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyBeaconSimulator implements BeaconSimulator {

    private final List<Beacon> beacons = new ArrayList<>();

    public MyBeaconSimulator() {
        for (int i = 0; i < 10; i++) {
            Beacon newBeacon = new Beacon.Builder()
                    .setId1("6fb0e0e9-2ae6-49d3-bba3-3cb7698c77e2")
                    .setId2(Integer.toString(i))
                    .setId3(Integer.toString(i))
                    .setManufacturer(0x0000)
                    .setTxPower(-59)
                    .setDataFields(Arrays.asList(0l))
                    .build();
            beacons.add(newBeacon);
        }
    }

    @Override
    public List<Beacon> getBeacons() {
        return beacons;
    }
}
