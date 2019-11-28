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
    }

    private void initListener() {
        mBtnPic.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startMuscle();
                openCamera();
                // 實驗一秒最多可拍幾張

                /*if(flag == false){
                    flag = true;
                    mBtnPic.setText("停止");
                    timer = new Timer(true);
                    timer.schedule(timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("YA");

                            openCamera();
                        }
                    }, 0, 1000); //在0秒後執行此任務,每次間隔1秒
                }
                else{
                    flag = false;
                    mBtnPic.setText("拍照");
                    timer.cancel();
                    System.out.println("STOP");
                }*/
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

    private void startMuscle(){
        handler.removeCallbacks(updateTimer);
        startTime = System.currentTimeMillis();
        count = 0;
        fftcount = 0;
        startbar.setProgress(0);
        startbar.setVisibility(View.VISIBLE);

        bar.setProgress(0);
        bar.setVisibility(View.VISIBLE);
        bar3.setProgress(0);
        bar3.setVisibility(View.VISIBLE);
        bar4.setProgress(0);
        bar4.setVisibility(View.VISIBLE);
        bar5.setProgress(0);
        bar5.setVisibility(View.VISIBLE);
        bar6.setProgress(0);
        bar6.setVisibility(View.VISIBLE);

        win.setVisibility(View.INVISIBLE);
        lose.setVisibility(View.INVISIBLE);

        handler.post(updateTimer);
    }

    @Override
    protected void onDestroy() {
        //將執行緒銷毀掉
        handler.removeCallbacks(updateTimer);
        // soundEffectPlayer2.stop();
        super.onDestroy();
    }

    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            final TextView times = (TextView) findViewById(R.id.time);
            times.setVisibility(View.VISIBLE);
            Long spentTime = System.currentTimeMillis() - startTime;
            //計算目前已過分鐘數
            Long minutes = (spentTime / 1000) / 60;
            //計算目前已過秒數
            Long seconds = (spentTime / 1000) % 60;

            times.setText("Time left : " + (26 - seconds));
            /*從session拿connect抓到的數字*/
            Session session = Session.getSession();

            int i = 0;
            i = Integer.parseInt((String) session.get("data"));

            /*過濾*/
            if(i>100000) i=i/1000;

            else if(i<100){
                o=i;
            }

            else if((i>10000)&&(i<100000)){

                if(i%1000==v%1000)
                    o=i/1000;
                else if(i%100==v%100)
                    o=i/100;
            }
            else if((i>1000)&&(i<10000)){

                if(i%100==v%100)
                    o=i/100;

                else if((i%10==v%10)&&(i%100!=v%100))
                    o=i/10;
            }
            else if(i<1000){
                if(i%10==v%10)
                    o=i/10;
                else
                    o=i;
            }

            //bar.setProgress(o);

            text.setText("力度:"+o);
            if(o>100)
                text.append("用力");
            else
                text.append("沒有用力");
            v=i;
            /*過濾end*/

            // note.setText("");

            /*陣列1
            int w=0;
            if(o<=120)
                 w=0;
            if(o>120){
                w=1;
            }
            */

            /*陣列1*/

            logs[3] = o;

            int u;
            for (u = 0; u <= 2; u++) {
                fftcount++;
                logs[u] = logs[u + 1];
            }

            /*陣列2*/

            longlogs[31] = o;

            int v;
            for(v=0;v<=30;v++){

                longfftcount++;
                longlogs[v] = longlogs[v + 1];

                bar.setProgress((int)longlogs[0]);
                bar3.setProgress((int)longlogs[1]);
                bar4.setProgress((int)longlogs[2]);
                bar5.setProgress((int)longlogs[3]);
                bar6.setProgress((int)longlogs[4]);
            }



            /*開始*/
            double move=1;/*判斷已沒有動的變數*/
            if(longfftcount>=64) {/*第二陣列滿*/

                analysis a = new analysis();
                move = a.fftcalculate(logs)[1];

                //note.append(o+">");
                // note.append((int)a.fftcalculate(logs)[1]+" "+(int)a.fftcalculate(logs)[2]+" "+(int)a.fftcalculate(logs)[3]);

                // if  ((move<25)|(move>40)){/*不動的情況*/
                if  ((move<25)){
                    //note.setText(" ");
                    // note.append("not move");
                }

                else
                {/*有動的情況*/
                    // note.append("move" + " ");


                    //longlogs
                    if(( a.fftcalculate(longlogs)[1]>300)&&( a.fftcalculate(longlogs)[1]<700)){
                        //note.setText(" ");
                        //note.setText("fast" + " ");

                        count++;
                    }

                    if(( a.fftcalculate(longlogs)[2]>300)&&( a.fftcalculate(longlogs)[2]<700)){
                        //note.setText(" ");
                        //note.setText("very fast" + " ");

                        count++;
                    }

                    if(( a.fftcalculate(longlogs)[3]>300)&&( a.fftcalculate(longlogs)[3]<700)){

                        //note.setText(" ");
                        //note.setText("very fast" + " ");

                        count++;
                    }



                }
                startbar.setProgress(count);
            }

            handler.postDelayed(this, 150);

            if(seconds>25){
                if(count<150){
                    lose.setVisibility(View.VISIBLE);
                    // note.setVisibility(View.VISIBLE);

                    // note.setText("好像差一點，再試一次？");
                    // soundEffectPlayer2.stop();
                    // soundEffectPlayer.play(R.raw.losesong);
                }

                handler.removeCallbacks(updateTimer);
            }

            if (count >= 150) {
                win.setVisibility(View.VISIBLE);
                // note.setVisibility(View.VISIBLE);

                // note.setText("你超棒的，你比規定的時間早了 " + (26 - seconds) + "　秒完成。");
                // soundEffectPlayer2.stop();
                // soundEffectPlayer.play(R.raw.winsong);
                handler.removeCallbacks(updateTimer);
            }
        }
    };
}