package com.example.jolin.afinal;

import android.os.Handler;

public class StartMuscle implements Runnable  {
    private Long startTime;
    private static Handler handler = new Handler();
    private int count = 0;
    private double[] logs = new double[4];
    private double[] longlogs =  new double[32];
    private int fftcount = 0;
    private int longfftcount = 0;
    private int v = 0;
    private int o = 0;
    private static double move = 1; /*判斷已沒有動的變數*/

    @Override
    public void run() {
        handler.removeCallbacks(updateTimer);
        startTime = System.currentTimeMillis();
        count = 0;
        fftcount = 0;

        handler.post(updateTimer);
    }

    protected void onDestroy() {
        //將執行緒銷毀掉
        handler.removeCallbacks(updateTimer);
    }

    public static double getMove(){
        return move;
    }

    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            Long spentTime = System.currentTimeMillis() - startTime;

            //計算目前已過分鐘數
            Long minutes = (spentTime / 1000) / 60;
            //計算目前已過秒數
            Long seconds = (spentTime / 1000) % 60;

            System.out.println("Time left : " + (26 - seconds));

            /*從session拿connect抓到的數字*/
            Session session = Session.getSession();

            int i = 0;
            i = Integer.parseInt((String) session.get("data"));

            /*過濾*/
            if(i > 100000){ // i = 199999
                i = i / 1000; // i = 199
            }
            else if(i < 100){ // i = 19
                o = i; // o = 19
            }

            else if((i > 10000) && (i < 100000)){ // i = 19999
                if(i % 1000 == v % 1000) {
                    o = i / 1000; // o = 19
                }
                else if(i % 100 == v % 100) {
                    o = i / 100; // o = 199
                }
            }
            else if((i > 1000) && (i < 10000)){ // i = 1999
                if(i % 100 == v % 100) {
                    o = i / 100; // o = 19
                }
                else if((i % 10 == v % 10) && (i % 100 != v % 100)) {
                    o = i / 10; // o = 199
                }
            }
            else if(i < 1000){ // i = 199
                if(i % 10 == v % 10) {
                    o = i / 10; // 19
                }
                else {
                    o = i; // i = 199
                }
            }

            System.out.println("力度:" + o);
            if(o > 100) {
                System.out.println("用力");
            }
            else {
                System.out.println("沒有用力");
            }
            v = i;
            /*過濾end*/

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

            for (int u = 0; u <= 2; u++) {
                fftcount++;
                logs[u] = logs[u + 1];
            }

            /*陣列2*/
            longlogs[31] = o;

            for(int v = 0; v <= 30; v++){
                longfftcount++;
                longlogs[v] = longlogs[v + 1];
            }

            /*開始*/
            move = 1; /*判斷已沒有動的變數*/
            if(longfftcount >= 64) { /*第二陣列滿*/

                analysis a = new analysis();
                move = a.fftcalculate(logs)[1];

                System.out.println(o + ">");
                System.out.println((int)a.fftcalculate(logs)[1] + " " + (int)a.fftcalculate(logs)[2] + " " + (int)a.fftcalculate(logs)[3]);

                // if  ((move<25)|(move>40)){/*不動的情況*/
                if((move < 25)){
                    System.out.println("not move");
                }
                else
                {/*有動的情況*/
                    System.out.println("move" + " ");

                    //longlogs
                    if((a.fftcalculate(longlogs)[1] > 300) && (a.fftcalculate(longlogs)[1] < 700)){
                        System.out.println("fast" + " ");

                        count++;
                    }

                    if((a.fftcalculate(longlogs)[2] > 300) && (a.fftcalculate(longlogs)[2] < 700)){
                        System.out.println("very fast" + " ");

                        count++;
                    }

                    if((a.fftcalculate(longlogs)[3] > 300) && (a.fftcalculate(longlogs)[3] < 700)){
                        System.out.println("very fast" + " ");

                        count++;
                    }
                }
            }

            handler.postDelayed(this, 150);

            if(seconds > 25){
                if(count < 150){
                    System.out.println("好像差一點，再試一次？");
                }

                handler.removeCallbacks(updateTimer);
            }

            if (count >= 150) {
                System.out.println("你超棒的，你比規定的時間早了 " + (26 - seconds) + "　秒完成。");
                handler.removeCallbacks(updateTimer);
            }
        }
    };
}
