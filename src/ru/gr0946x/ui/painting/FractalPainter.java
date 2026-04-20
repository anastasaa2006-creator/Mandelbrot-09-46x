package ru.gr0946x.ui.painting;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.fractals.ColorFunction;
import ru.gr0946x.ui.fractals.Fractal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

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

        int procs = Runtime.getRuntime().availableProcessors();

        List<Thread> threads = new ArrayList<>();

        for (int k = 0; k < procs; k++) {
            final int colorIndex = k;
            int partWidth = w / procs + 1;

            BufferedImage bi = new BufferedImage(partWidth, h, BufferedImage.TYPE_INT_RGB);
            Graphics biGr = bi.getGraphics();

            Thread th = new Thread(() -> {
                for (int x = 0; x < partWidth; x++) {
                    for (int y = 0; y < h; y++) {
                        double cr = conv.xScr2Crt(colorIndex * (w / procs) + x);
                        double ci = conv.yScr2Crt(y);
                        float value = fractal.inSetProbability(cr, ci);
                        Color color = colorFunction.getColor(value);
                        biGr.setColor(color);
                        biGr.fillRect(x, y, 1, 1);
                    }
                }
                synchronized (g) {
                    g.drawImage(bi, colorIndex * (w / procs), 0, null);
                }
            });
            th.start();
            threads.add(th);
        }

        for (Thread th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}