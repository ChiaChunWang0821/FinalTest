package com.example.jolin.afinal;

import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartGameActivity extends AppCompatActivity {
    private Handler mMainHandler;
    public Socket socket;
    private ExecutorService mThreadPool;

    private Activity activity;
    public static final int PermissionCode = 1000;
    public static final int GetPhotoCode = 1001;

    private Button btnConnect;
    private Button btnDisconnect;
    private Button mBtnPic;
    private ImageView mShowImage;
    private ImageView mShowreceiveImage;
    String imageFilePath;
    String imageFilereceivePath;
    private boolean isCameraPermission = false;

    private byte[] byteFile;
    private byte[] receivedData;

    private CameraTopRectView topView;
    private Camera mCamera;

    private String ReceivefilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        activity = this;
        initView();
        initListener();
    }

    private void initView() {
        btnConnect = (Button) findViewById(R.id.connect);
        btnDisconnect = (Button) findViewById(R.id.disconnect);

        mBtnPic = (Button) findViewById(R.id.btn_take_pic);
        mShowImage = (ImageView) findViewById(R.id.show_image);
        mShowreceiveImage = (ImageView) findViewById(R.id.receive_image);

        mThreadPool = Executors.newCachedThreadPool();

        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            }
        };
    }

    private void initListener() {
        btnConnect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 利用线程池直接开启一个线程 & 执行该线程
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        Client.connect();
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "Connect SUCCESS!", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                });
            }
        });

        mBtnPic.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        btnDisconnect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Client.disconnect();
                Toast.makeText(getApplicationContext(), "Connect ENDED!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionCode) {
            //假如允許了
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isCameraPermission = true;
                //do something
                Toast.makeText(this, "感謝賜予權限！", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(StartGameActivity.this, TakePicActivity.class), GetPhotoCode);
            }
            //假如拒絕了
            else {
                isCameraPermission = false;
                //do something
                Toast.makeText(this, "CAMERA權限FAIL，請給權限", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //創造檔案名稱、和存擋路徑
    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void openCamera() {
        //已獲得權限
        if (isCameraPermission) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.d("checkpoint", "error for createImageFile 創建路徑失敗");
            }
            //成功創建路徑的話
            if (photoFile != null) {
                Intent intent = new Intent(StartGameActivity.this, TakePicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", photoFile.getAbsolutePath());
                intent.putExtras(bundle);
                startActivityForResult(intent, GetPhotoCode);
            }
        }
        //沒有獲得權限
        else {
            getPermission();
        }
    }

    private void getPermission() {
        //檢查是否取得權限
        final int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        //沒有權限時
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            isCameraPermission = false;
            ActivityCompat.requestPermissions(StartGameActivity.this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PermissionCode);
        } else { //已獲得權限
            isCameraPermission = true;
            openCamera();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GetPhotoCode) {
            setPic(imageFilePath);
            setreceivePic(imageFilePath);
        }
    }

    private void setPic(String mCurrentPhotoPath) {
        // Get the dimensions of the View
        int targetW = mShowImage.getWidth();
        int targetH = mShowImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mShowImage.setImageBitmap(bitmap);
    }

    private void setreceivePic(String mCurrentPhotoPath) {
        // Get the dimensions of the View
        int targetW = mShowreceiveImage.getWidth();
        int targetH = mShowreceiveImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mShowreceiveImage.setImageBitmap(bitmap);
    }
}
