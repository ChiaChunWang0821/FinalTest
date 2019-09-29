package com.example.jolin.afinal;

import android.os.Bundle;

import java.util.ArrayList;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.sin;

public class analysis {
    public ArrayList array;
    int n;

    public static int bitReverse(int n, int bits) {
        int reversedN = n;
        int count = bits - 1;

        n >>= 1;
        while (n > 0) {
            reversedN = (reversedN << 1) | (n & 1);
            count--;
            n >>= 1;
        }
        return ((reversedN << count) & ((1 << bits) - 1));
    }

    static void fft(Complex[] buffer) {
        int bits = (int) (log(buffer.length) / log(2));
        for (int j = 1; j < buffer.length / 2; j++) {
            int swapPos = bitReverse(j, bits);
            Complex temp = buffer[j];
            buffer[j] = buffer[swapPos];
            buffer[swapPos] = temp;
        }

        for (int N = 2; N <= buffer.length; N <<= 1) {
            for (int i = 0; i < buffer.length; i += N) {
                for (int k = 0; k < N / 2; k++) {

                    int evenIndex = i + k;
                    int oddIndex = i + k + (N / 2);
                    Complex even = buffer[evenIndex];
                    Complex odd = buffer[oddIndex];

                    double term = (-2 * PI * k) / (double) N;
                    Complex exp = (new Complex(cos(term), sin(term)).mult(odd));

                    buffer[evenIndex] = even.add(exp);
                    buffer[oddIndex] = even.sub(exp);
                }
            }
        }
    }

    public double[] fftcalculate(  double input[]) {

        //double input[]={200,200,200,200,200,200,200,60};//140 140 140
        //double input[]={200,200,200,200,60,60,60,60};//365 0 151
        //double input[]={200,200,200,200,200,200,200,200};//nil
        //double input[]={200,60,60,60,60,60,60,60};//140 140 140


        double[] fftmath= new double[32];
        int count=0;
        Complex[] cinput = new Complex[input.length];
        for (int i = 0; i < input.length; i++)
            cinput[i] = new Complex(input[i], (0));

        fft(cinput);

        for (Complex c : cinput) {

            double m = Math.sqrt(Math.pow(c.im, 2) + Math.pow(c.re, 2));
            fftmath[count]=m;
            count++;
        }
        return fftmath;
    }
}

class Complex {
    public final double re;
    public final double im;

    public Complex() {
        this(0, 0);
    }

    public Complex(double r, double i) {
        re = r;
        im = i;
    }

    public Complex add(Complex b) {
        return new Complex(this.re + b.re, this.im + b.im);
    }

    public Complex sub(Complex b) {
        return new Complex(this.re - b.re, this.im - b.im);
    }

    public Complex mult(Complex b) {
        return new Complex(this.re * b.re - this.im * b.im,
                this.re * b.im + this.im * b.re);
    }

    @Override
    public String toString() {
        return String.format("(%f,%f)", re, im);
    }
}