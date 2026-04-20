package ru.gr0946x.ui.painting;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.fractals.ColorFunction;
import ru.gr0946x.ui.fractals.Fractal;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.awt.*;


public class FractalPainter implements Painter {

    private final Fractal fractal;
    private final Converter conv;
    private final ColorFunction colorFunction;
    private BufferedImage image;
    private volatile boolean isDrawing;

    @Override
    public int getWidth() {
        return conv.getWidth() + 1;  // компенсация: conv.getWidth() возвращает width-1
    }

    @Override
    public int getHeight() {
        return conv.getHeight() + 1; // компенсация: conv.getHeight() возвращает height-1
    }

    @Override
    public void setWidth(int width) {
        conv.setWidth(width);
    }

    @Override
    public void setHeight(int height) {
        conv.setHeight(height);
    }

    public FractalPainter(Fractal f, Converter conv, ColorFunction cf) {
        this.fractal = f;
        this.conv = conv;
        this.colorFunction = cf;
    }

    @Override
    public void paint(Graphics g) {
        var w = getWidth();   // теперь корректное значение
        var h = getHeight();  // теперь корректное значение

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                var x = conv.xScr2Crt(i);
                var y = conv.yScr2Crt(j);
                var res = fractal.inSetProbability(x, y);
                g.setColor(colorFunction.getColor(res));
                g.fillRect(i, j, 1, 1);  // один пиксель
            }
        }
    }

    private void renderRows(int startRow, int endRow, int width) {
        for (int y = startRow; y < endRow; y++) {
            for (int x = 0; x < width; x++) {
                double cr = conv.xScr2Crt(x);
                double ci = conv.yScr2Crt(y);
                float value = fractal.inSetProbability(cr, ci);
                Color color = colorFunction.getColor(value);
                image.setRGB(x, y, color.getRGB());
            }
        }
    }


}