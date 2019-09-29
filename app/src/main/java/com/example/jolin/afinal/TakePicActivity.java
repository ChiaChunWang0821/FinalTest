package com.example.jolin.afinal;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TakePicActivity extends AppCompatActivity {

    private ProgressBar powerbar;
    private Long startTime;
    private Button start;
    private static Handler handler = new Handler();
    int count = 0;
    private ImageView logo;
    private ImageView a;
    private ImageView b;
    private ImageView c;
    private ImageView d;
    private ImageView e;
    private ImageView win;
    private ImageView lose;
    private  TextView time;
    private  TextView note;
    int o;int v;

    private Button button;
    private CameraSurfaceView mCameraSurfaceView;

    private Activity activity;

    String filePath;

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
                mCameraSurfaceView.takePicture(activity, filePath);
            }
        });
    }

    private void initSet() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_take_pic);

        startTime = System.currentTimeMillis();
        count = 0;
        powerbar.setProgress(0);
        powerbar.setVisibility(View.VISIBLE);
        logo.setVisibility(View.INVISIBLE);
        //note.setVisibility(View.INVISIBLE);

        a.setVisibility(View.INVISIBLE);
        b.setVisibility(View.INVISIBLE);
        c.setVisibility(View.INVISIBLE);
        d.setVisibility(View.INVISIBLE);
        e.setVisibility(View.INVISIBLE);
        win.setVisibility(View.INVISIBLE);
        lose.setVisibility(View.INVISIBLE);

        handler.post(updateTimer);
    }


    private void initView() {
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        button = (Button) findViewById(R.id.takePic);

        // note = (TextView) findViewById(R.id.note);
        // logo = (ImageView) findViewById(R.id.logo);
        // a = (ImageView) findViewById(R.id.a);
        // b = (ImageView) findViewById(R.id.b);
        // c = (ImageView) findViewById(R.id.c);
        // d = (ImageView) findViewById(R.id.d);
        // e = (ImageView) findViewById(R.id.e);
        // win = (ImageView) findViewById(R.id.win);
        // lose = (ImageView) findViewById(R.id.lose);

        // TextView time = (TextView) findViewById(R.id.times);

        powerbar = (ProgressBar) findViewById(R.id.powerbar);
        powerbar.setProgress(0);
        powerbar.setMax(150);
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            filePath = bundle.getString("url");
        }
        Log.d("checkpoint", "check filePath - " + filePath);
    }


    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            // final TextView times = (TextView) findViewById(R.id.times);
            // times.setVisibility(View.VISIBLE);
            Long spentTime = System.currentTimeMillis() - startTime;
            //計算目前已過分鐘數
            Long minutes = (spentTime / 1000) / 60;
            //計算目前已過秒數
            Long seconds = (spentTime / 1000) % 60;

            // times.setText("Time left : "+(26-seconds));
            /*從session拿connect抓到的數字*/
            Session session = Session.getSession();

            int i = 0;
            i = Integer.parseInt((String) session.get("data"));

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

            v=i;

            if (o < 1000) {

                if (o > 120) {
                    count++;
                    if(o>400)
                        count++;}

                powerbar.setProgress(count);

            }
            note.setText(o+"/"+i);
            handler.postDelayed(this, 150);

            if(count>20)
                a.setVisibility(View.VISIBLE);
            if(count>40)
                b.setVisibility(View.VISIBLE);
            if(count>60)
                c.setVisibility(View.VISIBLE);
            if(count>80)
                d.setVisibility(View.VISIBLE);
            if(count>100)
                e.setVisibility(View.VISIBLE);


            if(seconds>25){
                if(count<150){
                    lose.setVisibility(View.VISIBLE);
                    a.setVisibility(View.INVISIBLE);
                    b.setVisibility(View.INVISIBLE);
                    c.setVisibility(View.INVISIBLE);
                    d.setVisibility(View.INVISIBLE);
                    e.setVisibility(View.INVISIBLE);
                    note.setVisibility(View.VISIBLE);

                    note.setText("好像差一點，再試一次？");
                    // soundEffectPlayer2.stop();
                    // soundEffectPlayer.play(R.raw.losesong);
                }

                handler.removeCallbacks(updateTimer);
            }

            if(count>=150){
                win.setVisibility(View.VISIBLE);
                a.setVisibility(View.INVISIBLE);
                b.setVisibility(View.INVISIBLE);
                c.setVisibility(View.INVISIBLE);
                d.setVisibility(View.INVISIBLE);
                e.setVisibility(View.INVISIBLE);
                note.setVisibility(View.VISIBLE);

                note.setText("你超棒的，你比規定的時間早了 "+(26-seconds)+"　秒完成。");
                // soundEffectPlayer2.stop();
                // soundEffectPlayer.play(R.raw.winsong);
                handler.removeCallbacks(updateTimer);
            }
        }


    };
}

