package ru.gr0946x.ui.fractals;

import ru.smak.math.Complex;

public class Mandelbrot implements Fractal {

    private int maxIterations = 100;
    private final double R2 = 4;
    private final int DEFAULT_ITERATIONS = 100;
    private final int MAX_ALLOWED_ITERATIONS = 5000;
    private final int MIN_ALLOWED_ITERATIONS = 50;

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int iterations) {
        if (iterations >= MIN_ALLOWED_ITERATIONS && iterations <= MAX_ALLOWED_ITERATIONS) {
            this.maxIterations = iterations;
        }
    }

    public void updateIterationsByZoom(double zoomLevel) {
        double zoomFactor = 3.0 / zoomLevel;
        if (zoomFactor < 1.0) zoomFactor = 1.0;
        int newIterations = (int) (DEFAULT_ITERATIONS * (1 + 1.5 * Math.log(zoomFactor)));
        if (newIterations < MIN_ALLOWED_ITERATIONS) newIterations = MIN_ALLOWED_ITERATIONS;
        if (newIterations > MAX_ALLOWED_ITERATIONS) newIterations = MAX_ALLOWED_ITERATIONS;
        this.maxIterations = newIterations;
    }

    @Override
    public float inSetProbability(double x, double y) {
        var c = new Complex(x, y);
        var z = new Complex();
        int i = 0;
        while (z.getAbsoluteValue2() < R2 && ++i < maxIterations) {
            z.timesAssign(z);
            z.plusAssign(c);
        }
        return (float) i / maxIterations;
    }
}