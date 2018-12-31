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

    private final LayoutInflater inflater;
    private List<TrackedBeacon> trackedBeacons;

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
        convertView = inflater.inflate(R.layout.list_tracked, parent, false);
        TrackedBeacon curr = trackedBeacons.get(position);

        TextView idText = convertView.findViewById(R.id.idText);
        TextView nameText = convertView.findViewById(R.id.nameText);
        TextView distText = convertView.findViewById(R.id.distText);

        idText.setText(curr.getUuid());
        nameText.setText(curr.getBeaconName());
        distText.setText(curr.getDistance());

        return convertView;
    }
}
