package com.company.PCBuilder.dialogues;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.company.PCBuilder.interfaces.OnCategoriesChosenListener;

import java.util.ArrayList;

public class SearchByCategoryDialogue extends AppCompatDialogFragment {
    // Label the TAG
    private static final String TAG = "SEARCH_CATEGORIES_DIALOG";

    // Create a communicator between activity and dialogFragment
    private OnCategoriesChosenListener communicator;

    // Get array for the categories
    private final String[] categories;

    // Constructor with communicator and categories
    public SearchByCategoryDialogue(OnCategoriesChosenListener listener, ArrayList<String> categories){
        communicator = listener;
        this.categories = categories.toArray(new String[categories.size()]);
        Log.d(TAG, "# of categories: " + categories.size());
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Get current activity
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // to see the checked items
        final boolean[] checkedItems = new boolean[categories.length];

        // Set the UI and actions with
        // setTitle: Chose categories
        // setMultiChoiceItems: do nothing when some parts are chosen
        // setNegativeButton: do nothing when user cancels the dialogue
        // setPositiveButton: store the selected categories in an arrayList and return
        builder.setTitle("Chose categories")
                .setMultiChoiceItems(categories, checkedItems, new DialogInterface.OnMultiChoiceClickListener(){
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
                        // figure out the selected categories
                        ArrayList<String> selectedCategories = new ArrayList<>();

                        // In the range of all categories, if the category is selected
                        // Add it into the returning arrayList
                        for (int i=0; i < categories.length; i++){
                            if (checkedItems[i])
                                selectedCategories.add(categories[i]);
                        }
                        // tell the communicator that ok button was clicked with these categories chosen
                        // in the search fragment, you should check that the length is > 0. Don't update if not
                        communicator.onCategoriesChosen(selectedCategories);
                    }
                });

        // Builder the dialogue
        return builder.create();
    }
}