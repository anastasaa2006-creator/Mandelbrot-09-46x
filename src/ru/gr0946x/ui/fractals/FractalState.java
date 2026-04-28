package ru.gr0946x.ui.fractals;

import java.io.Serializable;

public class FractalState implements Serializable {
    private static final long serialVersionUID = 1L;

    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;
    private int width;
    private int height;

    public FractalState(double xMin, double xMax, double yMin, double yMax, int width, int height) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.width = width;
        this.height = height;
    }

    public double getXMin() { return xMin; }
    public double getXMax() { return xMax; }
    public double getYMin() { return yMin; }
    public double getYMax() { return yMax; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}