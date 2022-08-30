package com.example.ar;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

import java.io.Serializable;

public class algorithmObject implements Serializable {


    private MatOfKeyPoint keyPoint;
    private Mat descriptor;
    private String objectName;
    private String description;
    private String imgPath;

    algorithmObject(MatOfKeyPoint keyPoint, Mat descriptor, String objectName, String description, String imgPath) {

        this.keyPoint = keyPoint;
        this.descriptor = descriptor;
        this.objectName = objectName;
        this.description = description;
        this.imgPath = imgPath;
    }

    void printAlgorithmObject() {

        System.out.println("keyPoint: " + keyPoint + ", " + descriptor + "objectName: " + objectName + "description: " + description + "imgPath: " + imgPath);
    }

    public MatOfKeyPoint getKeyPoint() {
        return keyPoint;
    }

    public void setKeyPoint(MatOfKeyPoint keyPoint) {
        this.keyPoint = keyPoint;
    }

    public Mat getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(Mat descriptor) {
        this.descriptor = descriptor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }


}
