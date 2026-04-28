package ru.gr0946x.ui.fractals;

import java.io.Serializable;

public class KeyFrame implements Serializable {
    private static final long serialVersionUID = 1L;

    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;
    private double durationSeconds;  // ← ИСПРАВЛЕНО

    public KeyFrame(double xMin, double xMax, double yMin, double yMax, double durationSeconds) {  // ← ИСПРАВЛЕНО
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.durationSeconds = durationSeconds;
    }

    public double getXMin() { return xMin; }
    public double getXMax() { return xMax; }
    public double getYMin() { return yMin; }
    public double getYMax() { return yMax; }
    public double getDurationSeconds() { return durationSeconds; }
}