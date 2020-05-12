package com.company.PCBuilder.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.company.PCBuilder.activities.MainActivity;
import com.company.PCBuilder.PCComponent;
import com.company.PCBuilder.R;
import com.company.PCBuilder.adapters.PCComponentListAdapter;
import com.company.PCBuilder.dialogues.CompareDialogue;
import com.company.PCBuilder.dialogues.DetailDialogue;
import com.company.PCBuilder.dialogues.SearchByBrandDialogue;
import com.company.PCBuilder.dialogues.SearchByCategoryDialogue;
import com.company.PCBuilder.dialogues.SearchByPriceDialogue;
import com.company.PCBuilder.interfaces.OnBrandsChosenListener;
import com.company.PCBuilder.interfaces.OnCategoriesChosenListener;
import com.company.PCBuilder.interfaces.OnCompareChosenListener;
import com.company.PCBuilder.interfaces.OnDetailChosenListener;
import com.company.PCBuilder.interfaces.OnPriceRangeListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment

implements OnBrandsChosenListener,
        OnCategoriesChosenListener,
        OnPriceRangeListener,
        OnDetailChosenListener,
        OnCompareChosenListener {

    private Context context;

    private ListView searchByListView;

    private EditText keywordText;

    private boolean multiChoice = false;

    private ArrayList<PCComponent> currentItems;

    SparseBooleanArray checkedPositions = new SparseBooleanArray();

    private static final String  TAG = "SEARCH_FRAGMENT";

    private boolean clientAccount = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        Log.d(TAG, "in onAttach()");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "in onCreate()");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.search_layout, container, false);

        Log.d(TAG, "in onCreateView()");

        searchByListView = layout.findViewById(R.id.search_by_list_view);

        updateListView(((MainActivity) context).getAllComponents());

        RadioGroup searchRadioGroup = layout.findViewById(R.id.search_by_radio_group);

        searchRadioGroup.setOnCheckedChangeListener(onSearchListener);

        registerForContextMenu(searchByListView);

        ImageButton search_by_keyword = layout.findViewById(R.id.search_by_keyword);

        search_by_keyword.setOnClickListener(searchByKeywordListener);

        keywordText = layout.findViewById(R.id.search_by_text);

        Button compareButton = layout.findViewById(R.id.compare_button);

        compareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!multiChoice)
                    Toast.makeText(context, "Turn on Multi Select to compare", Toast.LENGTH_SHORT).show();
                else {
                    List<Integer> positionsChecked = getCheckedPositions();
                    if (positionsChecked.size() != 2)
                        Toast.makeText(context, "Can only compare with 2 items", Toast.LENGTH_SHORT).show();
                    else {
                        PCComponent component1 = currentItems.get(positionsChecked.get(0));
                        PCComponent component2 = currentItems.get(positionsChecked.get(1));

                        CompareDialogue dialogue = new CompareDialogue(SearchFragment.this,
                                component1, component2);
                        dialogue.show(getParentFragmentManager(), "compare_dialogue");
                    }

                }
            }
        });

        ToggleButton choiceModeButton = layout.findViewById(R.id.choice_mode_button);

        choiceModeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    multiChoice = true;
                } else {
                    multiChoice = false;
                    // reset the list view
                    updateListView(currentItems);
                }
            }
        });

        searchByListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!multiChoice)
                    showDetailDialogue( (PCComponent) parent.getItemAtPosition(position));
                else {
                    CheckBox checkBox =  view.findViewById(R.id.checkbox);
                    checkBox.toggle();
                    if (checkBox.isChecked())
                        checkedPositions.put(position, true);
                    else
                        checkedPositions.put(position, false);

                    /*
                    StringBuilder builder = new StringBuilder();
                    for (int i=0; i<currentItems.size(); i++) {
                        if (checkedPositions.get(i, false)) {
                            PCComponent c = (PCComponent) parent.getItemAtPosition(i);
                            builder.append(c.getName());
                            builder.append(", ");
                        }
                    }
                    Toast.makeText(context, builder.toString(), Toast.LENGTH_SHORT).show();

                     */
                }
            }
        });

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

    private RadioGroup.OnCheckedChangeListener onSearchListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton button = getActivity().findViewById(checkedId);

            // Different buttons clicked
            if (button.getId() == R.id.search_all) {
                updateListView(((MainActivity) context).getAllComponents());
            } else if (button.getId() == R.id.search_brand){
                // Create a new SearchByBrandDialogue with communicator and brands list
                SearchByBrandDialogue brandDialogue = new SearchByBrandDialogue(SearchFragment.this,
                        ((MainActivity) context).getBrands());

                brandDialogue.show(getParentFragmentManager(), "search_by_brand_dialogue");
            } else if (button.getId() == R.id.search_category) {
                // Create a new SearchByBrandDialogue with communicator and brands list
                SearchByCategoryDialogue categoriesDialogue = new SearchByCategoryDialogue(SearchFragment.this,
                        ((MainActivity) context).getCategories());

                categoriesDialogue.show(getParentFragmentManager(), "search_by_category_dialog");
            } else if (button.getId() == R.id.search_price) {
                SearchByPriceDialogue priceDialogue = new SearchByPriceDialogue(SearchFragment.this);

                priceDialogue.show(getParentFragmentManager(), "search_by_price_dialog");
            }
            // Toast.makeText(context, "Searching by " + button.getText(), Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener searchByKeywordListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String keyword = keywordText.getText().toString().trim();

            if (keyword.length() >= 3){
                ArrayList<PCComponent> keywordComponents = ((MainActivity) context).getComponentsByKeyword(keyword);
                updateListView(keywordComponents);
            } else {
                Toast.makeText(context, "Type at least 3 characters to search", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        try { // just in case we can't get the activity
            if (clientAccount)
                getActivity().getMenuInflater().inflate(R.menu.search_context_menu_client, menu);
            else
                getActivity().getMenuInflater().inflate(R.menu.search_context_menu, menu);
        } catch (NullPointerException e) {
            if (clientAccount)
                ((Activity) context).getMenuInflater().inflate(R.menu.search_context_menu_client, menu);
            else
                ((Activity) context).getMenuInflater().inflate(R.menu.search_context_menu, menu);
        }
    }



    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        AdapterView.AdapterContextMenuInfo info = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo());

        try {
            PCComponent component = (PCComponent) searchByListView.getItemAtPosition(info.position);

            if (id == R.id.add_to_build) {
                ((MainActivity) context).addToBuild(component);
                return true;
            } else if (id == R.id.view_component_search) {
                showDetailDialogue(component);
                return true;
            } else if (id == R.id.delete_component_search){
                confirmDelete(component);
            }
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "Build fragment list view was clicked");
            return false;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onBrandsChosen(ArrayList<String> brands) {
        ArrayList<PCComponent> brandComponents = ((MainActivity) context).getComponentsByBrands(brands);

        updateListView(brandComponents);
    }

    @Override
    public void onCategoriesChosen(ArrayList<String> categories) {
        ArrayList<PCComponent> categoryComponents = ((MainActivity) context).getComponentsByCategories(categories);

        updateListView(categoryComponents);
    }

    @Override
    public void onPriceRange(float low, float high) {
        ArrayList<PCComponent> inRange = ((MainActivity) context).getComponentsByPrice(low, high);
        updateListView(inRange);
    }

    @Override
    public String getFragmentName() {
        return "Search";
    }

    @Override
    public void onDetailButtonClicked(PCComponent component) {
        ((MainActivity) context).addToBuild(component);
    }

    private void updateListView(ArrayList<PCComponent> newItems) {
        currentItems = newItems;
        checkedPositions.clear();
        for (int i=0; i<currentItems.size(); i++){
            checkedPositions.put(i, false);
        }
        searchByListView.setAdapter(new PCComponentListAdapter(context, newItems, true));
    }

    private List<Integer> getCheckedPositions(){
        List<Integer> chosenIndices = new ArrayList<>();

        for (int i=0; i<currentItems.size(); i++){
            if (checkedPositions.get(i, false))
                chosenIndices.add(i);
        }
        return chosenIndices;
    }
    private void showDetailDialogue(PCComponent component){
        DetailDialogue detailDialogue = new DetailDialogue(SearchFragment.this, component);
        detailDialogue.show(getParentFragmentManager(), "component_detail");
    }

    @Override
    public void onCompareChosen(PCComponent component1, PCComponent component2) {
        if (component1 != null)
            ((MainActivity) context).addToBuild(component1);
        if (component2 != null)
            ((MainActivity) context).addToBuild(component2);
    }

    public void setClientAccount(boolean isClientAccount){
        this.clientAccount = isClientAccount;
    }

    private void confirmDelete(PCComponent component){
        final PCComponent componentFinal = component;
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Delete Component")
                .setMessage("Are you sure you want to delete " + component.getName() + " from the DB?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity) context).removeComponent(componentFinal);
                        updateListView(((MainActivity) context).getAllComponents());
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
}
