package com.example.jolin.afinal;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {


    private Button button;
    private CameraSurfaceView mCameraSurfaceView;

    private Activity activity;
    String filePath;

    private String imageFilePath;
    private File image = null;
    private FileOutputStream fos = null;

    private Timer timer;
    private TimerTask timerTask;
    private Date date;

    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        getBundleData();

        initSet();
        initView();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mCameraSurfaceView.takePicture(activity, filePath);
                if(flag == false){
                    flag = true;
                    button.setText("停止");
                    getScreenShot();
                }
                else{
                    flag = false;
                    button.setText("拍照");
                    stopWork();
                }
            }
        });
    }

    private void initSet() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_take_pic);
    }


    private void initView() {
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        button = (Button) findViewById(R.id.takePic);
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            filePath = bundle.getString("url");
        }
        Log.d("checkpoint", "check filePath - " + filePath);
    }

    //將全螢幕畫面轉換成Bitmap
    private void getScreenShot()
    {
        timer = new Timer(true);
        timer.schedule(timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("YA");

                //藉由View來Cache全螢幕畫面後放入Bitmap
                View mView = getWindow().getDecorView();
                mView.setDrawingCacheEnabled(true); //设置能否缓存图片信息（drawing cache）
                mView.buildDrawingCache(); //如果能够缓存图片，则创建图片缓存
                Bitmap mFullBitmap = mView.getDrawingCache(); //如果图片已经缓存，返回一个bitmap

                //取得系統狀態列高度
                Rect mRect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(mRect);
                int mStatusBarHeight = mRect.top;

                //取得手機螢幕長寬尺寸
                int mPhoneWidth = getWindowManager().getDefaultDisplay().getWidth();
                int mPhoneHeight = getWindowManager().getDefaultDisplay().getHeight();

                //將狀態列的部分移除並建立新的Bitmap
                Bitmap mBitmap = Bitmap.createBitmap(mFullBitmap, 0, mStatusBarHeight, mPhoneWidth, mPhoneHeight - mStatusBarHeight);

                int bytes = mBitmap.getByteCount();

                ByteBuffer buf = ByteBuffer.allocate(bytes);
                mBitmap.copyPixelsToBuffer(buf);

                byte[] byteArray = buf.array();

                try {
                    image = createImageFile();
                    fos = new FileOutputStream(imageFilePath);
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //將Cache的畫面清除
                mView.destroyDrawingCache(); //释放缓存占用的资源
            }
        }, 0, 1000); //在0秒後執行此任務,每次間隔1秒

        // return mBitmap;
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

    private void stopWork(){
        timer.cancel();
        System.out.println("STOP");
    }
}
