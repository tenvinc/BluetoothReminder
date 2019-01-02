package com.project.tenvinc.bluetoothreminder;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class TrackedAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private List<TrackedBeacon> trackedBeacons;
    private AppCompatActivity context;

    public TrackedAdapter(Context context) {
        trackedBeacons = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        this.context = (AppCompatActivity) context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.list_tracked, parent, false);
        final TrackedBeacon curr = trackedBeacons.get(position);

        TextView idText = convertView.findViewById(R.id.idText);
        TextView nameText = convertView.findViewById(R.id.nameText);
        TextView distText = convertView.findViewById(R.id.distText);
        ImageView options = convertView.findViewById(R.id.options);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        MoreInfoDialog dialog = new MoreInfoDialog();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("parcel", new BeaconInfo(curr.getBeacon()));
                        dialog.setArguments(bundle);
                        showDialog(dialog);
                        return true;
                    }
                });
                popupMenu.inflate(R.menu.beacon_options);
                popupMenu.show();
            }
        });

        idText.setText(curr.getUuid());
        nameText.setText(curr.getBeaconName());
        distText.setText(curr.getDistance());

        return convertView;
    }

    private void showDialog(AppCompatDialogFragment dialogFragment) {
        dialogFragment.show(context.getSupportFragmentManager(), "");
    }
}
