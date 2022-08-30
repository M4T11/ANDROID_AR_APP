package com.example.ar;

import static org.opencv.features2d.DescriptorMatcher.BRUTEFORCE_HAMMING;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FastFeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.FlannBasedMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;
import org.opencv.xfeatures2d.SIFT;
import org.opencv.xfeatures2d.SURF;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SingleFrameDetection extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String TAG = "SingleFrameDetection";
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
    Button searchButton;


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
    Mat img;
    algorithmObject algorithmObjectMatched;
    Mat outputMatchedImages;
    String userAlgorithmChoiceString;
    String userMatcherChoiceString;


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

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_frame_detection);
        SiftObjectsArrayList = MyObjects.getInstance().getSiftObjectsArrayList();
        SurfObjectsArrayList = MyObjects.getInstance().getSurfObjectsArrayList();
        FastObjectsArrayList = MyObjects.getInstance().getFastObjectsArrayList();
        OrbObjectsArrayList = MyObjects.getInstance().getOrbObjectsArrayList();

        SharedPreferences sharedPref = getSharedPreferences("Spinner",MODE_PRIVATE);
        userAlgorithmChoiceString = sharedPref.getString("userAlgorithmChoiceString",null);
        userMatcherChoiceString = sharedPref.getString("userMatcherChoiceString",null);

        System.out.println(userAlgorithmChoiceString);
        System.out.println(userMatcherChoiceString);

        isMatched = false;
        searchButton = findViewById(R.id.searchButton);
        outputMatchedImages = new Mat();


        bottomSheetDialog = new BottomSheetDialog(SingleFrameDetection.this);
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


        listen = new MutableLiveData<>();

        listen.setValue(false);

        listen.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean changedValue) {
                //Do something with the changed value
                if (changedValue && algorithmObjectMatched != null) {
                    textViewObjectName.setText("Dopasowany obiekt: " + algorithmObjectMatched.getObjectName());
                    textViewDescription.setText(algorithmObjectMatched.getDescription());

                    buttonCheckMatches.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isMatched) {
                                Imgproc.resize(outputMatchedImages, outputMatchedImages, new Size(640, 360));
                                Intent i = new Intent(getApplicationContext(), MatchedImagesDisplay.class);
                                Bitmap b = Bitmap.createBitmap(outputMatchedImages.cols(), outputMatchedImages.rows(), Bitmap.Config.ARGB_8888);
                                Utils.matToBitmap(outputMatchedImages, b, true);
                                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                                b.compress(Bitmap.CompressFormat.PNG, 50, bs);
                                i.putExtra("byteArray", bs.toByteArray());
                                startActivity(i);
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "buttonCheckMatches is Clicked ", Toast.LENGTH_LONG).show();
//                        bottomSheetDialog.dismiss();
                            }

                        }
                    });

                    bottomSheetDialog.show();
                }
            }
        });


        searchButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageSceneRgba != null) {
                    if (!imageSceneRgba.empty()) {
                        switch(userAlgorithmChoiceString) {
                            case "SIFT":
                                recognize(SiftObjectsArrayList);
                                break;
                            case "SURF":
                                recognize(SurfObjectsArrayList);
                                break;
                            case "FAST-SURF":
                                recognize(FastObjectsArrayList);
                                break;
                            case "ORB":
                                recognizeORB(OrbObjectsArrayList);
                                break;

                        }
                    }
                }
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

        textView.setText(userAlgorithmChoiceString + " " + userMatcherChoiceString);

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

    public Mat recognize(ArrayList<algorithmObject> ObjectArrayList) {

        descriptors2 = new Mat();
        keypoints2 = new MatOfKeyPoint();

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
                break;

        }

        int index = 0;
        int bestMatchindex = -1;
        int bestGoodMatches = -1;

        for (algorithmObject algorithmObject : ObjectArrayList) {
            descriptors1 = algorithmObject.getDescriptor();
            keypoints1 = algorithmObject.getKeyPoint();

            // Matching
            List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
            if(!descriptors1.empty() && !descriptors2.empty()) {
                matcher.knnMatch(descriptors1, descriptors2, matches,2);
            } else {
                return imageSceneRgba;
            }

            LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
            for (int i = 0; i < matches.size(); i++) {
                MatOfDMatch matofDMatch = matches.get(i);
                DMatch[] dmatcharray = matofDMatch.toArray();
                DMatch m1 = dmatcharray[0];
                DMatch m2 = dmatcharray[1];

                if (m1.distance <= m2.distance * 0.5) {
                    good_matches.addLast(m1);
                    System.out.println("added good match");

                }
            }

            if (good_matches.size() > bestGoodMatches) {
                bestGoodMatches = good_matches.size();
                bestMatchindex = index;
                System.out.println("bestGoodMatches" + bestGoodMatches);
                System.out.println("bestMatchindex" + bestMatchindex);
            }

            index++;
        }

        if (bestGoodMatches > 7) {
            System.out.println("ZNALAZLEM " + bestMatchindex);
            isMatched = true;
            listen.postValue(true);

            algorithmObject algorithmObject = ObjectArrayList.get(bestMatchindex);
            algorithmObjectMatched = algorithmObject;
            siftObjectImg = new Mat();
            Bitmap bitmap1 = FileService.loadImageBitmapPath(algorithmObject.getImgPath());
            Utils.bitmapToMat(bitmap1, siftObjectImg);
            Imgproc.cvtColor(siftObjectImg, siftObjectImg, Imgproc.COLOR_RGBA2GRAY);

            descriptors1 = algorithmObject.getDescriptor();
            keypoints1 = algorithmObject.getKeyPoint();

            // Matching
            List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
            //        if (matFrame.type() == aInputFrame.type()) {
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

                if (m1.distance <= m2.distance * 0.5) {
                    good_matches.addLast(m1);
                    System.out.println("added good match");

                }
            }

            MatOfDMatch goodMatches = new MatOfDMatch();
            goodMatches.fromList(good_matches);
            MatOfByte drawnMatches = new MatOfByte();
            if (imageSceneRgba.empty() || imageSceneRgba.cols() < 1 || imageSceneRgba.rows() < 1) {
                return imageSceneRgba;
            }
            Features2d.drawMatches(siftObjectImg, keypoints1, imageSceneGray, keypoints2, goodMatches, outputMatchedImages, GREEN, RED, drawnMatches, Features2d.NOT_DRAW_SINGLE_POINTS);
            Imgproc.resize(outputMatchedImages, outputMatchedImages, imageSceneGray.size());

        }

        return imageSceneRgba;
    }

    public Mat recognizeORB(ArrayList<algorithmObject> ObjectArrayList) {

        descriptors2 = new Mat();
        keypoints2 = new MatOfKeyPoint();

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
                break;

        }

        int index = 0;
        int bestMatchindex = -1;
        int bestGoodMatches = -1;

        for (algorithmObject algorithmObject : ObjectArrayList) {

            Bitmap bitmap1 = FileService.loadImageBitmapPath(algorithmObject.getImgPath());
            img = new Mat();
            Utils.bitmapToMat(bitmap1, img);
            Imgproc.cvtColor(img, img, Imgproc.COLOR_RGBA2GRAY);
            orb.detectAndCompute(img, new Mat(), keypoints1, descriptors1);

            // Matching
            List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
            if(!descriptors1.empty() && !descriptors2.empty()) {
                matcher.knnMatch(descriptors1, descriptors2, matches,2);
            } else {
                return imageSceneRgba;
            }

            LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
            for (int i = 0; i < matches.size(); i++) {
                MatOfDMatch matofDMatch = matches.get(i);
                DMatch[] dmatcharray = matofDMatch.toArray();
                DMatch m1 = dmatcharray[0];
                DMatch m2 = dmatcharray[1];

                if (m1.distance <= m2.distance * 0.8) {
                    good_matches.addLast(m1);
                    System.out.println("added good match");

                }
            }

            if (good_matches.size() > bestGoodMatches) {
                bestGoodMatches = good_matches.size();
                bestMatchindex = index;
                System.out.println("bestGoodMatches" + bestGoodMatches);
                System.out.println("bestMatchindex" + bestMatchindex);
            }

            index++;
        }

        if (bestGoodMatches > 3) {
            System.out.println("ZNALAZLEM " + bestMatchindex);
            isMatched = true;
            listen.postValue(true);

            algorithmObject algorithmObject = ObjectArrayList.get(bestMatchindex);
            algorithmObjectMatched = algorithmObject;
            siftObjectImg = new Mat();
            Bitmap bitmap1 = FileService.loadImageBitmapPath(algorithmObject.getImgPath());
            Utils.bitmapToMat(bitmap1, siftObjectImg);
            Imgproc.cvtColor(siftObjectImg, siftObjectImg, Imgproc.COLOR_RGBA2GRAY);

            orb.detectAndCompute(siftObjectImg, new Mat(), keypoints1, descriptors1);

            // Matching
            List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
            if (!descriptors1.empty() && !descriptors2.empty()) {
                matcher.knnMatch(descriptors1, descriptors2, matches,2);
            } else {
                return imageSceneRgba;

            }

            LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
            for (int i = 0; i < matches.size(); i++) {
                MatOfDMatch matofDMatch = matches.get(i);
                DMatch[] dmatcharray = matofDMatch.toArray();
                DMatch m1 = dmatcharray[0];
                DMatch m2 = dmatcharray[1];

                if (m1.distance <= m2.distance * 0.8) {
                    good_matches.addLast(m1);
                    System.out.println("added good match");

                }
            }

            MatOfDMatch goodMatches = new MatOfDMatch();
            goodMatches.fromList(good_matches);
            MatOfByte drawnMatches = new MatOfByte();
            if (imageSceneRgba.empty() || imageSceneRgba.cols() < 1 || imageSceneRgba.rows() < 1) {
                return imageSceneRgba;
            }
            Features2d.drawMatches(siftObjectImg, keypoints1, imageSceneGray, keypoints2, goodMatches, outputMatchedImages, GREEN, RED, drawnMatches, Features2d.NOT_DRAW_SINGLE_POINTS);
            Imgproc.resize(outputMatchedImages, outputMatchedImages, imageSceneGray.size());

        }

        return imageSceneRgba;
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        imageSceneRgba = inputFrame.rgba();
        imageSceneGray = inputFrame.gray();
//        Imgproc.cvtColor(imageSceneRgba, imageSceneRgba, Imgproc.COLOR_RGBA2BGR);

        if (isMatched) {
            return imageSceneRgba;
        }

        return imageSceneRgba;

    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }
}