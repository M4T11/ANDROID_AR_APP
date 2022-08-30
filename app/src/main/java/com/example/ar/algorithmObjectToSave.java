package com.example.ar;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class algorithmObjectToSave implements Serializable {


    private byte[] keyPoint;
    private int rowsKeypoints;
    private int columnsKeypoints;
    private int matTypeKeypoints;
    private byte[] descriptor;
    private int rowsDescriptors;
    private int columnsDescriptors;
    private int matTypeDescriptors;
    private String objectName;
    private String description;
    private String imgPath;

    algorithmObjectToSave(byte[] keyPoint, int rowsKeypoints, int columnsKeypoints, int matTypeKeypoints, byte[] descriptor, int rowsDescriptors, int columnsDescriptors, int matTypeDescriptors, String objectName, String description, String imgPath) {
        this.keyPoint = keyPoint;
        this.rowsKeypoints = rowsKeypoints;
        this.columnsKeypoints = columnsKeypoints;
        this.matTypeKeypoints = matTypeKeypoints;
        this.descriptor = descriptor;
        this.rowsDescriptors = rowsDescriptors;
        this.columnsDescriptors = columnsDescriptors;
        this.matTypeDescriptors = matTypeDescriptors;
        this.objectName = objectName;
        this.description = description;
        this.imgPath = imgPath;
    }

    void printAlgorithmObject() {

        System.out.println("keyPoint: " + keyPoint + ", " + descriptor + "description: " + description + "imgPath: " + imgPath);
    }

    Object[] returnObjects() {
        MatOfKeyPoint keyPoints = new MatOfKeyPoint();
        keyPoints.create(rowsKeypoints, columnsKeypoints, matTypeKeypoints);
        ByteBuffer buffer = ByteBuffer.wrap(keyPoint);
        FloatBuffer floatBuffer = buffer.asFloatBuffer();
        float[] floatArray = new float[floatBuffer.limit()];
        floatBuffer.get(floatArray);
        keyPoints.put(0, 0, floatArray);

        Mat descriptors = new Mat();
        descriptors.create(rowsDescriptors, columnsDescriptors, matTypeDescriptors);
        ByteBuffer bufferDescriptors = ByteBuffer.wrap(descriptor);
        FloatBuffer floatBufferDescriptors = bufferDescriptors.asFloatBuffer();
        float[] floatArrayDescriptors = new float[floatBufferDescriptors.limit()];
        floatBufferDescriptors.get(floatArrayDescriptors);
        descriptors.put(0, 0, floatArrayDescriptors);

        return new Object[]{keyPoints, descriptors};
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
