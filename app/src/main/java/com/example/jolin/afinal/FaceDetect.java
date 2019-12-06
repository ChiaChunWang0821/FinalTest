package com.example.jolin.afinal;


import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.IOException;
import java.util.List;

public class FaceDetect {
    private static final String TAG = "FaceDetectionProcessor";
    private FirebaseVisionFaceDetector detector;
    private GraphicOverlay graphicOverlay;

    public  void FaceDetect(){
    }
    public void start(Bitmap bitmap) {
        System.out.println("------------Face Detect--------------");
        //FaceDetectionResultListener faceDetectionResultListener;
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .build();

        detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
        detector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        System.out.println("------------Face Detecting--------------");
                        getFaceResults(firebaseVisionFaces);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("------------Face Detecting Fail--------------");
                        Log.e(TAG, "fail detectInImage: " + e);
                    }
                });

    }
    /*.addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>(){
                        @Override
                        public void onSuccess(List<FirebaseVisionFace> firebaseVisionFace)
                        {
                            System.out.println("------------Face Detecting--------------");
                            getFaceResults(firebaseVisionFace);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("------------Face Detecting Fail--------------");
                    Log.e(TAG, "fail detectInImage: " + e);
                }
            });*/
    private void getFaceResults(List<FirebaseVisionFace> firebaseVisionFace) {
        int counter = 0;
        for(FirebaseVisionFace face : firebaseVisionFace){
            System.out.println("------------Face Detect Result--------------");
            System.out.println(counter);
            Rect rect = face.getBoundingBox();

            FaceGraphic faceGraphic = new FaceGraphic(graphicOverlay, face, CameraSource.CAMERA_FACING_FRONT);

            graphicOverlay.add(faceGraphic);

            counter = counter + 1;
        }

    }

    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: " + e);
        }
    }
}
