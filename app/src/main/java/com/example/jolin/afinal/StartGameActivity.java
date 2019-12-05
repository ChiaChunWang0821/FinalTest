package com.example.jolin.afinal;

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
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartGameActivity extends AppCompatActivity {
    // private Handler mMainHandler;
    private ExecutorService mThreadPool;
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    private Activity activity;
    public static final int PermissionCode = 1000;
    public static final int GetPhotoCode = 1001;

    // private Button btnConnect;
    private Button btnDisconnect;
    private Button mBtnPic;
    private ImageView mShowImage;
    public static ImageView mShowReceiveImage;
    public static String imageFilePath = null;
    public static String imageFileReceivePath = null;
    private boolean isCameraPermission = false;

    private CameraTopRectView topView;
    private Camera mCamera;

    private Client client;

    private File photoFile = null;
    private File photoReceiveFile = null;

    private ProgressBar startbar;
    private ProgressBar bar;
    private ProgressBar bar2;
    private ProgressBar bar3;
    private ProgressBar bar4;
    private ProgressBar bar5;
    private ProgressBar bar6;
    private Long startTime;
    private ImageView win;
    private ImageView lose;
    private static Handler handler = new Handler();
    int count = 0;
    private TextView time;
    private TextView text;
    double[] logs = new double[4];
    double[] longlogs =  new double[32];
    int fftcount = 0;
    int longfftcount=0;
    int v=0;
    int o=0;

    private Timer timer;
    private TimerTask timerTask;
    private Date date;

    private boolean flag = false;

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
        initSet();
        initListener();
    }

    private void initView() {
        /*----將變數綁向layout中物件----*/
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // btnConnect = (Button) findViewById(R.id.connect);
        btnDisconnect = (Button) findViewById(R.id.disconnect);

        mBtnPic = (Button) findViewById(R.id.btn_take_pic);
        mShowImage = (ImageView) findViewById(R.id.show_image);
        mShowReceiveImage = (ImageView) findViewById(R.id.receive_image);

        mThreadPool = Executors.newCachedThreadPool();

        /*mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            }
        };*/

        startbar = (ProgressBar) findViewById(R.id.startbar);
        bar = (ProgressBar) findViewById(R.id.Bar2);
        bar3 = (ProgressBar) findViewById(R.id.bar3);
        bar4 = (ProgressBar) findViewById(R.id.bar4);
        bar5 = (ProgressBar) findViewById(R.id.bar5);
        bar6 = (ProgressBar) findViewById(R.id.bar6);
        win = (ImageView) findViewById(R.id.win);
        lose = (ImageView) findViewById(R.id.lose);
        text = (TextView) findViewById(R.id.text);

        startbar.setProgress(0);
        startbar.setMax(150);
        bar.setProgress(0);
        bar.setMax(600);
        bar3.setProgress(0);
        bar3.setMax(600);
        bar4.setProgress(0);
        bar4.setMax(600);
        bar5.setProgress(0);
        bar5.setMax(600);
        bar6.setProgress(0);
        bar6.setMax(600);
    }

    private void initSet(){
        try {
            photoFile = createImageFile();
            photoReceiveFile = createReceiveImageFile();
        } catch (IOException e) {
            Log.d("checkpoint", "error for createImageFile 創建路徑失敗");
            System.out.println("error for createImageFile 創建路徑失敗");
        }

        client = new Client();
        Toast.makeText(getApplicationContext(), "Connect SUCCESS!", Toast.LENGTH_LONG).show();

        cachedThreadPool.execute(new StartMuscle());
    }

    private void initListener() {
        mBtnPic.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 實驗一秒最多可拍幾張

                if(flag == false){
                    flag = true;
                    mBtnPic.setText("停止");
                    timer = new Timer(true);
                    timer.schedule(timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("YA");

                            openCamera();
                            // Client.checkMuscle();
                        }
                    }, 0, 1000); //在0秒後執行此任務,每次間隔1秒
                }
                else{
                    flag = false;
                    mBtnPic.setText("拍照");
                    timer.cancel();
                    System.out.println("STOP");
                }
            }
        });

        btnDisconnect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.stop();
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    //創造檔案名稱、和存擋路徑
    public File createReceiveImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_R" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );
        imageFileReceivePath = image.getAbsolutePath();
        return image;
    }

    private void openCamera() {
        //已獲得權限
        if (isCameraPermission) {
            /*File photoFile = null;
            File photoReceiveFile = null;
            try {
                photoFile = createImageFile();
                photoReceiveFile = createReceiveImageFile();
            } catch (IOException e) {
                Log.d("checkpoint", "error for createImageFile 創建路徑失敗");
            }*/
            //成功創建路徑的話
            if (photoFile != null) {
                Intent intent = new Intent(StartGameActivity.this, TakePicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", photoFile.getAbsolutePath());
                intent.putExtras(bundle);
                startActivityForResult(intent, GetPhotoCode);
            }

            //成功創建路徑的話
            /*if (photoReceiveFile != null) {
                Intent intent = new Intent(StartGameActivity.this, TakePicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", photoReceiveFile.getAbsolutePath());
                intent.putExtras(bundle);
                startActivityForResult(intent, GetPhotoCode);
            }*/
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
            setreceivePic(imageFileReceivePath);
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

    public void setreceivePic(String mCurrentPhotoPath) {
        // Get the dimensions of the View
        int targetW = mShowReceiveImage.getWidth();
        int targetH = mShowReceiveImage.getHeight();

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
        mShowReceiveImage.setImageBitmap(bitmap);
    }
}