package com.project.tenvinc.bluetoothreminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.util.List;

public class BeaconListAdapter extends BaseAdapter {
    LayoutInflater inflater;
    List<Beacon> data;

    public BeaconListAdapter(Context context, List<Beacon> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void setData(List<Beacon> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.beacon_list_layout, parent, false);
            Beacon currBeacon = data.get(position);

            TextView uuidText = convertView.findViewById(R.id.uuidText);
            TextView minorText = convertView.findViewById(R.id.minorText);
            TextView majorText = convertView.findViewById(R.id.majorText);

            uuidText.setText(currBeacon.getId1().toString());
            minorText.setText(currBeacon.getId2().toString());
            majorText.setText(currBeacon.getId3().toString());

            result = convertView;
        } else {
            result = convertView;
        }
        return result;
    }
}
