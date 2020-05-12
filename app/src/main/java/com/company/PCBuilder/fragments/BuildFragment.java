package com.company.PCBuilder.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.company.PCBuilder.PCComponent;
import com.company.PCBuilder.activities.MainActivity;
import com.company.PCBuilder.R;
import com.company.PCBuilder.adapters.PCComponentListAdapter;
import com.company.PCBuilder.dialogues.BuildDialogue;
import com.company.PCBuilder.dialogues.DetailDialogue;
import com.company.PCBuilder.interfaces.OnBuildNameChangedListener;
import com.company.PCBuilder.interfaces.OnDetailChosenListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BuildFragment extends Fragment
implements OnBuildNameChangedListener, OnDetailChosenListener
{
    private static final String TAG = "BUILD_FRAGMENT";

    private Context context;

    private TextView buildNameTextView, buildPriceTextView;
    private ListView buildListView;

    private DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "in onAttach()");
        this.context = context;
        df.setRoundingMode(RoundingMode.UP);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "in onCreate()");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View layout = inflater.inflate(R.layout.build_layout, container, false);

       Log.d(TAG, "in onCreateView()");

       setRetainInstance(true);

       /* ****************************Change Build Name Button ****************************************** */

        Button changeBuildNameButton = layout.findViewById(R.id.change_build_name_button);

        changeBuildNameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Open the dialogue
                BuildDialogue dialogue = new BuildDialogue(BuildFragment.this);
                dialogue.show(getParentFragmentManager(), "build_dialogue");
            }
        });

        /* **************************** Clear Build Button ****************************************** */

        Button clearBuildButton = layout.findViewById(R.id.clear_build_button);

        clearBuildButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (((MainActivity) context).getUserComponents().size() > 0)
                    confirmClear();
            }
        });

        /* **************************** Get email and build name from MainActivity ****************************************** */

        buildNameTextView = layout.findViewById(R.id.build_name_text);

        String buildName = ((MainActivity) context).getBuildName();

        buildNameTextView.setText(buildName);

        Log.d(TAG, "Got build name from Main: " + buildName);

        /* **************************** Set price ****************************************** */

        buildPriceTextView = layout.findViewById(R.id.build_price_text);

        updatePrice(((MainActivity) context).getUserComponents());

        /* **************************** Build List init  ****************************************** */

        buildListView = layout.findViewById(R.id.build_list);

        PCComponentListAdapter adapter = new PCComponentListAdapter(context,
                ((MainActivity) context).getUserComponents(), false);

        buildListView.setAdapter(adapter);

        buildListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetailDialogue((PCComponent) parent.getItemAtPosition(position));
            }
        });

        registerForContextMenu(buildListView);

       return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "in onActivityCreated()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "in onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "in onResume()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "in onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "in onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "in onDetach()");
    }

    @Override
    public void onBuildNameChanged(String newName) {
        // Called when "ok" was clicked on the change build name dialogue
        if (!newName.isEmpty()){
            buildNameTextView.setText(newName);
            // tell main activity that build name was changed
            ((OnBuildNameChangedListener) context).onBuildNameChanged(newName);
        }
    }

    @Override
    public String getFragmentName() {
        return "Build";
    }

    @Override
    public void onDetailButtonClicked(PCComponent component) {
        ArrayList<PCComponent> modifiedComponents = ((MainActivity) context).removeFromBuild(component);
        updateList(modifiedComponents);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        try { // just in case we can't get the activity
            getActivity().getMenuInflater().inflate(R.menu.build_context_menu, menu);
        } catch (NullPointerException e) {
            ((Activity) context).getMenuInflater().inflate(R.menu.build_context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        AdapterView.AdapterContextMenuInfo info = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo());


        try {
            PCComponent component = (PCComponent) buildListView.getItemAtPosition(info.position);

            if (id == R.id.view_component){
                showDetailDialogue(component);
                return true;
            } else if (id == R.id.delete_component){
                ArrayList<PCComponent> updatedItems = ((MainActivity) context).removeFromBuild(component);

                Log.d(TAG, "size after deletion: " + updatedItems.size());
                updateList(updatedItems);
                return true;
            }
        } catch (IndexOutOfBoundsException e){
            Log.d(TAG, "Search fragment list view was clicked");
            return false;
        }

        return super.onContextItemSelected(item);
    }

    public void updateList(ArrayList<PCComponent> newItems){
        buildListView.setAdapter(new PCComponentListAdapter(context, newItems, false));
    }


    private void showDetailDialogue(PCComponent component){
        DetailDialogue detailDialogue = new DetailDialogue(BuildFragment.this, component);
        detailDialogue.show(getParentFragmentManager(), "component_detail");
    }

    private void confirmClear(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Clear Build")
                .setMessage("Are you sure you want to clear your build?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity) context).clearBuild();
                        updateList(((MainActivity) context).getUserComponents());
                        updatePrice(((MainActivity) context).getUserComponents());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
        dialog.show();
    }

    public void updatePrice(List<PCComponent> components){
        double total = 0.00;

        for (PCComponent c: components){
            total += c.getPrice();
        }

        buildPriceTextView.setText(String.format("$%s", df.format(total)));
    }

}
