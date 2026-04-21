package ru.gr0946x.ui.fractals;

import ru.smak.math.Complex;

public class Julia implements Fractal {

    private final int maxIterations = 500;
    private final double R2 = 4.0;

    private double cReal;
    private double cImag;

    public Julia() {
        this.cReal = -0.8;
        this.cImag = 0.156;
    }


    public void setC(double real, double imag) {
        this.cReal = real;
        this.cImag = imag;
    }

    @Override
    public float inSetProbability(double x, double y) {
        Complex z = new Complex(x, y);
        Complex c = new Complex(cReal, cImag);

        int i = 0;
        while (z.getAbsoluteValue2() < R2 && ++i < maxIterations) {
            z.timesAssign(z);
            z.plusAssign(c);
        }

        return (float) i / maxIterations;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public double getR2() {
        return R2;
    }
}