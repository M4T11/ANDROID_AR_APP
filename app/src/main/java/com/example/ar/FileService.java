package com.example.ar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class FileService {

    public static void saveAlgorithmObjects(ArrayList<algorithmObject> algorithmObjectsArrayList, String filename, Context context) {

        ArrayList<algorithmObjectToSave> algorithmObjectsToSaveArrayList = new ArrayList<>();

        for(algorithmObject algorithmObject : algorithmObjectsArrayList) {

            float[] data = new float[(int) algorithmObject.getKeyPoint().total() * algorithmObject.getKeyPoint().channels()];
            algorithmObject.getKeyPoint().get(0, 0, data);
            ByteBuffer buffer = ByteBuffer.allocate(data.length * 8);
            for (int i = 0; i < data.length; i++) {
                buffer.putFloat(data[i]);
            }
            byte[] keypointsArray = buffer.array();

            float[] data1 = new float[(int) algorithmObject.getDescriptor().total() * algorithmObject.getDescriptor().channels()];
            algorithmObject.getDescriptor().get(0, 0, data1);
            ByteBuffer buffer1 = ByteBuffer.allocate(data1.length * 4);
            for (int i = 0; i < data1.length; i++) {
                buffer1.putFloat(data1[i]);
            }
            byte[] descriptorsArray = buffer1.array();

            algorithmObjectToSave algorithmObjectToSave = new algorithmObjectToSave(keypointsArray, algorithmObject.getKeyPoint().rows(), algorithmObject.getKeyPoint().cols(), algorithmObject.getKeyPoint().type(), descriptorsArray, algorithmObject.getDescriptor().rows(), algorithmObject.getDescriptor().cols(), algorithmObject.getDescriptor().type(), algorithmObject.getObjectName(), algorithmObject.getDescription(), algorithmObject.getImgPath());
            algorithmObjectsToSaveArrayList.add(algorithmObjectToSave);
        }

        try {

            FileOutputStream f = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(algorithmObjectsToSaveArrayList);

            o.close();
            f.close();
            System.out.println("Zapisalem");

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveOrbObjects(ArrayList<algorithmObject> algorithmObjectsArrayList, String filename, Context context) {

        ArrayList<OrbObjectToSave> orbObjectsToSaveArrayList = new ArrayList<>();

        for(algorithmObject algorithmObject : algorithmObjectsArrayList) {

            float[] data = new float[(int) algorithmObject.getKeyPoint().total() * algorithmObject.getKeyPoint().channels()];
            algorithmObject.getKeyPoint().get(0, 0, data);
            ByteBuffer buffer = ByteBuffer.allocate(data.length * 8);
            for (int i = 0; i < data.length; i++) {
                buffer.putFloat(data[i]);
            }
            byte[] keypointsArray = buffer.array();

            byte[] data1 = new byte[(int) algorithmObject.getDescriptor().total() * algorithmObject.getDescriptor().channels()];
            algorithmObject.getDescriptor().get(0, 0, data1);
            ByteBuffer buffer1 = ByteBuffer.allocate(data1.length * 4);
            for (int i = 0; i < data1.length; i++) {
                buffer1.putFloat(data1[i]);
            }
            byte[] descriptorsArray = buffer1.array();

            OrbObjectToSave orbObjectToSave = new OrbObjectToSave(keypointsArray, algorithmObject.getKeyPoint().rows(), algorithmObject.getKeyPoint().cols(), algorithmObject.getKeyPoint().type(), descriptorsArray, algorithmObject.getDescriptor().rows(), algorithmObject.getDescriptor().cols(), algorithmObject.getDescriptor().type(), algorithmObject.getObjectName(), algorithmObject.getDescription(), algorithmObject.getImgPath());
            orbObjectsToSaveArrayList.add(orbObjectToSave);
        }

        try {

            FileOutputStream f = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(orbObjectsToSaveArrayList);

            o.close();
            f.close();
            System.out.println("Zapisalem");

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<algorithmObject> loadAlgorithmObjects(String filename, Context context) {

        ArrayList<algorithmObject> algorithmObjectsArrayList = new ArrayList<>();
        ArrayList<algorithmObjectToSave> algorithmObjectsToSaveArrayList = new ArrayList<>();

        algorithmObjectToSave loadedObj = null;
        try {
            InputStream f = context.openFileInput(filename);
            ObjectInputStream o = new ObjectInputStream(f);

            algorithmObjectsToSaveArrayList = (ArrayList<algorithmObjectToSave>)o.readObject();
            o.close();
            f.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        for(algorithmObjectToSave algorithmObjectToSave : algorithmObjectsToSaveArrayList) {

            Object[] temp = algorithmObjectToSave.returnObjects();
            MatOfKeyPoint keypointsObject = (MatOfKeyPoint)temp[0];
            Mat descriptorsObject = (Mat) temp[1];

            algorithmObject algorithmObject = new algorithmObject(keypointsObject, descriptorsObject, algorithmObjectToSave.getObjectName(), algorithmObjectToSave.getDescription(), algorithmObjectToSave.getImgPath());
            algorithmObjectsArrayList.add(algorithmObject);

        }

        return algorithmObjectsArrayList;
    }

    public static ArrayList<algorithmObject> loadOrbObjects (String filename, Context context) {

        ArrayList<algorithmObject> algorithmObjectsArrayList = new ArrayList<>();
        ArrayList<OrbObjectToSave> orbObjectsToSaveArrayList = new ArrayList<>();

        OrbObjectToSave loadedObj = null;
        try {
            InputStream f = context.openFileInput(filename);
            ObjectInputStream o = new ObjectInputStream(f);

            orbObjectsToSaveArrayList = (ArrayList<OrbObjectToSave>)o.readObject();
            o.close();
            f.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        for(OrbObjectToSave orbObjectToSave : orbObjectsToSaveArrayList) {

            Object[] temp = orbObjectToSave.returnObjects();
            MatOfKeyPoint keypointsObject = (MatOfKeyPoint)temp[0];
            Mat descriptorsObject = (Mat) temp[1];

            algorithmObject algorithmObject = new algorithmObject(keypointsObject, descriptorsObject, orbObjectToSave.getObjectName(), orbObjectToSave.getDescription(), orbObjectToSave.getImgPath());
            algorithmObjectsArrayList.add(algorithmObject);

        }

        return algorithmObjectsArrayList;
    }

    public static Bitmap loadImageBitmap(Context context, String name, String extension){
        name = name + "." + extension;
        FileInputStream fileInputStream;
        Bitmap bitmap = null;
        try{
            fileInputStream = new FileInputStream(context.getFilesDir().getPath() + "/saved_images/" + name);
            System.out.println(fileInputStream.toString());
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap loadImageBitmapPath(String path){
        FileInputStream fileInputStream;
        Bitmap bitmap = null;
        try{
            fileInputStream = new FileInputStream(path);
            System.out.println(fileInputStream.toString());
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}