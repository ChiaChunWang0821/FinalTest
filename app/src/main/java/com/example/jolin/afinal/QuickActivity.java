package com.example.jolin.afinal;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class QuickActivity extends AppCompatActivity {
    private SoundEffectPlayer soundEffectPlayer ;
    private SoundEffectPlayer soundEffectPlayer2 ;

    private ProgressBar quickbar;
    private ProgressBar bar;
    private ProgressBar bar2;
    private ProgressBar bar3;
    private ProgressBar bar4;
    private ProgressBar bar5;
    private ProgressBar bar6;
    private Long startTime;
    private Button start;
    private static Handler handler = new Handler();
    int count = 0;
    private ImageView logo;
    //private ImageView a;
    //private ImageView b;
    //private ImageView c;
    //private ImageView d;
    //private ImageView e;
    private ImageView win;
    private ImageView lose;
    private TextView time;
    private TextView note;
    private TextView text;
    double[] logs = new double[4];
    double[] longlogs =  new double[32];
    int fftcount = 0;
    int longfftcount=0;
    int v=0;
    int o=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick);

        /*----音效模組-----*/
        soundEffectPlayer = new SoundEffectPlayer( this ) ;
        soundEffectPlayer2 = new SoundEffectPlayer( this ) ;

        /*----將變數綁向layout中物件----*/
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        note = (TextView) findViewById(R.id.note);
        text = (TextView) findViewById(R.id.text);
        logo = (ImageView) findViewById(R.id.logo);
        //a = (ImageView) findViewById(R.id.a);
        //b = (ImageView) findViewById(R.id.b);
        //c = (ImageView) findViewById(R.id.c);
        //d = (ImageView) findViewById(R.id.d);
        //e = (ImageView) findViewById(R.id.e);
        win = (ImageView) findViewById(R.id.win);
        lose = (ImageView) findViewById(R.id.lose);


        quickbar = (ProgressBar) findViewById(R.id.quickbar);
        bar = (ProgressBar) findViewById(R.id.Bar2);
        bar3 = (ProgressBar) findViewById(R.id.bar3);
        bar4 = (ProgressBar) findViewById(R.id.bar4);
        bar5 = (ProgressBar) findViewById(R.id.bar5);
        bar6 = (ProgressBar) findViewById(R.id.bar6);

        quickbar.setProgress(0);
        quickbar.setMax(150);
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

        final Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundEffectPlayer2.play(R.raw.flamingo);
                handler.removeCallbacks(updateTimer);
                startTime = System.currentTimeMillis();
                count = 0;
                fftcount = 0;
                quickbar.setProgress(0);
                quickbar.setVisibility(View.VISIBLE);

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

                logo.setVisibility(View.INVISIBLE);
                //note.setVisibility(View.INVISIBLE);

                //a.setVisibility(View.INVISIBLE);
                //b.setVisibility(View.INVISIBLE);
                //c.setVisibility(View.INVISIBLE);
                //d.setVisibility(View.INVISIBLE);
                //e.setVisibility(View.INVISIBLE);
                win.setVisibility(View.INVISIBLE);
                lose.setVisibility(View.INVISIBLE);

                handler.post(updateTimer);

                start.setText("重新開始");

            }
        });
    }

    @Override
    protected void onDestroy() {
        //將執行緒銷毀掉
        handler.removeCallbacks(updateTimer);
        soundEffectPlayer2.stop();
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

            note.setText("");

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
                    note.append((int)a.fftcalculate(logs)[1]+" "+(int)a.fftcalculate(logs)[2]+" "+(int)a.fftcalculate(logs)[3]);

                   // if  ((move<25)|(move>40)){/*不動的情況*/
                        if  ((move<25)){
                        //note.setText(" ");
                       // note.append("not move");
                    }

                    else
            {/*有動的情況*/
                        note.append("move" + " ");


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
                    quickbar.setProgress(count);
                }


            handler.postDelayed(this, 150);
/*
            if (count > 20)
                a.setVisibility(View.VISIBLE);
            if (count > 35)
                b.setVisibility(View.VISIBLE);
            if (count > 50)
                c.setVisibility(View.VISIBLE);
            if (count > 65)
                d.setVisibility(View.VISIBLE);
            if (count > 100)
                e.setVisibility(View.VISIBLE);
*/
                if(seconds>25){
                    if(count<150){
                        lose.setVisibility(View.VISIBLE);
                        //a.setVisibility(View.INVISIBLE);
                        //b.setVisibility(View.INVISIBLE);
                        //c.setVisibility(View.INVISIBLE);
                        //d.setVisibility(View.INVISIBLE);
                        //e.setVisibility(View.INVISIBLE);
                        note.setVisibility(View.VISIBLE);

                        note.setText("好像差一點，再試一次？");
                        soundEffectPlayer2.stop();
                        soundEffectPlayer.play(R.raw.losesong);
                    }

                    handler.removeCallbacks(updateTimer);
                }

            if (count >= 150) {
                win.setVisibility(View.VISIBLE);
                //a.setVisibility(View.INVISIBLE);
                //b.setVisibility(View.INVISIBLE);
                //c.setVisibility(View.INVISIBLE);
                //d.setVisibility(View.INVISIBLE);
                //e.setVisibility(View.INVISIBLE);
                note.setVisibility(View.VISIBLE);

                note.setText("你超棒的，你比規定的時間早了 " + (26 - seconds) + "　秒完成。");
                soundEffectPlayer2.stop();
                soundEffectPlayer.play(R.raw.winsong);
                handler.removeCallbacks(updateTimer);
            }
        }
    };
}

