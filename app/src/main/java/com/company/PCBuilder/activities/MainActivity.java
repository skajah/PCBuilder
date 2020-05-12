package com.company.PCBuilder.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.company.PCBuilder.PCComponent;
import com.company.PCBuilder.R;
import com.company.PCBuilder.fragments.AccountFragment;
import com.company.PCBuilder.fragments.BuildFragment;
import com.company.PCBuilder.fragments.CreateComponentFragment;
import com.company.PCBuilder.fragments.SearchFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.company.PCBuilder.adapters.SectionsPagerAdapter;
import com.company.PCBuilder.interfaces.OnBuildNameChangedListener;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


import static com.company.PCBuilder.activities.LoadScreenActivity.ALL_COMPONENTS;
import static com.company.PCBuilder.activities.LoadScreenActivity.UNAVAILABLE_COMPONENTS;
import static com.company.PCBuilder.activities.LoadScreenActivity.USER_COMPONENTS;
import static com.company.PCBuilder.activities.LoginActivity.ACCOUNT_TYPE;
import static com.company.PCBuilder.activities.LoginActivity.EMAIL;
import static com.company.PCBuilder.activities.LoadScreenActivity.BUILD_NAME;
import static com.company.PCBuilder.activities.LoadScreenActivity.BRANDS;
import static com.company.PCBuilder.activities.LoadScreenActivity.CATEGORIES;


import static com.company.PCBuilder.activities.LoadScreenActivity.makePair;


