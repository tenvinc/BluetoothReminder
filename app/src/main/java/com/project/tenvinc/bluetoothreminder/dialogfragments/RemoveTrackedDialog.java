package com.project.tenvinc.bluetoothreminder.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.project.tenvinc.bluetoothreminder.R;

public class RemoveTrackedDialog extends AppCompatDialogFragment {

    private TextView beaconNameText;
    private RemoveDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_remove, null);

        builder.setView(view)
                .setTitle("Remove selected beacon?")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.remove(beaconNameText.getText().toString());
                    }
                });

        beaconNameText = view.findViewById(R.id.beaconName);

        Bundle bundle = getArguments();
        beaconNameText.setText(bundle.getString("name"));

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (RemoveTrackedDialog.RemoveDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement RemoveDialogListener");
        }
    }

    public interface RemoveDialogListener {
        void remove(String name);
    }
}
