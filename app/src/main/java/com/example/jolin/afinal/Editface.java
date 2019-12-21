package com.example.jolin.afinal;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.io.IOException;
import java.util.List;

public class Editface implements Runnable {
    private static final String TAG = null;
    private Bitmap bitmap;
    private FirebaseVisionFaceDetector detector;
    private Resources pathname;
    private Bitmap  iconbitmap;
    private  int counter = 0;


    public Editface(Bitmap bitmap,Resources pathname){
        this.bitmap = bitmap;
        this.pathname = pathname;
        iconbitmap = BitmapFactory.decodeResource(pathname , R.drawable.light);
    }
    @Override
    public void run() {
        //startDetect(bitmap);
        toConformBitmap(bitmap,iconbitmap);
    }

    public void startDetect(Bitmap bitmap) {
        System.out.println("------------Face Detect--------------");
        //FaceDetectionResultListener faceDetectionResultListener;
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.FAST)
                        //.setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        //.setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .build();
        System.out.println("------------Face Detecting1--------------");
        detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
        System.out.println("------------Face Detecting2--------------");
        detector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        System.out.println("------------Face Detecting3--------------");
                        getFaceResults(firebaseVisionFaces);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("------------Face Detecting fail--------------");
                    }
                });

        /*private FaceDetector createFaceDetector(final Context context) {
            Log.d(TAG, "createFaceDetector called.");

            FaceDetector detector = new FaceDetector.Builder(context)
                    .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .setTrackingEnabled(true)
                    .setMode(FaceDetector.FAST_MODE)
                    .setProminentFaceOnly(mIsFrontFacing)
                    .setMinFaceSize(mIsFrontFacing ? 0.35f : 0.15f)
                    .build();

            MultiProcessor.Factory<FaceDetector.Face> factory = new MultiProcessor.Factory<FaceDetector.Face>() {
                @Override
                public Tracker<FaceDetector.Face> create(Face face) {
                    return new FaceTracker(mGraphicOverlay, context, mIsFrontFacing);
                }
            };

            Detector.Processor<Face> processor = new MultiProcessor.Builder<>(factory).build();
            detector.setProcessor(processor);

            if (!detector.isOperational()) {
                Log.w(TAG, "Face detector dependencies are not yet available.");

                // Check the device's storage.  If there's little available storage, the native
                // face detection library will not be downloaded, and the app won't work,
                // so notify the user.
                IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

                if (hasLowStorage) {
                    Log.w(TAG, getString(R.string.low_storage_error));
                    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.app_name)
                            .setMessage(R.string.low_storage_error)
                            .setPositiveButton(R.string.disappointed_ok, listener)
                            .show();
                }
            }
            return detector;
        }*/
    }
    private void getFaceResults(List<FirebaseVisionFace> firebaseVisionFace) {

        for(FirebaseVisionFace face : firebaseVisionFace){
            System.out.println("------------Face Detect Result--------------");
            System.out.println(counter);
            FirebaseVisionFaceLandmark landmark = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM);
            FirebaseVisionPoint point = landmark.getPosition();
            System.out.println(point);
            counter = counter + 1;
        }

    }

    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            //Log.e(TAG, "Exception thrown while trying to close Face Detector: " + e);
        }
    }

    public static Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
        System.out.println("start conform");
        if (background == null) {
            return null;
        }

        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        //create the new blank bitmap
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        //draw bg into
        cv.drawBitmap(background, 0, 0, null);//在 0，0座標開始畫入bg
        //draw fg into
        cv.drawBitmap(foreground, 0, 0, null);//從任意位置畫
        //save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);//保存
        //store
        cv.restore();//存储
        return newbmp;
    }

    public Bitmap getEditpic(){
        return bitmap;
    }


}

