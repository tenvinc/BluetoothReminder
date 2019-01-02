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
import android.widget.EditText;
import android.widget.TextView;

import com.project.tenvinc.bluetoothreminder.R;

public class EditTrackedDialog extends AppCompatDialogFragment {

    private TextView oldName;
    private EditText newName;
    private EditDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit, null);

        builder.setView(view)
                .setTitle("Edit name of selected beacon?")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.edit(oldName.getText().toString(), newName.getText().toString());
                    }
                });

        oldName = view.findViewById(R.id.oldNameText);
        newName = view.findViewById(R.id.newNameEdit);

        Bundle bundle = getArguments();
        oldName.setText(bundle.getString("name"));

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (EditTrackedDialog.EditDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement EditDialogListener");
        }
    }

    public interface EditDialogListener {
        void edit(String oldName, String newName);
    }
}
