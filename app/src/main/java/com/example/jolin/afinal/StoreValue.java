package com.example.jolin.afinal;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;

import java.util.LinkedList;

public class StoreValue {
    MatOfKeyPoint keyPoints1;
    Mat descriptors1;
    Mat resultMat;
    LinkedList<Point> spKeyPoint1 = new LinkedList<>();
    //Point[] corner_dst;

    public StoreValue(MatOfKeyPoint keyPoints1, Mat descriptors1, Mat resultMat, LinkedList<Point> spKeyPoint1) {
        this.keyPoints1 = keyPoints1;
        this.descriptors1 = descriptors1;
        this.resultMat = resultMat;
        this.spKeyPoint1 = spKeyPoint1;
        //this.corner_dst = corner_dst;
    }
}
