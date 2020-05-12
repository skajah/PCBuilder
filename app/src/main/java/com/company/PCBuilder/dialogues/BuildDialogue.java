package com.company.PCBuilder.dialogues;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.company.PCBuilder.R;
import com.company.PCBuilder.interfaces.OnBuildNameChangedListener;

public class BuildDialogue extends AppCompatDialogFragment {

    private EditText buildNameEditText;
    private OnBuildNameChangedListener communicator;


    public BuildDialogue(OnBuildNameChangedListener listener){
        // get a reference to calling class so that we can send info about the dialogue back
        communicator = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.change_build_name_dialogue_layout, null);

        buildNameEditText = layout.findViewById(R.id.new_build_name_text);

        builder.setView(layout)
                .setTitle("Build Name")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing to do; auto closes
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // tell the communicator that ok button was clicked
                        communicator.onBuildNameChanged(buildNameEditText.getText().toString().trim());
                    }
                });

        return builder.create();
    }
}
