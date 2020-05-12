package com.company.PCBuilder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.company.PCBuilder.PCComponent;
import com.company.PCBuilder.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.company.PCBuilder.activities.LoginActivity.EMAIL;
import static com.company.PCBuilder.activities.LoginActivity.ACCOUNT_TYPE;

public class LoadScreenActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    static final String BUILD_NAME = "build_name", TAG = "LOAD_SCREEN",
    ALL_COMPONENTS = "all_commponents", USER_COMPONENTS = "users_components",
    BRANDS = "brands", CATEGORIES = "categories", UNAVAILABLE_COMPONENTS = "unavailable_components";

    private String buildName, email, accountType;

    private ArrayList<PCComponent> allComponents, userComponents;

    private ArrayList<String> brands, categories;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.load_screen_layout);

        Intent i = getIntent();

        email = i.getStringExtra(EMAIL);

        accountType = i.getStringExtra(ACCOUNT_TYPE);

        DocumentReference userDocument = db.collection("Users").document(email);

        CollectionReference componentCollection = db.collection("PCComponents");


        setAllInfo(userDocument, componentCollection);

    }


    @Override
    protected void onResume() {
        super.onResume();
        // makeComponents();
        // goToMain();
    }

    public static HashMap<String, Object> makePair(String key, Object value){
        HashMap<String, Object> result = new HashMap<>();
        result.put(key, value);
        return result;
    }

    private void goToMain(int unavailableComponents){
        Intent i = new Intent(LoadScreenActivity.this, MainActivity.class);
        i.putExtra(EMAIL, email);
        Log.d(TAG, "Account Type: " + accountType);
        i.putExtra(ACCOUNT_TYPE, accountType);
        i.putExtra(BUILD_NAME, buildName);
        i.putExtra(ALL_COMPONENTS, allComponents);
        i.putExtra(USER_COMPONENTS, userComponents);
        i.putExtra(CATEGORIES, categories);
        i.putExtra(BRANDS, brands);
        i.putExtra(UNAVAILABLE_COMPONENTS, unavailableComponents);
        startActivity(i);
        finish();
    }


    private void setAllInfo(final DocumentReference userDocument, CollectionReference componentCollection){
        Task userQuery = userDocument.get();
        Task componentQuery = componentCollection.get();

        // need this because we should open main activity after all the DB stuff is done
        Tasks.whenAllSuccess(userQuery, componentQuery)
                .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> objects) {
                        DocumentSnapshot userDocument = (DocumentSnapshot) objects.get(0);
                        QuerySnapshot collectionSnapshot = (QuerySnapshot) objects.get(1);

                        // Get the user's build name and the component's IDs
                        LoadScreenActivity.this.buildName = userDocument.getString(BUILD_NAME);
                        ArrayList<String> userComponentIds = (ArrayList<String>) userDocument.get("components");

                        // get all the components
                        ArrayList<PCComponent> allComponents = new ArrayList<>();

                        for (DocumentSnapshot ds: collectionSnapshot.getDocuments()){
                            allComponents.add(new PCComponent(ds));
                        }

                        LoadScreenActivity.this.allComponents = allComponents;

                        // get all the user's components from the Ids; at the same time get the brands and categories
                        ArrayList<PCComponent> userComponents = new ArrayList<>();
                        ArrayList<String> categories = new ArrayList<>();
                        ArrayList<String> brands = new ArrayList<>();

                        for (PCComponent c: allComponents){

                            if (userComponentIds.contains(c.getId()))
                                userComponents.add(c);

                            String brand = c.getBrand();
                            String category = c.getCategory();

                            if (!brands.contains(brand))
                                brands.add(brand);
                            if (!categories.contains(category))
                                categories.add(category);
                        }


                        final int unavailableUserIds = userComponentIds.size() - userComponents.size();


                        LoadScreenActivity.this.userComponents = userComponents;
                        LoadScreenActivity.this.categories = categories;
                        LoadScreenActivity.this.brands = brands;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                goToMain(unavailableUserIds);

                            }
                        },500); // delay for 1 second


                    }
                });


    }

    // Help to auto populate PCComponents db collection
    private void makeComponents() {
        String[] brands = {"Intel", "NVIDIA", "Qualcomm", "Acer", "Samsung", "ASUS"};
        String[] categories = {"Base", "Fan", "Motherboard", "CPU", "GPU", "RAM",
                "Monitor", "Storage", "PSU", "OS", "Keyboard", "Mouse"};

        Arrays.sort(categories);

        int min = 100;
        int max = 500;

        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.UP);

        for (String brand: brands){
            for (String category: categories){
                HashMap<Object, Object> info = new HashMap<>();

                info.put("brand", brand);
                info.put("category", category);


                info.put("price", df.format(min + Math.random() * (max - min)));

                String name = brand + " " + category;
                info.put("name", name);

                info.put("description", "Generic description for " + name);
                // maybe quantity at a later time?
                db.collection("PCComponents")
                        .document()
                        .set(info);
            }
        }
    }
}
