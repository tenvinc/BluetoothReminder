package com.project.tenvinc.bluetoothreminder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import static com.project.tenvinc.bluetoothreminder.BeaconStringUtils.getDistString;
import static com.project.tenvinc.bluetoothreminder.BeaconStringUtils.getMajorString;
import static com.project.tenvinc.bluetoothreminder.BeaconStringUtils.getMinorString;
import static com.project.tenvinc.bluetoothreminder.BeaconStringUtils.getUuidString;

public class MoreInfoDialog extends AppCompatDialogFragment {
    private TextView uuidText;
    private TextView minorText;
    private TextView majorText;
    private TextView distText;
    private Beacon ref;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_more_info, null);

        builder.setView(view)
                .setTitle("Additional info")
                .setNegativeButton("return", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        uuidText = view.findViewById(R.id.uuidText);
        minorText = view.findViewById(R.id.minorText);
        majorText = view.findViewById(R.id.majorDialogText);
        distText = view.findViewById(R.id.distText);

        Bundle bundle = getArguments();
        ref = BeaconApplication.getInstance().beacons.get(bundle.getInt("position"));
        uuidText.setText(getUuidString(ref));
        minorText.setText(getMinorString(ref));
        majorText.setText(getMajorString(ref));
        distText.setText(getDistString(ref));

        return builder.create();
    }
}
