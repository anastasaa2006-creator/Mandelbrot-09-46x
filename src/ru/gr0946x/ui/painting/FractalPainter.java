package ru.gr0946x.ui.painting;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.fractals.ColorFunction;
import ru.gr0946x.ui.fractals.Fractal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FractalPainter implements Painter {

    private final Fractal fractal;
    private final Converter conv;
    private final ColorFunction colorFunction;

    public FractalPainter(Fractal f, Converter conv, ColorFunction cf) {
        this.fractal = f;
        this.conv = conv;
        this.colorFunction = cf;
    }

    @Override
    public int getWidth() {
        return conv.getWidth() + 1;
    }

    @Override
    public int getHeight() {
        return conv.getHeight() + 1;
    }

    @Override
    public void setWidth(int width) {
        conv.setWidth(width);
    }

    @Override
    public void setHeight(int height) {
        conv.setHeight(height);
    }

    @Override
    public void paint(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        if (w <= 0 || h <= 0) return;

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        int procs = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(procs);

        for (int k = 0; k < procs; k++) {
            final int colorIndex = k;
            int startX = colorIndex * (w / procs);
            int endX = (colorIndex == procs - 1) ? w : startX + (w / procs);

            executor.submit(() -> {
                for (int x = startX; x < endX; x++) {
                    for (int y = 0; y < h; y++) {
                        double cr = conv.xScr2Crt(x);
                        double ci = conv.yScr2Crt(y);
                        float value = fractal.inSetProbability(cr, ci);
                        Color color = colorFunction.getColor(value);
                        image.setRGB(x, y, color.getRGB());
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        g.drawImage(image, 0, 0, null);
    }
}