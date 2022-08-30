package com.example.ar;

import java.util.ArrayList;

public class MyObjects {

    public static MyObjects myObj;

    public MyObjects(){}

    public static MyObjects getInstance() {
        if (myObj == null) {
            myObj = new MyObjects();
        }

        return myObj;
    }

    static ArrayList<algorithmObject> SiftObjectsArrayList = new ArrayList<>();
    static ArrayList<algorithmObject> SurfObjectsArrayList = new ArrayList<>();
    static ArrayList<algorithmObject> FastObjectsArrayList = new ArrayList<>();
    static ArrayList<algorithmObject> OrbObjectsArrayList = new ArrayList<>();

    public static ArrayList<algorithmObject> getSiftObjectsArrayList() {
        return SiftObjectsArrayList;
    }

    public static void setSiftObjectsArrayList(ArrayList<algorithmObject> siftObjectsArrayList) {
        SiftObjectsArrayList = siftObjectsArrayList;
    }

    public static ArrayList<algorithmObject> getSurfObjectsArrayList() {
        return SurfObjectsArrayList;
    }

    public static void setSurfObjectsArrayList(ArrayList<algorithmObject> surfObjectsArrayList) {
        SurfObjectsArrayList = surfObjectsArrayList;
    }

    public static ArrayList<algorithmObject> getFastObjectsArrayList() {
        return FastObjectsArrayList;
    }

    public static void setFastObjectsArrayList(ArrayList<algorithmObject> fastObjectsArrayList) {
        FastObjectsArrayList = fastObjectsArrayList;
    }

    public static ArrayList<algorithmObject> getOrbObjectsArrayList() {
        return OrbObjectsArrayList;
    }

    public static void setOrbObjectsArrayList(ArrayList<algorithmObject> orbObjectsArrayList) {
        OrbObjectsArrayList = orbObjectsArrayList;
    }

}

