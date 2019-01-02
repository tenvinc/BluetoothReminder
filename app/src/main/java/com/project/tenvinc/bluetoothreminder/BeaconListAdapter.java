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

import org.altbeacon.beacon.Beacon;

import java.util.List;

import static com.project.tenvinc.bluetoothreminder.BeaconStringUtils.getCombinedIdString;
import static com.project.tenvinc.bluetoothreminder.BeaconStringUtils.getDistString;

public class BeaconListAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private List<Beacon> data;
    private static String TAG = "BeaconListAdapter.class";
    private final AppCompatActivity context;

    public BeaconListAdapter(Context context, List<Beacon> data) {
        inflater = LayoutInflater.from(context);
        this.context = (AppCompatActivity) context;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        convertView = inflater.inflate(R.layout.list_scan_result, parent, false);
        final Beacon currBeacon = data.get(position);

        TextView idText = convertView.findViewById(R.id.idText);
        TextView distText = convertView.findViewById(R.id.distText);
        final ImageView image_options = convertView.findViewById(R.id.options);

        idText.setText(getCombinedIdString(currBeacon));
        distText.setText(getDistString(currBeacon));

        image_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        MoreInfoDialog dialog = new MoreInfoDialog();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("parcel", new BeaconInfo(currBeacon));
                        dialog.setArguments(bundle);
                        showDialog(dialog);
                        return true;
                    }
                });
                popupMenu.inflate(R.menu.beacon_options);
                popupMenu.show();
            }
        });
        return convertView;
    }

    private void showDialog(AppCompatDialogFragment dialogFragment) {
        dialogFragment.show(context.getSupportFragmentManager(), "");
    }
}
