package com.project.tenvinc.bluetoothreminder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AddTrackedDialog extends AppCompatDialogFragment {

    private EditText editName;
    private TextView uuidText;
    private TextView minorText;
    private TextView majorText;
    private FavouritesDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.favourite_dialog_layout, null);

        builder.setView(view)
                .setTitle("Add to tracked beacons")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editName.getText().toString();
                        String uuid = uuidText.getText().toString();
                        String minor = minorText.getText().toString();
                        String major = majorText.getText().toString();
                        listener.applyTexts(name, uuid, minor, major);
                    }
                });

        editName = view.findViewById(R.id.editName);
        uuidText = view.findViewById(R.id.uuidText);
        minorText = view.findViewById(R.id.minorText);
        majorText = view.findViewById(R.id.majorDialogText);

        Bundle bundle = getArguments();
        uuidText.setText(bundle.getString("uuid"));
        minorText.setText(bundle.getString("minor"));
        majorText.setText(bundle.getString("major"));

        uuidText.setMovementMethod(new ScrollingMovementMethod());

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (FavouritesDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement FavouritesDialogListener");
        }
    }

    public interface FavouritesDialogListener {
        void applyTexts(String name, String uuid, String minor, String major);
    }
}
