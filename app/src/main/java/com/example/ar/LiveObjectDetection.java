package com.example.ar;

import static org.opencv.features2d.DescriptorMatcher.BRUTEFORCE_HAMMING;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FastFeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.FlannBasedMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;
import org.opencv.xfeatures2d.SIFT;
import org.opencv.xfeatures2d.SURF;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LiveObjectDetection extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String TAG = "LiveObjectDetection";
    private static final int REQUEST_PERMISSION = 100;
    private int w, h;
    private CameraBridgeViewBase mOpenCvCameraView;
    TextView textView;
    Scalar RED = new Scalar(255, 0, 0);
    Scalar GREEN = new Scalar(0, 255, 0);
    DescriptorMatcher matcher;
    Mat descriptors2, descriptors1;

    MatOfKeyPoint keypoints1, keypoints2;
    org.opencv.xfeatures2d.SIFT sift;
    org.opencv.xfeatures2d.SURF surf;
    org.opencv.features2d.FastFeatureDetector fast;
    org.opencv.features2d.ORB orb;
    ArrayList<algorithmObject> SiftObjectsArrayList;
    ArrayList<algorithmObject> SurfObjectsArrayList;
    ArrayList<algorithmObject> FastObjectsArrayList;
    ArrayList<algorithmObject> OrbObjectsArrayList;


    private Mat imageSceneRgba;
    private Mat imageSceneGray;
    private Mat imageObjectGray;


    BottomSheetDialog bottomSheetDialog;
    MutableLiveData<Boolean> listen;
    Boolean isMatched;
    Button buttonCheckMatches;
    TextView textViewObjectName;
    TextView textViewDescription;
    Mat siftObjectImg;
    Mat outputMatchedImages;
    algorithmObject obj;
    String userAlgorithmChoiceString;
    String userMatcherChoiceString;
    String userObjectsChoiceString;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    try {
                        initializeDependencies();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private void initializeDependencies() throws IOException {
        mOpenCvCameraView.enableView();

        switch(userAlgorithmChoiceString) {
            case "SIFT":
                sift = SIFT.create();
                break;
            case "SURF":
                surf = SURF.create();
                break;
            case "FAST-SURF":
                fast = FastFeatureDetector.create();
                fast.setNonmaxSuppression(false);
                surf = SURF.create();
                break;
            case "ORB":
                orb = ORB.create();
                break;

        }

        switch(userMatcherChoiceString) {
            case "Brute-Force Matcher":
                if(!userAlgorithmChoiceString.equals("ORB")) {
                    matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_SL2);
                } else {
                    matcher = DescriptorMatcher.create(BRUTEFORCE_HAMMING);
                }
                break;
            case "FLANN Matcher":
                if(!userAlgorithmChoiceString.equals("ORB")) {
                    matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
                } else {
                    matcher = FlannBasedMatcher.create(BRUTEFORCE_HAMMING);
                }

                break;

        }

        imageObjectGray = new Mat();
        descriptors1 = new Mat();
        keypoints1 = new MatOfKeyPoint();

        if(!userObjectsChoiceString.equals("Wybierz obiekt")) {
            switch(userAlgorithmChoiceString) {
                case "SIFT":
                    obj = findObjectChoosed(SiftObjectsArrayList, userObjectsChoiceString);
                    siftObjectImg = new Mat();
                    Bitmap bitmap1 = FileService.loadImageBitmapPath(obj.getImgPath());
                    Utils.bitmapToMat(bitmap1, siftObjectImg);
                    Imgproc.cvtColor(siftObjectImg, siftObjectImg, Imgproc.COLOR_RGBA2GRAY);

                    descriptors1 = obj.getDescriptor();
                    keypoints1 = obj.getKeyPoint();

                    break;
                case "SURF":
                    obj = findObjectChoosed(SurfObjectsArrayList, userObjectsChoiceString);
                    siftObjectImg = new Mat();
                    Bitmap bitmap2 = FileService.loadImageBitmapPath(obj.getImgPath());
                    Utils.bitmapToMat(bitmap2, siftObjectImg);
                    Imgproc.cvtColor(siftObjectImg, siftObjectImg, Imgproc.COLOR_RGBA2GRAY);

                    descriptors1 = obj.getDescriptor();
                    keypoints1 = obj.getKeyPoint();
                    break;
                case "FAST-SURF":
                    obj = findObjectChoosed(FastObjectsArrayList, userObjectsChoiceString);
                    siftObjectImg = new Mat();
                    Bitmap bitmap3 = FileService.loadImageBitmapPath(obj.getImgPath());
                    Utils.bitmapToMat(bitmap3, siftObjectImg);
                    Imgproc.cvtColor(siftObjectImg, siftObjectImg, Imgproc.COLOR_RGBA2GRAY);

                    descriptors1 = obj.getDescriptor();
                    keypoints1 = obj.getKeyPoint();
                    break;
                case "ORB":
                    obj = findObjectChoosed(OrbObjectsArrayList, userObjectsChoiceString);
                    siftObjectImg = new Mat();
                    Bitmap bitmap4 = FileService.loadImageBitmapPath(obj.getImgPath());
                    Utils.bitmapToMat(bitmap4, siftObjectImg);
                    Imgproc.cvtColor(siftObjectImg, siftObjectImg, Imgproc.COLOR_RGBA2GRAY);
                    orb.detectAndCompute(siftObjectImg, new Mat(), keypoints1, descriptors1);

                    break;

            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_object_detection);
        SiftObjectsArrayList = MyObjects.getInstance().getSiftObjectsArrayList();
        SurfObjectsArrayList = MyObjects.getInstance().getSurfObjectsArrayList();
        FastObjectsArrayList = MyObjects.getInstance().getFastObjectsArrayList();
        OrbObjectsArrayList = MyObjects.getInstance().getOrbObjectsArrayList();

        SharedPreferences sharedPref = getSharedPreferences("Spinner",MODE_PRIVATE);
        userAlgorithmChoiceString = sharedPref.getString("userAlgorithmChoiceString",null);
        userMatcherChoiceString = sharedPref.getString("userMatcherChoiceString",null);
        userObjectsChoiceString = sharedPref.getString("userObjectsChoiceString",null);

        System.out.println(userAlgorithmChoiceString);
        System.out.println(userMatcherChoiceString);
        System.out.println(userObjectsChoiceString);

        outputMatchedImages = new Mat();


        bottomSheetDialog = new BottomSheetDialog(LiveObjectDetection.this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
        buttonCheckMatches = bottomSheetDialog.findViewById(R.id.buttonCheckMatches);
        textViewObjectName = bottomSheetDialog.findViewById(R.id.textViewObjectName);
        textViewDescription = bottomSheetDialog.findViewById(R.id.textViewDescription);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setDismissWithAnimation(true);

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                System.out.println("bottomSheetDialog Dissmissed");
                listen.postValue(false);
                isMatched = false;
            }
        });


        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        }

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        textView = (TextView) findViewById(R.id.text);


        if(userObjectsChoiceString.equals("Wybierz obiekt")) {
            textView.setText("Wybierz obiekt w ustawieniach!");
        } else {
            textView.setText(userAlgorithmChoiceString + " " + userMatcherChoiceString);
        }


        if (!OpenCVLoader.initDebug())
            Log.e("OpenCV", "Unable to load OpenCV!");
        else
            Log.d("OpenCV", "OpenCV loaded Successfully!");

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Unable to load OpenCV!");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV loaded Successfully!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        w = width;
        h = height;
    }

    public void onCameraViewStopped() {
    }


    public Mat recognizeChoosedObject() {

        descriptors2 = new Mat();
        keypoints2 = new MatOfKeyPoint();
        double threshold = 0.5;
        switch(userAlgorithmChoiceString) {
            case "SIFT":
                sift.detectAndCompute(imageSceneGray, new Mat(), keypoints2, descriptors2);
                break;
            case "SURF":
                surf.detectAndCompute(imageSceneGray, new Mat(), keypoints2, descriptors2);
                break;
            case "FAST-SURF":
                fast.detect(imageSceneGray, keypoints2);
                surf.compute(imageSceneGray,keypoints2, descriptors2);
                break;
            case "ORB":
                orb.detectAndCompute(imageSceneGray, new Mat(), keypoints2, descriptors2);
                threshold = 0.8;
                break;

        }

        // Matching
        List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
        if (!descriptors1.empty() && !descriptors2.empty()) {
            matcher.knnMatch(descriptors1, descriptors2, matches, 2);
        } else {
            return imageSceneRgba;

        }

        LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
        for (int i = 0; i < matches.size(); i++) {
            MatOfDMatch matofDMatch = matches.get(i);
            DMatch[] dmatcharray = matofDMatch.toArray();
            DMatch m1 = dmatcharray[0];
            DMatch m2 = dmatcharray[1];

            if (m1.distance <= m2.distance * threshold) {
                good_matches.addLast(m1);
                System.out.println("added good match");

            }
        }
        MatOfDMatch goodMatches = new MatOfDMatch();
        goodMatches.fromList(good_matches);
        MatOfByte drawnMatches = new MatOfByte();
        Mat outputImg = new Mat();
        if (imageSceneRgba.empty() || imageSceneRgba.cols() < 1 || imageSceneRgba.rows() < 1) {
            return imageSceneRgba;
        }

        Features2d.drawMatches(siftObjectImg, keypoints1, imageSceneGray, keypoints2, goodMatches, outputImg, GREEN, RED, drawnMatches, Features2d.NOT_DRAW_SINGLE_POINTS);
        Imgproc.resize(outputImg, outputImg, imageSceneGray.size());
        if (good_matches.size() > 7) {
            System.out.println("ZNALAZLEM ");
        }

        return outputImg;
    }


    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        imageSceneRgba = inputFrame.rgba();
        imageSceneGray = inputFrame.gray();
//        Imgproc.cvtColor(imageSceneRgba, imageSceneRgba, Imgproc.COLOR_RGBA2BGR);

        return recognizeChoosedObject();

    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    public algorithmObject findObjectChoosed(ArrayList<algorithmObject> ObjectArrayList, String objectName) {
        return ObjectArrayList.stream().filter(siftObject -> objectName.equals(siftObject.getObjectName())).findFirst().orElse(null);
    }

}