package com.project.tenvinc.bluetoothreminder;

import android.os.Parcel;
import android.os.Parcelable;

import org.altbeacon.beacon.Beacon;

import static com.project.tenvinc.bluetoothreminder.BeaconStringUtils.getDistString;
import static com.project.tenvinc.bluetoothreminder.BeaconStringUtils.getMajorString;
import static com.project.tenvinc.bluetoothreminder.BeaconStringUtils.getMinorString;
import static com.project.tenvinc.bluetoothreminder.BeaconStringUtils.getUuidString;

public class BeaconInfo implements Parcelable {
    public static final Creator<BeaconInfo> CREATOR = new Creator<BeaconInfo>() {
        @Override
        public BeaconInfo createFromParcel(Parcel in) {
            return new BeaconInfo(in);
        }

        @Override
        public BeaconInfo[] newArray(int size) {
            return new BeaconInfo[size];
        }
    };
    protected String uuid;
    protected String minor;
    protected String major;
    protected String dist;

    public BeaconInfo(Beacon beacon) {
        uuid = getUuidString(beacon);
        minor = getMinorString(beacon);
        major = getMajorString(beacon);
        dist = getDistString(beacon);
    }

    protected BeaconInfo(Parcel in) {
        uuid = in.readString();
        minor = in.readString();
        major = in.readString();
        dist = in.readString();
    }

    public String getUuid() {
        return uuid;
    }

    public String getMinor() {
        return minor;
    }

    public String getMajor() {
        return major;
    }

    public String getDist() {
        return dist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(minor);
        dest.writeString(major);
        dest.writeString(dist);
    }
}
