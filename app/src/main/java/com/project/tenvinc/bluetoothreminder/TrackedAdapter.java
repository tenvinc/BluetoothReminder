package com.project.tenvinc.bluetoothreminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class TrackedAdapter extends BaseAdapter {

    private List<TrackedBeacon> trackedBeacons;
    private final LayoutInflater inflater;

    public TrackedAdapter(Context context) {
        trackedBeacons = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<TrackedBeacon> data) {
        this.trackedBeacons = data;
    }

    @Override
    public int getCount() {
        return trackedBeacons.size();
    }

    @Override
    public Object getItem(int position) {
        return trackedBeacons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.tracked_list_layout, parent, false);
        TrackedBeacon curr = trackedBeacons.get(position);

        TextView uuidText = convertView.findViewById(R.id.uuidText);
        TextView minorText = convertView.findViewById(R.id.minorText);
        TextView majorText = convertView.findViewById(R.id.majorText);
        TextView nameText = convertView.findViewById(R.id.nameText);

        uuidText.setText(curr.getUuid());
        minorText.setText(curr.getMinor());
        majorText.setText(curr.getMajor());
        nameText.setText(curr.getBeaconName());

        return convertView;
    }
}
