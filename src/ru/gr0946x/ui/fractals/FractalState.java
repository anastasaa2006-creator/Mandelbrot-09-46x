package ru.gr0946x.ui.fractals;

import java.io.Serializable;

public class FractalState implements Serializable {
    private final double xMin, xMax, yMin, yMax;
    private final int width, height;
    private final int maxIterations;

    public FractalState(double xMin, double xMax, double yMin, double yMax, int width, int height, int maxIterations) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.width = width;
        this.height = height;
        this.maxIterations = maxIterations;
    }

    public double getXMin() { return xMin; }
    public double getXMax() { return xMax; }
    public double getYMin() { return yMin; }
    public double getYMax() { return yMax; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getMaxIterations() { return maxIterations; }
}