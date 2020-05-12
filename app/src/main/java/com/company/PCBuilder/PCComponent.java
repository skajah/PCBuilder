package com.company.PCBuilder;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;

public class PCComponent implements Serializable {

    private String brand, category, description, id, name;
    private float price;

    static final String BRAND = "brand", CATEGORY = "category", DESCRIPTION = "description",
            ID = "id", NAME = "name", PRICE = "price";

    public PCComponent(DocumentSnapshot documentSnapshot){
        brand = documentSnapshot.getString(BRAND);
        category = documentSnapshot.getString(CATEGORY);
        description = documentSnapshot.getString(DESCRIPTION);
        id = documentSnapshot.getId();
        name = documentSnapshot.getString(NAME);
        Log.d("PCComponent", name + " : " + documentSnapshot.get(PRICE).toString());
        price = Float.parseFloat(documentSnapshot.getString(PRICE));
    }

    public PCComponent(String brand, String category, String description, String id, String name, float price){
        this.brand = brand;
        this.category = category;
        this.description = description;
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getBrand(){
        return brand;
    }

    public String getCategory(){
        return category;
    }

    public String getDescription(){
        return description;
    }

    public String getName(){
        return name;
    }

    public float getPrice(){
        return price;
    }

    public String getId() {
        return id;
    }

    @NonNull
    @Override
    public String toString() {
        return "Id: " + id + '\n' +
                "Name: " + name + '\n' +
                "Price: " + price + '\n' +
                "Brand: " + brand + '\n' +
                "Category: " + category + '\n' +
                "Description: " + description + '\n';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Log.d("PC Compare: ", "in here");
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (! (obj instanceof PCComponent))
            return false;

        return ((PCComponent) obj).getId().equals(id);
    }
}
