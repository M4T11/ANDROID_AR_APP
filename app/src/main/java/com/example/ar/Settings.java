package com.example.ar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class Settings extends AppCompatActivity {

    private Spinner spinnerAlgorithm;
    private Spinner spinnerMatcher;
    private Spinner spinnerObject;
    List<String> objectsList;
    String[] objectsArray;
    int oldUserAlgorithmChoice;
    ArrayList<algorithmObject> SiftObjectsArrayList;
    ArrayList<algorithmObject> SurfObjectsArrayList;
    ArrayList<algorithmObject> FastObjectsArrayList;
    ArrayList<algorithmObject> OrbObjectsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spinnerAlgorithm = (Spinner) findViewById(R.id.spinner_algorithm);
        spinnerMatcher = (Spinner) findViewById(R.id.spinner_matcher);
        spinnerObject = (Spinner) findViewById(R.id.spinner_object);

        SiftObjectsArrayList = MyObjects.getInstance().getSiftObjectsArrayList();
        SurfObjectsArrayList = MyObjects.getInstance().getSurfObjectsArrayList();
        FastObjectsArrayList = MyObjects.getInstance().getFastObjectsArrayList();
        OrbObjectsArrayList = MyObjects.getInstance().getOrbObjectsArrayList();

        String[] algorithms = {"SIFT", "SURF", "FAST-SURF", "ORB"};
        String[] matchers = {"Brute-Force Matcher", "FLANN Matcher"};
        objectsList = new ArrayList();

        ArrayAdapter<String> adapterAlgorithms = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                algorithms);

        adapterAlgorithms.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.spinnerAlgorithm.setAdapter(adapterAlgorithms);

        this.spinnerAlgorithm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onItemSelectedHandlerAlgorithms(parent, view, position, id);
                SharedPreferences sharedPref = getSharedPreferences("Spinner",0);
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                int userAlgorithmChoice = spinnerAlgorithm.getSelectedItemPosition();
                String userAlgorithmChoiceString = spinnerAlgorithm.getSelectedItem().toString();
                if (oldUserAlgorithmChoice != userAlgorithmChoice) {
                    prefEditor.putInt("userObjectsChoice", 0);
                    prefEditor.putString("userObjectsChoiceString", "Wybierz obiekt");
                }
                prefEditor.putInt("userAlgorithmChoice", userAlgorithmChoice);
                prefEditor.putString("userAlgorithmChoiceString", userAlgorithmChoiceString);
                prefEditor.commit();
                setObjectsList(userAlgorithmChoiceString);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapterMatchers = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                matchers);

        adapterMatchers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.spinnerMatcher.setAdapter(adapterMatchers);

        this.spinnerMatcher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onItemSelectedHandlerMatchers(parent, view, position, id);
                int userMatcherChoice = spinnerMatcher.getSelectedItemPosition();
                String userMatcherChoiceString = spinnerMatcher.getSelectedItem().toString();
                SharedPreferences sharedPref = getSharedPreferences("Spinner",0);
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.putInt("userMatcherChoice", userMatcherChoice);
                prefEditor.putString("userMatcherChoiceString", userMatcherChoiceString);
                prefEditor.commit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        SharedPreferences sharedPref = getSharedPreferences("Spinner",MODE_PRIVATE);
        int spinnerValueAlgorithm = sharedPref.getInt("userAlgorithmChoice",-1);
        String userAlgorithmChoiceString = sharedPref.getString("userAlgorithmChoiceString",null);
        setObjectsList(userAlgorithmChoiceString);
        int spinnerValueMatcher = sharedPref.getInt("userMatcherChoice",-1);
        int spinnerValueObjects = sharedPref.getInt("userObjectsChoice",-1);
        if(spinnerValueAlgorithm != -1) {
            spinnerAlgorithm.setSelection(spinnerValueAlgorithm);
            oldUserAlgorithmChoice = spinnerValueAlgorithm;
        }
        if(spinnerValueMatcher != -1) {
            spinnerMatcher.setSelection(spinnerValueMatcher);
        }
        if(spinnerValueObjects != -1) {
            spinnerObject.setSelection(spinnerValueObjects);
        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.settings);


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.object_descriptor:
                        startActivity(new Intent(getApplicationContext(),ObjectDescriptor.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.image_search:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.settings:
                        return true;
                }
                return false;
            }
        });
    }
    private void onItemSelectedHandlerAlgorithms(AdapterView<?> adapterView, View view, int position, long id) {
        Adapter adapter = adapterView.getAdapter();
//        localSettings.put("AlGORITHM", adapter.getItem(position).toString());
//        Toast.makeText(getApplicationContext(), "Selected: " + adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
    }

    private void onItemSelectedHandlerMatchers(AdapterView<?> adapterView, View view, int position, long id) {
        Adapter adapter = adapterView.getAdapter();
//        localSettings.put("MATCHER", adapter.getItem(position).toString());
//        Toast.makeText(getApplicationContext(), "Selected: " + adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
    }

    private void onItemSelectedHandlerObjects(AdapterView<?> adapterView, View view, int position, long id) {
        Adapter adapter = adapterView.getAdapter();
//        localSettings.put("MATCHER", adapter.getItem(position).toString());
//        Toast.makeText(getApplicationContext(), "Selected: " + adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
    }

    private void setObjectsList (String userAlgorithmChoiceString) {
        objectsList.removeAll(objectsList);
        switch(userAlgorithmChoiceString) {
            case "SIFT":
                objectsList.add("Wybierz obiekt");
                for (algorithmObject algorithmObject : SiftObjectsArrayList) {
                    objectsList.add(algorithmObject.getObjectName().toString());
                }
                objectsArray = new String[objectsList.size()];
                objectsArray = objectsList.toArray(objectsArray);
                break;
            case "SURF":
                objectsList.add("Wybierz obiekt");
                for (algorithmObject algorithmObject : SurfObjectsArrayList) {
                    objectsList.add(algorithmObject.getObjectName().toString());
                }
                objectsArray = new String[objectsList.size()];
                objectsArray = objectsList.toArray(objectsArray);
                break;
            case "FAST-SURF":
                objectsList.add("Wybierz obiekt");
                for (algorithmObject algorithmObject : FastObjectsArrayList) {
                    objectsList.add(algorithmObject.getObjectName().toString());
                }
                objectsArray = new String[objectsList.size()];
                objectsArray = objectsList.toArray(objectsArray);
                break;
            case "ORB":
                objectsList.add("Wybierz obiekt");
                for (algorithmObject algorithmObject : OrbObjectsArrayList) {
                    objectsList.add(algorithmObject.getObjectName().toString());
                }
                objectsArray = new String[objectsList.size()];
                objectsArray = objectsList.toArray(objectsArray);
                break;

        }
        ArrayAdapter<String> adapterObjects = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                objectsArray);
        adapterObjects.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.spinnerObject.setAdapter(adapterObjects);


        SharedPreferences sharedPref = getSharedPreferences("Spinner",MODE_PRIVATE);
        int spinnerValueObjects = sharedPref.getInt("userObjectsChoice",-1);
        if(spinnerValueObjects != -1) {
            spinnerObject.setSelection(spinnerValueObjects);
        }

        this.spinnerObject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onItemSelectedHandlerObjects(parent, view, position, id);
                int userObjectsChoice = spinnerObject.getSelectedItemPosition();
                String userObjectsChoiceString = spinnerObject.getSelectedItem().toString();
                SharedPreferences sharedPref = getSharedPreferences("Spinner",0);
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.putInt("userObjectsChoice", userObjectsChoice);
                prefEditor.putString("userObjectsChoiceString", userObjectsChoiceString);
                prefEditor.commit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

}
