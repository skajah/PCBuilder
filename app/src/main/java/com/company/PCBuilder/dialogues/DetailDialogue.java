package com.company.PCBuilder.dialogues;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.company.PCBuilder.PCComponent;
import com.company.PCBuilder.R;
import com.company.PCBuilder.adapters.ComponentDetailListAdapter;
import com.company.PCBuilder.interfaces.OnDetailChosenListener;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DetailDialogue extends AppCompatDialogFragment {
    private String action;
    private boolean buttonClicked = false;
    private OnDetailChosenListener communicator;
    private PCComponent component;
    //private ListView listView;

    public DetailDialogue(OnDetailChosenListener listener, PCComponent component) {
        this.communicator = listener;
        this.component = component;
        //this.listView = listView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.component_viewer_layout, null);

        String name = component.getName();
        String brand = component.getBrand();
        String price = String.valueOf(component.getPrice());
        String category = component.getCategory();
        String description = component.getDescription();

        // Name
        TextView nameTextView = layout.findViewById(R.id.name_text);
        nameTextView.setText(name);

        // Picture
        ImageView image = layout.findViewById(R.id.category_image);
        image.setImageResource(getImage(category));
        image.setBackgroundResource(R.drawable.green_light_effect);

        // Details
        List<String> headerItems = Arrays.asList("Brand", "Price", "Category", "Description");

        HashMap<String, List<String >> childItems = new HashMap<>();

        childItems.put("Brand", Arrays.asList(brand));
        childItems.put("Price", Arrays.asList(price));
        childItems.put("Category", Arrays.asList(category));
        childItems.put("Description", Arrays.asList(description));

        ComponentDetailListAdapter adapter = new ComponentDetailListAdapter(getContext(), headerItems, childItems);

        ExpandableListView detailList = layout.findViewById(R.id.component_info_list);

        detailList.setAdapter(adapter);

        for (int i=0; i<4; i++){
            detailList.expandGroup(i);
        }


        // Get whether search or build, if search -> add, otherwise, delete
        if (communicator.getFragmentName().equals("Search")) {
           action = "Add";
        } else {
            action = "Delete";
        }

        builder.setView(layout)
                .setTitle("Details")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                })
                .setPositiveButton(action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ArrayList<PCComponent> updatedItems = ((MainActivity) communicator).removeFromBuild(component);
                        //listView.setAdapter(new PCComponentListAdapter((Context) communicator, updatedItems));
                        communicator.onDetailButtonClicked(component);
                        //Toast.makeText(getContext(), action, Toast.LENGTH_LONG).show();
                    }
                });

        return builder.create();
    }

    public static int getImage(String category){
        int image = 0;

        switch(category){
            case "Base":
                image = R.drawable.base;
                break;
            case "Fan":
                image = R.drawable.cooling_fan;
                break;
            case "CPU":
                image = R.drawable.cpu;
                break;
            case "GPU":
                image = R.drawable.gpu;
                break;
            case "Monitor":
                image = R.drawable.monitor;
                break;
            case "Motherboard":
                image = R.drawable.motherboard;
                break;
            case "OS":
                image = R.drawable.os;
                break;
            case "PSU":
                image = R.drawable.psu;
                break;
            case "RAM":
                image = R.drawable.ram;
                break;
            case "Storage":
                image = R.drawable.storage;
                break;
            case "Keyboard":
                image = R.drawable.keyboard;
                break;
            case "Mouse":
                image = R.drawable.mouse;
                break;
            default:
                image = R.drawable.unknown_part;
                break;
        }

        return image;
    }
}
