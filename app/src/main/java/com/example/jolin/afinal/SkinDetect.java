package com.example.jolin.afinal;

import android.graphics.Bitmap;
import android.graphics.Color;

public class SkinDetect extends Thread{
    private Bitmap mBitmap;
    private static int red, green, blue;
    private static float[] hsv = new float[3];

    public SkinDetect(Bitmap bitmap){
        mBitmap = bitmap;
        start();
    }

    @Override
    public void run() {
        System.out.println("Skin Detect START!");

        RGBskinDetection(mBitmap);
    }

    public static void RGBskinDetection(Bitmap bitmap){
        int x, y;
        int h = bitmap.getHeight();
        int w = bitmap.getWidth();

        for(x = 0; x < bitmap.getWidth(); x++){
            for(y = 0; y < bitmap.getHeight(); y++){
                int colour = bitmap.getPixel(x, y);

                red = Color.red(colour);
                green = Color.green(colour);
                blue = Color.blue(colour);

                if(red > 95 && blue > 20 && green > 40){
                    if(red > green && red > blue){
                        if(red - Math.min(green, blue) > 15){
                            if(Math.abs(red - green) > 15){
                                // System.out.println("Red: " + red + ", Green: " + green + ", Blue: " + blue);

                                Color.RGBToHSV(red, green, blue, hsv);
                                /*System.out.println("hsv!!!");
                                System.out.println(hsv[0]);
                                System.out.println(hsv[1]);
                                System.out.println(hsv[2]);*/

                                ReduceSaturation();
                                /*if(StartMuscle.getMove() > 25){
                                    ReduceSaturation();
                                }*/
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Skin Finish!!!");
    }

    private static void ReduceSaturation(){
        // System.out.println("hsv!!!");
        // System.out.println(hsv[1]);
        // hsv[1] /= 2.0;
    }
}
