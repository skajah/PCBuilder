package com.company.PCBuilder.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.company.PCBuilder.PCComponent;
import com.company.PCBuilder.R;
import com.company.PCBuilder.activities.MainActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CreateComponentFragment extends Fragment {

    private Context context;

    private EditText nameEditText, priceEditText, brandEditText,
            categoryEditText, descriptionEditText;

    private Spinner brandSpinner, categorySpinner;

    private DecimalFormat df = new DecimalFormat("0.00");


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        df.setRoundingMode(RoundingMode.UP);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.create_component_layout, container, false);

        nameEditText = layout.findViewById(R.id.component_name);
        priceEditText = layout.findViewById(R.id.component_price);
        brandEditText = layout.findViewById(R.id.brand_text);
        categoryEditText = layout.findViewById(R.id.category_text);
        descriptionEditText = layout.findViewById(R.id.description_text);

        brandSpinner = layout.findViewById(R.id.brand_choices);
        categorySpinner = layout.findViewById(R.id.category_choices);

        List<String> brands = new ArrayList<>();
        List<String> categories = new ArrayList<>();

        brands.add("Brand");
        for (String brand: ((MainActivity) context).getBrands()){
            brands.add(brand);
        }

        categories.add("Category");
        for (String category: ((MainActivity) context).getCategories()){
            categories.add(category);
        }

        ArrayAdapter brandAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, brands);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(brandAdapter);

        ArrayAdapter categoryAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        Button createComponentButton = layout.findViewById(R.id.create_component_button);

        createComponentButton.setOnClickListener(createComponentListener);


        return layout;
    }

    private View.OnClickListener createComponentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = nameEditText.getText().toString().trim();
            Float price = getPrice(priceEditText.getText().toString());
            String brandTyped = brandEditText.getText().toString().trim();
            String brandSelected = brandSpinner.getSelectedItem().toString();
            String categoryTyped = categoryEditText.getText().toString().trim();
            String categorySelected = categorySpinner.getSelectedItem().toString();
            String description = descriptionEditText.getText().toString().trim();

            if (name.isEmpty()) {
                nameEditText.setError("Name can't be empty");
                nameEditText.requestFocus();
                return;
            } else if (price == null){
                priceEditText.setError("You must enter a number");
                priceEditText.requestFocus();
                return;
            } else if (brandSelected.equals("Brand") && brandTyped.isEmpty()){
                brandEditText.setError("Type or select brand");
                brandEditText.requestFocus();
                return;
            } else if (categorySelected.equals("Category") && categoryTyped.isEmpty()){
                categoryEditText.setError("Type or select category");
                categoryEditText.requestFocus();
                return;
            } else if (description.isEmpty()){
                descriptionEditText.setError("Description can't be empty");
                descriptionEditText.requestFocus();
                return;
            }

            String brand;

            if (brandSelected.equals("Brand"))
                brand = getBrand(brandTyped);
            else
                brand = getBrand(brandSelected);

            String category;

            if (categorySelected.equals("Category"))
                category = getCategory(categoryTyped);
            else
                category = getCategory(categorySelected);

            String displayString = "Name: " + name +
                    "\nPrice: " + price +
                    "\nBrand: " + brand +
                    "\nCategory: " + category +
                    "\nDescription: " + description;

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("New Component")
                    .setMessage(displayString)
                    .setPositiveButton("Create", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            DocumentReference componentDocument = FirebaseFirestore.getInstance()
                                    .collection("PCComponents").document();

                            PCComponent component = new PCComponent(
                                    brand, category, description,
                                    componentDocument.getId(), name, price);

                            ((MainActivity) context).getAllComponents().add(component);

                            Map<String, Object> details = new HashMap<>();
                            details.put("brand", brand);
                            details.put("category", category);
                            details.put("description", description);
                            details.put("name", name);
                            details.put("price", df.format(price));

                            componentDocument.set(details);

                            Toast.makeText(context, name + " created", Toast.LENGTH_SHORT).show();

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
    };

    private Float getPrice(String price){
        Float result = null;
        try {
            result = Float.valueOf(price);

            result = Float.valueOf(df.format(result));

        } catch (Exception e){
            // Skip it
        }

        return result;
    }

    private String getBrand(String chosenBrand){
        String result = chosenBrand;

        for (String brand: ((MainActivity) context).getBrands()){ // In case they type in an existing brand
            if (brand.toLowerCase().equals(chosenBrand.toLowerCase())){
                result = brand;
                break;
            }
        }
        return result;
    }

    private String getCategory(String chosenCategory){
        String result = chosenCategory;

        for (String category: ((MainActivity) context).getCategories()){ // In case they type in an existing category
            if (category.toLowerCase().equals(chosenCategory.toLowerCase())){
                result = category;
                break;
            }
        }
        return result;
    }
}
