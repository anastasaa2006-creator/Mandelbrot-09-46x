//Для хранения ключевых кадров экскурсии
package ru.gr0946x.ui.fractals;
import java.io.Serializable;

public class KeyFrame implements Serializable {
    private static final long serialVersionUID = 1L;

    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;
    private int durationSeconds;

    public KeyFrame(double xMin, double xMax, double yMin, double yMax, int durationSeconds) {
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
    public int getDurationSeconds() { return durationSeconds; }

    public double getWidth() { return xMax - xMin; }
    public double getHeight() { return yMax - yMin; }

    @Override
    public String toString() {
        return String.format("X: [%.4f, %.4f] Y: [%.4f, %.4f] (%.1f сек)",
                xMin, xMax, yMin, yMax, durationSeconds);
    }
}