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
import com.company.PCBuilder.interfaces.OnCompareChosenListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.company.PCBuilder.dialogues.DetailDialogue.getImage;

public class CompareDialogue extends AppCompatDialogFragment {

    private PCComponent component1, component2;
    private OnCompareChosenListener communicator;

    public CompareDialogue(OnCompareChosenListener communicator, PCComponent component1, PCComponent component2){
        this.communicator = communicator;
        this.component1 = component1;
        this.component2 = component2;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.compare_dialogue_layout, null);

        // For top component info
        String name1 = component1.getName();
        String brand1 = component1.getBrand();
        String price1 = String.valueOf(component1.getPrice());
        String category1 = component1.getCategory();
        String description1 = component1.getDescription();

        // For bottom panel
        String name2 = component2.getName();
        String brand2 = component2.getBrand();
        String price2 = String.valueOf(component2.getPrice());
        String category2 = component2.getCategory();
        String description2 = component2.getDescription();



        // Name Top panel
        TextView nameTextView1 = layout.findViewById(R.id.name_text1);
        nameTextView1.setText(name1);

        // Name bottom panel
        TextView nameTextView2 = layout.findViewById(R.id.name_text2);
        nameTextView2.setText(name2);


        // Picture Top Panel
        ImageView image1 = layout.findViewById(R.id.category_image1);
        image1.setImageResource(getImage(category1));
        image1.setBackgroundResource(R.drawable.green_light_effect);

        // Picture bottom Panel
        ImageView image2 = layout.findViewById(R.id.category_image2);
        image2.setImageResource(getImage(category2));
        image2.setBackgroundResource(R.drawable.green_light_effect);

        // Details
        List<String> headerItems = Arrays.asList("Brand", "Price", "Category", "Description");

        // For Top Panel
        HashMap<String, List<String >> childItems1 = new HashMap<>();

        childItems1.put("Brand", Arrays.asList(brand1));
        childItems1.put("Price", Arrays.asList(price1));
        childItems1.put("Category", Arrays.asList(category1));
        childItems1.put("Description", Arrays.asList(description1));

        ComponentDetailListAdapter adapter1 = new ComponentDetailListAdapter(getContext(), headerItems, childItems1);

        ExpandableListView detailList1 = layout.findViewById(R.id.component_info_list1);

        detailList1.setAdapter(adapter1);


        // For bottom panel
        HashMap<String, List<String >> childItems2 = new HashMap<>();

        childItems2.put("Brand", Arrays.asList(brand2));
        childItems2.put("Price", Arrays.asList(price2));
        childItems2.put("Category", Arrays.asList(category2));
        childItems2.put("Description", Arrays.asList(description2));

        ComponentDetailListAdapter adapter2 = new ComponentDetailListAdapter(getContext(), headerItems, childItems2);

        ExpandableListView detailList2 = layout.findViewById(R.id.component_info_list2);

        detailList2.setAdapter(adapter2);


        for (int i=0; i<4; i++){
            detailList1.expandGroup(i);
            detailList2.expandGroup(i);
        }

        builder.setView(layout)
                .setTitle("Comparing")
                .setNeutralButton("Add Both", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        communicator.onCompareChosen(component1, component2);
                    }
                })
                .setNegativeButton("Add 1st", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        communicator.onCompareChosen(component1, null);
                    }
                 })
                .setPositiveButton("Add 2nd", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        communicator.onCompareChosen(null, component2);
                    }
                });

        return builder.create();
    }
}
