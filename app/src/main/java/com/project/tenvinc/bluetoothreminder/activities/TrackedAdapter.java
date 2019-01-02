package com.project.tenvinc.bluetoothreminder.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.project.tenvinc.bluetoothreminder.BeaconApplication;
import com.project.tenvinc.bluetoothreminder.BeaconInfo;
import com.project.tenvinc.bluetoothreminder.R;
import com.project.tenvinc.bluetoothreminder.TrackedBeacon;
import com.project.tenvinc.bluetoothreminder.dialogfragments.EditTrackedDialog;
import com.project.tenvinc.bluetoothreminder.dialogfragments.MoreInfoDialog;
import com.project.tenvinc.bluetoothreminder.dialogfragments.RemoveTrackedDialog;
import com.project.tenvinc.bluetoothreminder.exceptions.DuplicateNameException;

import java.util.ArrayList;
import java.util.List;

class TrackedAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private List<TrackedBeacon> trackedBeacons;
    private AppCompatActivity context;
    private static String TAG = "TrackedAdapter";

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
        final TextView nameText = convertView.findViewById(R.id.nameText);
        TextView distText = convertView.findViewById(R.id.distText);

        ImageView options = convertView.findViewById(R.id.options);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Bundle bundle = new Bundle();
                        switch (item.getItemId()) {
                            case R.id.extra_info:
                                MoreInfoDialog moreInfoDialog = new MoreInfoDialog();
                                bundle.putParcelable("parcel", new BeaconInfo(curr.getBeacon()));
                                moreInfoDialog.setArguments(bundle);
                                showDialog(moreInfoDialog);
                                return true;
                            case R.id.remove:
                                RemoveTrackedDialog removeTrackedDialog = new RemoveTrackedDialog();
                                bundle.putString("name", nameText.getText().toString());
                                removeTrackedDialog.setArguments(bundle);
                                showDialog(removeTrackedDialog);
                                return true;
                            case R.id.edit:
                                EditTrackedDialog editTrackedDialog = new EditTrackedDialog();
                                bundle.putString("name", nameText.getText().toString());
                                editTrackedDialog.setArguments(bundle);
                                showDialog(editTrackedDialog);
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.tracked_options);
                popupMenu.show();
            }
        });

        final SwitchCompat sw = convertView.findViewById(R.id.sw);
        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackedBeacon tb = BeaconApplication.getInstance().trackedBeacons.getTrackedBeacon(position);
                tb.toggleIsEnabled(sw.isChecked());
                try {
                    BeaconApplication.getInstance().trackedBeacons.edit(position, tb);
                } catch (DuplicateNameException e) {
                    Log.e(TAG, "this should not be executed");
                }
                if (sw.isChecked()) {
                    Toast.makeText(v.getContext(), "Beacon tracked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Beacon not tracked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        idText.setText(curr.getUuid());
        nameText.setText(curr.getBeaconName());
        distText.setText(curr.getDistance());
        sw.setChecked(curr.isEnabled());


        return convertView;
    }

    private void showDialog(AppCompatDialogFragment dialogFragment) {
        dialogFragment.show(context.getSupportFragmentManager(), "");
    }
}
