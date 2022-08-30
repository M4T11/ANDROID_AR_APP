package com.example.ar;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FastFeatureDetector;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.ORB;
import org.opencv.xfeatures2d.SIFT;
import org.opencv.imgproc.Imgproc;
import org.opencv.xfeatures2d.SURF;


public class OpenCVFunctions {

    public static Object[] computeSIFT(Bitmap bitmap) {
        Mat mat = new Mat();
        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);

        org.opencv.xfeatures2d.SIFT sift = SIFT.create();
        MatOfKeyPoint keypointsObject = new MatOfKeyPoint();
        Mat descriptorsObject = new Mat();

        sift.detectAndCompute(mat, new Mat(), keypointsObject, descriptorsObject);

        System.out.println(keypointsObject);
        System.out.println(descriptorsObject);

        return new Object[]{keypointsObject, descriptorsObject};
    }

    public static Object[] computeSURF(Bitmap bitmap) {
        Mat mat = new Mat();
        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);

        org.opencv.xfeatures2d.SURF surf = SURF.create();
        MatOfKeyPoint keypointsObject = new MatOfKeyPoint();
        Mat descriptorsObject = new Mat();

        surf.detectAndCompute(mat, new Mat(), keypointsObject, descriptorsObject);

        System.out.println(keypointsObject);
        System.out.println(descriptorsObject);

        return new Object[]{keypointsObject, descriptorsObject};
    }

    public static Object[] computeFAST(Bitmap bitmap) {
        Mat mat = new Mat();
        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);

        org.opencv.features2d.FastFeatureDetector fast = FastFeatureDetector.create();
        fast.setNonmaxSuppression(false);
        org.opencv.xfeatures2d.SURF surf = SURF.create();
        MatOfKeyPoint keypointsObject = new MatOfKeyPoint();
        Mat descriptorsObject = new Mat();
        fast.detect(mat, keypointsObject);
        surf.compute(mat,keypointsObject, descriptorsObject);

        System.out.println(keypointsObject);
        System.out.println(descriptorsObject);

        return new Object[]{keypointsObject, descriptorsObject};
    }

    public static Object[] computeORB(Bitmap bitmap) {
        Mat mat = new Mat();
        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);

        org.opencv.features2d.ORB orb = ORB.create();
        MatOfKeyPoint keypointsObject = new MatOfKeyPoint();
        Mat descriptorsObject = new Mat();

        orb.detectAndCompute(mat, new Mat(), keypointsObject, descriptorsObject);

        System.out.println(keypointsObject);
        System.out.println(descriptorsObject);

        return new Object[]{keypointsObject, descriptorsObject};
    }



}
