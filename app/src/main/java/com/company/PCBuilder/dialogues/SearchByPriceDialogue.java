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
import com.company.PCBuilder.interfaces.OnPriceRangeListener;

public class SearchByPriceDialogue extends AppCompatDialogFragment {

    private EditText lowPriceEditText, highPriceEditText;
    private OnPriceRangeListener communicator;


    public SearchByPriceDialogue(OnPriceRangeListener listener){
        // get a reference to calling class so that we can send info about the dialogue back
        communicator = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.search_by_price_dialogue_layout, null);

        lowPriceEditText = layout.findViewById(R.id.search_by_price_low);
        highPriceEditText = layout.findViewById(R.id.search_by_price_high);

        builder.setView(layout)
                .setTitle("Enter Price Range")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing to do; auto closes
                    }
                })
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // tell the communicator that ok button was clicked
                        try {
                            float low = Float.parseFloat(lowPriceEditText.getText().toString().trim());
                            float high = Float.parseFloat(highPriceEditText.getText().toString().trim());
                            communicator.onPriceRange(low, high);
                        } catch (Exception e){
                            // SKIP IT
                        }

                    }
                });

        return builder.create();
    }
}