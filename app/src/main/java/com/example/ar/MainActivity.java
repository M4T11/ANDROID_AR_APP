package com.example.ar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import org.opencv.android.OpenCVLoader;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    Button startButton;
    Button startButton1;
    Button startButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        startButton1 = findViewById(R.id.startButton1);
        startButton2 = findViewById(R.id.startButton2);

        File f = new File(getApplicationContext().getFilesDir().getPath() + "/shared_prefs/" + "Spinner");
        SharedPreferences sharedPref = getSharedPreferences("Spinner",MODE_PRIVATE);
        int spinnerValueAlgorithm = sharedPref.getInt("userAlgorithmChoice",-1);
        String userAlgorithmChoiceString = sharedPref.getString("userAlgorithmChoiceString",null);
        int spinnerValueMatcher = sharedPref.getInt("userMatcherChoice",-1);
        String userMatcherChoiceString = sharedPref.getString("userMatcherChoiceString",null);
        int spinnerValueObjets = sharedPref.getInt("userObjectsChoice",-1);
        String userObjectsChoiceString = sharedPref.getString("userObjectsChoiceString",null);

        if (userAlgorithmChoiceString == null || spinnerValueAlgorithm == -1 || userMatcherChoiceString == null || spinnerValueMatcher == -1 || userObjectsChoiceString == null || spinnerValueObjets == -1) {
            SharedPreferences.Editor prefEditor = sharedPref.edit();
            prefEditor.putInt("userAlgorithmChoice", 0);
            prefEditor.putString("userAlgorithmChoiceString", "SIFT");
            prefEditor.putInt("userMatcherChoice", 0);
            prefEditor.putString("userMatcherChoiceString", "Brute-Force Matcher");
            prefEditor.putInt("userObjectsChoice", 0);
            prefEditor.putString("userObjectsChoiceString", "Wybierz obiekt");
            prefEditor.commit();
        }

        System.out.println(spinnerValueAlgorithm + userAlgorithmChoiceString + spinnerValueMatcher + userMatcherChoiceString + spinnerValueObjets + userObjectsChoiceString);

        if (!OpenCVLoader.initDebug())
            Log.e("OpenCV", "Unable to load OpenCV!");
        else
            Log.d("OpenCV", "OpenCV loaded Successfully!");

        startButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ImageSearch.class));
            }
        });

        startButton1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SingleFrameDetection.class));
            }
        });

        startButton2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LiveObjectDetection.class));
            }
        });


        if(fileExists(getApplicationContext(), "SIFT.txt")) {
            MyObjects.getInstance().setSiftObjectsArrayList(FileService.loadAlgorithmObjects("SIFT.txt", getApplicationContext()));
            Toast.makeText(getApplicationContext(), "SIFT - Wczytano dane z pliku!", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("SIFT - BRAK PLIKU");
        }

        if(fileExists(getApplicationContext(), "SURF.txt")) {
            MyObjects.getInstance().setSurfObjectsArrayList(FileService.loadAlgorithmObjects("SURF.txt", getApplicationContext()));
            Toast.makeText(getApplicationContext(), "SURF - Wczytano dane z pliku!", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("SIFT - BRAK PLIKU");
        }

        if(fileExists(getApplicationContext(), "FAST.txt")) {
            MyObjects.getInstance().setFastObjectsArrayList(FileService.loadAlgorithmObjects("FAST.txt", getApplicationContext()));
            Toast.makeText(getApplicationContext(), "FAST - Wczytano dane z pliku!", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("FAST - BRAK PLIKU");
        }

        if(fileExists(getApplicationContext(), "ORB.txt")) {
            MyObjects.getInstance().setOrbObjectsArrayList(FileService.loadOrbObjects("ORB.txt", getApplicationContext()));
            Toast.makeText(getApplicationContext(), "ORB - Wczytano dane z pliku!", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("ORB - BRAK PLIKU");
        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.image_search);

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
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(),Settings.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

}