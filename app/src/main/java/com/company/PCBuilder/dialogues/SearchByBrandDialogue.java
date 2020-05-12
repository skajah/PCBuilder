package com.company.PCBuilder.dialogues;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.company.PCBuilder.interfaces.OnBrandsChosenListener;

import java.util.ArrayList;

public class SearchByBrandDialogue extends AppCompatDialogFragment {

    private static final String TAG = "SEARCH_BRAND_DIALOG";

    private OnBrandsChosenListener communicator;

    private final String[] brands;

    public SearchByBrandDialogue(OnBrandsChosenListener listener, ArrayList<String> brands){
        communicator = listener;
        this.brands = brands.toArray(new String[brands.size()]);
        Log.d(TAG, "# of brands: " + brands.size());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final boolean[] checkedItems = new boolean[brands.length];

        builder.setTitle("Chose brands")
                .setMultiChoiceItems(brands, checkedItems, new DialogInterface.OnMultiChoiceClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // Do Nothing
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing to do; auto closes
                    }
                })
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // figure out the selected brands
                        ArrayList<String> selectedBrands = new ArrayList<>();

                        for (int i=0; i < brands.length; i++){
                            if (checkedItems[i])
                                selectedBrands.add(brands[i]);
                        }
                        // tell the communicator that ok button was clicked with these brands chosen
                        // in the search fragment, you should check that the length is > 0. Don't update if not
                        communicator.onBrandsChosen(selectedBrands);
                    }
                });

        return builder.create();
    }
}
