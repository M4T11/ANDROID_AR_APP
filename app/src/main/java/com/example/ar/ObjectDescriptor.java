package com.example.ar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.theartofdev.edmodo.cropper.CropImage;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ObjectDescriptor extends AppCompatActivity {


    Button openButton;
    Button saveButton;
    ImageView imageView;
    Uri mImageUri;
    Bitmap mImageBitmap;
    ArrayList<algorithmObject> SiftObjectsArrayList = null;
    ArrayList<algorithmObject> SurfObjectsArrayList = null;
    ArrayList<algorithmObject> FastObjectsArrayList = null;
    ArrayList<algorithmObject> OrbObjectsArrayList = null;
    algorithmObject algorithmObject = null;
    String objectName = "";
    String description = "";

    String userAlgorithmChoiceString;
    MatOfKeyPoint keypointsObject;
    Mat descriptorsObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_descriptor);

        imageView = findViewById(R.id.imageView);
        openButton = findViewById(R.id.openButton);
        saveButton = findViewById(R.id.saveButton);

        SiftObjectsArrayList =  MyObjects.getInstance().getSiftObjectsArrayList();
        SurfObjectsArrayList =  MyObjects.getInstance().getSurfObjectsArrayList();
        FastObjectsArrayList =  MyObjects.getInstance().getFastObjectsArrayList();
        OrbObjectsArrayList =  MyObjects.getInstance().getOrbObjectsArrayList();

        SharedPreferences sharedPref = getSharedPreferences("Spinner",MODE_PRIVATE);
        userAlgorithmChoiceString = sharedPref.getString("userAlgorithmChoiceString",null);

        System.out.println(userAlgorithmChoiceString);



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.object_descriptor);


        saveButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File imgPath = getOutputMediaFile();
                storeImage(mImageBitmap, imgPath);
                System.out.println("description" + description);
                algorithmObject.setImgPath(imgPath.toString());
                algorithmObject.setObjectName(objectName);
                algorithmObject.setDescription(description);

                switch(userAlgorithmChoiceString) {
                    case "SIFT":
                        SiftObjectsArrayList.add(algorithmObject);
                        MyObjects.getInstance().setSiftObjectsArrayList(SiftObjectsArrayList);
                        for(algorithmObject algorithmObjectArray : SiftObjectsArrayList)
                        {
                            System.out.println("SAVED");
                            algorithmObjectArray.printAlgorithmObject();
                        }
                        FileService.saveAlgorithmObjects(SiftObjectsArrayList, "SIFT.txt", getApplicationContext());
                        Toast.makeText(ObjectDescriptor.this, "Zapisano!", Toast.LENGTH_LONG).show();
                        saveButton.setEnabled(false);

                        break;
                    case "SURF":
                        SurfObjectsArrayList.add(algorithmObject);
                        MyObjects.getInstance().setSurfObjectsArrayList(SurfObjectsArrayList);
                        for(algorithmObject algorithmObjectArray : SurfObjectsArrayList)
                        {
                            System.out.println("SAVED");
                            algorithmObjectArray.printAlgorithmObject();
                        }
                        FileService.saveAlgorithmObjects(SurfObjectsArrayList, "SURF.txt", getApplicationContext());
                        Toast.makeText(ObjectDescriptor.this, "Zapisano!", Toast.LENGTH_LONG).show();
                        saveButton.setEnabled(false);
                        break;

                    case "FAST-SURF":
                        FastObjectsArrayList.add(algorithmObject);
                        MyObjects.getInstance().setFastObjectsArrayList(FastObjectsArrayList);
                        for(algorithmObject algorithmObjectArray : FastObjectsArrayList)
                        {
                            System.out.println("SAVED");
                            algorithmObjectArray.printAlgorithmObject();
                        }
                        FileService.saveAlgorithmObjects(FastObjectsArrayList, "FAST.txt", getApplicationContext());
                        Toast.makeText(ObjectDescriptor.this, "Zapisano!", Toast.LENGTH_LONG).show();
                        saveButton.setEnabled(false);
                        break;
                    case "ORB":
                        OrbObjectsArrayList.add(algorithmObject);
                        MyObjects.getInstance().setOrbObjectsArrayList(OrbObjectsArrayList);
                        for(algorithmObject algorithmObjectArray : OrbObjectsArrayList)
                        {
                            System.out.println("SAVED");
                            algorithmObjectArray.printAlgorithmObject();
                        }
                        FileService.saveOrbObjects(OrbObjectsArrayList, "ORB.txt", getApplicationContext());
                        Toast.makeText(ObjectDescriptor.this, "Zapisano!", Toast.LENGTH_LONG).show();
                        saveButton.setEnabled(false);
                        break;

                }

            }
        });


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.object_descriptor:
                        return true;
                    case R.id.image_search:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
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

    public void onChoosedFile(View v) {
        CropImage.activity().start(ObjectDescriptor.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                try
                {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                }
                catch (Exception e) {
                    //handle exception
                }

                if (mImageBitmap != null) {
                    showDialog();
                    showDialog1();

                    switch(userAlgorithmChoiceString) {
                        case "SIFT":
                            Object[] tempSIFT = OpenCVFunctions.computeSIFT(mImageBitmap);
                            keypointsObject = (MatOfKeyPoint)tempSIFT[0];
                            descriptorsObject = (Mat) tempSIFT[1];

                            System.err.println(Arrays.toString(keypointsObject.toArray()));
                            System.out.println(descriptorsObject);

                            algorithmObject = new algorithmObject(keypointsObject, descriptorsObject, objectName, description, "");

                            break;
                        case "SURF":
                            Object[] tempSURF = OpenCVFunctions.computeSURF(mImageBitmap);
                            keypointsObject = (MatOfKeyPoint)tempSURF[0];
                            descriptorsObject = (Mat) tempSURF[1];

                            System.err.println(Arrays.toString(keypointsObject.toArray()));
                            System.out.println(descriptorsObject);

                            algorithmObject = new algorithmObject(keypointsObject, descriptorsObject, objectName, description, "");
                            break;
                        case "FAST-SURF":
                            Object[] tempFAST = OpenCVFunctions.computeFAST(mImageBitmap);
                            keypointsObject = (MatOfKeyPoint)tempFAST[0];
                            descriptorsObject = (Mat) tempFAST[1];

                            System.err.println(Arrays.toString(keypointsObject.toArray()));
                            System.out.println(descriptorsObject);

                            algorithmObject = new algorithmObject(keypointsObject, descriptorsObject, objectName, description, "");
                            break;
                        case "ORB":
                            Object[] tempORB = OpenCVFunctions.computeORB(mImageBitmap);
                            keypointsObject = (MatOfKeyPoint)tempORB[0];
                            descriptorsObject = (Mat) tempORB[1];

                            System.err.println(Arrays.toString(keypointsObject.toArray()));
                            System.out.println(descriptorsObject);

                            algorithmObject = new algorithmObject(keypointsObject, descriptorsObject, objectName, description, "");
                            break;

                    }

                    Mat imgToDraw = new Mat();
                    Mat imgToDisplay = new Mat();
                    Utils.bitmapToMat(mImageBitmap, imgToDisplay);
                    Imgproc.cvtColor(imgToDisplay, imgToDisplay, Imgproc.COLOR_RGBA2GRAY);
                    Features2d.drawKeypoints(imgToDisplay, keypointsObject, imgToDraw);

                    Utils.matToBitmap(imgToDraw, mImageBitmap);
                    imageView.setImageBitmap(mImageBitmap);
                } else {
                    System.out.println("test");
                }


            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception e = result.getError();
                Toast.makeText(this, "Possible error is : " + e, Toast.LENGTH_SHORT).show();
            }

        }

    }


    private void storeImage(Bitmap image, File filePath) {
        System.out.println(filePath);
        if (filePath == null) {
            System.out.println("Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error accessing file: " + e.getMessage());
        }

    }

    private  File getOutputMediaFile(){
        File mediaStorageDir = new File(getApplicationContext().getFilesDir().getPath() + "/saved_images");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

private void showDialog(){
    final EditText field = new EditText(this);
    final AlertDialog dialog = new AlertDialog.Builder(this)
            .setMessage("Wprowadź opis obiektu:")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    description = field.getText().toString();
                    saveButton.setEnabled(true);
                }
            }).setCancelable(true).setView(field)
            .create();

    field.addTextChangedListener(new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence c, int i, int i2, int i3) {}
        @Override public void onTextChanged(CharSequence c, int i, int i2, int i3) {}

        @Override
        public void afterTextChanged(Editable editable) {
            // Will be called AFTER text has been changed.
            if (editable.toString().length() == 0){
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            } else {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }
        }
    });


    dialog.show();
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
}

private void showDialog1(){
    final EditText field = new EditText(this);

    final AlertDialog dialog = new AlertDialog.Builder(this)
            .setMessage("Wprowadź nazwę obiektu:")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    objectName = field.getText().toString();
                    saveButton.setEnabled(true);
                }
            }).setCancelable(true).setView(field)
            .create();

    field.addTextChangedListener(new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence c, int i, int i2, int i3) {}
        @Override public void onTextChanged(CharSequence c, int i, int i2, int i3) {}

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().length() == 0){
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            } else {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }
        }
    });

    dialog.show();
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
}


}