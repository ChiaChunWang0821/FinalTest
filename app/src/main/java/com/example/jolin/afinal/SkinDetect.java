package com.example.jolin.afinal;

import android.graphics.Bitmap;
import android.graphics.Color;

public class SkinDetect extends Thread{
    private Bitmap mBitmap;
    private static int red, green, blue;
    // private static float r, g, b;
    // private static float h, s, v;
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
        int[] graySkin = new int [h * w];

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
                                // graySkin[y + x * w] = 255;
                                // RGB2HSV();
                                Color.RGBToHSV(red, green, blue, hsv);
                                /*System.out.println("hsv!!!");
                                System.out.println(hsv[0]);
                                System.out.println(hsv[1]);
                                System.out.println(hsv[2]);*/

                                ReduceSaturation();
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Skin Finish!!!");
    }

    /*private static void RGB2HSV(){
        r = red;
        g = green;
        b = blue;

        float min = Math.min(g, b);
        float max = r;
        h = (g - b) / (max - min);
        h *= 60;
        if(h < 0) h = 360;
        s = (max - min) / max;
        v = max;

        System.out.println("h: " + h + ", s: " + s + ", v: " + v);

        ReduceSaturation();
    }*/

    private static void ReduceSaturation(){
        // s /= 10;
    }
}