public class MainActivity extends AppCompatActivity
implements OnBuildNameChangedListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DocumentReference userDocument;

    private String email, buildName, accountType;

    private ArrayList<PCComponent> allComponents, userComponents;

    private static final String TAG = "MAIN_ACTIVITY";

    private ArrayList<String> brands, categories;

    private BuildFragment buildFragment;
    private SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // can only be started by Login Activity
        Intent i = getIntent();


        email = i.getStringExtra(EMAIL);

        accountType = i.getStringExtra(ACCOUNT_TYPE);

        buildName = i.getStringExtra(BUILD_NAME);

        allComponents = (ArrayList<PCComponent>) i.getSerializableExtra(ALL_COMPONENTS);

        Collections.sort(allComponents, new Comparator<PCComponent>() {
            @Override
            public int compare(PCComponent o1, PCComponent o2) {
                return o1.getBrand().compareTo(o2.getBrand());
            }
        });

        userComponents = (ArrayList<PCComponent>) i.getSerializableExtra(USER_COMPONENTS);

        brands = (ArrayList<String>) i.getSerializableExtra(BRANDS);

        categories = (ArrayList<String>) i.getSerializableExtra(CATEGORIES);

        int unavailableComponents = i.getIntExtra(UNAVAILABLE_COMPONENTS, 0);

        if (unavailableComponents > 0)
            showUpdate(unavailableComponents);

        ArrayList<Fragment> fragmentPages = new ArrayList<>();
        buildFragment = new BuildFragment(); // we need to keep a reference to this so that we can update user build list

        searchFragment = new SearchFragment(); // keep reference to tell the account type

        AccountFragment accountFragment = new AccountFragment();

        fragmentPages.add(accountFragment); // first tab to make the sections adapter logic easier
        fragmentPages.add(buildFragment);
        fragmentPages.add(searchFragment);

        if (accountType.equals("client")){
            searchFragment.setClientAccount(true);
            fragmentPages.add(new CreateComponentFragment());
        }

        // Sets up tabs in activity
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(),
                fragmentPages);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(3); // so that tabs are not recreated
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        Log.d(TAG, "email: " + email);
        Log.d(TAG, "build_name: " + buildName);

        userDocument = db.collection("Users").document(email);

    }

    @Override
    public void onBuildNameChanged(String newName) {
        updateWithDataBase();
        this.buildName = newName;
    }

    public String getEmail(){
        return email;
    }

    public String getAccountType(){
        return accountType;
    }

    public String getBuildName(){
        return buildName;
    }

    public ArrayList<String> getBrands(){
        return this.brands;
    }

    public ArrayList<String> getCategories() { return this.categories;}


    public ArrayList<PCComponent> getAllComponents(){ return allComponents; }

    public ArrayList<PCComponent> getUserComponents(){
        return userComponents;
    }


    public ArrayList<PCComponent> getComponentsByBrands(ArrayList<String> brands){
        ArrayList<PCComponent> brandSearchResult = new ArrayList<>();

        for (PCComponent pc : allComponents) {
            // arraylist.contains Runtime(O(n))
            if (brands.contains(pc.getBrand())) {
                brandSearchResult.add(pc);
            }
        }

        Collections.sort(brandSearchResult,
                new Comparator<PCComponent>() {
                    @Override
                    public int compare(PCComponent o1, PCComponent o2) {
                        return o1.getBrand().compareTo(o2.getBrand());
                    }
                });
        return brandSearchResult;
    }


    public ArrayList<PCComponent> getComponentsByCategories(ArrayList<String> categories){
        ArrayList<PCComponent> brandSearchResult = new ArrayList<>();
        for (PCComponent pc : allComponents) {
            // arrayList.contains Runtime(O(n))
            if (categories.contains(pc.getCategory())) {
                brandSearchResult.add(pc);
            }
        }

        Collections.sort(brandSearchResult,
                new Comparator<PCComponent>() {
                    @Override
                    public int compare(PCComponent o1, PCComponent o2) {
                        return o1.getBrand().compareTo(o2.getBrand());
                    }
                });

        return brandSearchResult;
    }


    public ArrayList<PCComponent> getComponentsByPrice(float low, float high){
        ArrayList<PCComponent> priceSearchResult = new ArrayList<>();

        for (PCComponent pc : allComponents) {
            if (pc.getPrice() >= low && pc.getPrice() <= high) {
                priceSearchResult.add(pc);
            }
        }

        Collections.sort(priceSearchResult, new Comparator<PCComponent>() {
            @Override
            public int compare(PCComponent o1, PCComponent o2) {
                return (int)(o1.getPrice() - o2.getPrice());
            }
        });

        return priceSearchResult;

    }


    public ArrayList<PCComponent> getComponentsByKeyword(String keyword){
        ArrayList<PCComponent> matches = new ArrayList<>();

        for (PCComponent c: allComponents){
            if (c.getName().toLowerCase().contains(keyword.toLowerCase()))
                matches.add(c);
        }

        Collections.sort(matches, new Comparator<PCComponent>() {
            @Override
            public int compare(PCComponent o1, PCComponent o2) {
                return (int)(o1.getPrice() - o2.getPrice());
            }
        });
        return matches;
    }

    public void addToBuild(PCComponent component){

        if (userComponents.contains(component)){
            Toast.makeText(this, component.getName() + " is already in your build", Toast.LENGTH_SHORT).show();
        } else {
            PCComponent sameCategoryComponent = findCategoryInBuild(component.getCategory());

            if (sameCategoryComponent != null){ // category already there, then check to replace old
                confirmReplace(sameCategoryComponent, component);
                return;
            }

            _addToBuild(component);

        }

    }

    private void _addToBuild(PCComponent component){
        userComponents.add(component);
        buildFragment.updatePrice(userComponents);
        buildFragment.updateList(userComponents);
        updateWithDataBase();
        Toast.makeText(this, component.getName() + " added to build", Toast.LENGTH_SHORT).show();
    }

    private PCComponent findCategoryInBuild(String category){
        for (PCComponent c: userComponents){
            if (c.getCategory().equals(category))
                return c;
        }
        return null;
    }

    private void confirmReplace(PCComponent oldComponent, PCComponent newComponent){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Component Conflict")
                .setMessage("You already have a " + oldComponent.getCategory() + " in your build. " +
                        "Replace " + oldComponent.getName() + " with " + newComponent.getName() + '?')
                .setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeFromBuild(oldComponent);
                        _addToBuild(newComponent);
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

    public void removeComponent(PCComponent component){
        if (this.allComponents.remove(component)) {
            db.collection("PCComponents")
                    .document(component.getId())
                    .delete();
        }
    }

    public ArrayList<PCComponent> removeFromBuild(PCComponent component){
        userComponents.remove(component);
        Toast.makeText(this, component.getName() + " removed from build", Toast.LENGTH_SHORT).show();
        updateWithDataBase();
        buildFragment.updatePrice(userComponents);
        return userComponents;
    }


    public void clearBuild(){
        userComponents.clear();
        updateWithDataBase();
    }

    private ArrayList<String> getComponentIds(ArrayList<PCComponent> components){
        ArrayList<String> ids = new ArrayList<>();

        for (PCComponent c: components){
            ids.add(c.getId());
        }
        return ids;
    }

    private void updateWithDataBase() {
        userDocument.set(makePair(BUILD_NAME, buildName), SetOptions.merge());
        userDocument.set(makePair("components", getComponentIds(userComponents)), SetOptions.merge());
    }

    private void showUpdate(int unavailableComponents){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Change to Build")
                .setMessage(String.format("%d item(s) in your build are no longer available. The seller doesn't " +
                        "carry them anymore", unavailableComponents))
                .setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "in onDestroy() and saving buildname and components");
        updateWithDataBase();
        super.onDestroy();
    }
}