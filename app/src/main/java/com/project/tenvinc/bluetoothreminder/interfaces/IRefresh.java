package com.project.tenvinc.bluetoothreminder.interfaces;

import org.altbeacon.beacon.Beacon;

import java.util.List;

public interface IRefresh {
    void refresh(List<Beacon> data);
}
