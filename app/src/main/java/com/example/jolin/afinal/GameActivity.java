package com.example.jolin.afinal;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GameActivity extends Activity {
    //擷取畫面按鈕
    private Button mBtn;
    //截圖的畫面
    private ImageView mImg;

    private String imageFilePath;
    private File image = null;
    private FileOutputStream fos = null;
    private RandomAccessFile rand = null;

    private String serverName = "140.121.197.165";
    private int serverPort = 5002;
    private Socket socket = null;
    private OutputStream os = null;
    private DataOutputStream dos = null;
    private FileInputStream fis = null;
    private byte[] buffer;
    private File file = null;
    // private RandomAccessFile rand = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //取得Button與ImageView元件
        mBtn = (Button) findViewById(R.id.btn);
        mImg = (ImageView) findViewById(R.id.img);

        //點擊按鈕觸發
        mBtn.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //將截圖Bitmap放入ImageView
                mImg.setImageBitmap(getScreenShot());
            }
        });
    }

    //將全螢幕畫面轉換成Bitmap
    private Bitmap getScreenShot()
    {
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
            // rand = new RandomAccessFile(image, "rw");
            // rand.write(byteArray);
            fos = new FileOutputStream(imageFilePath);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*try {
            client();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //將Cache的畫面清除
        mView.destroyDrawingCache(); //释放缓存占用的资源

        return mBitmap;
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

    void client() throws IOException {
        socket = new Socket(serverName, serverPort);
        os = socket.getOutputStream();
        try {
            file = new File(imageFilePath);
            rand = new RandomAccessFile(file, "r");
            // fis = new FileInputStream(StartGameActivity.imageFilePath);
            dos = new DataOutputStream(os);
            dos.writeInt((int)rand.length());
            System.out.println("Send image file length: " + (int)rand.length());

            buffer = new byte[(int)rand.length()];
            int count = 0;
            while(count < (int)rand.length()){
                count += rand.read(buffer, count, (int)rand.length() - count);
            }
            os.write(buffer);
            System.out.println("Send image to Server..." + count);

            // os.flush();
            rand.close();
            System.out.println("Send image FINISH.");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error " + e.getMessage());
            stop();
        }
    }

    public void stop() {
        try {
            os.close();
            // is.close();
            // dos.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error closing : " + e.getMessage());
        }
    }
}
