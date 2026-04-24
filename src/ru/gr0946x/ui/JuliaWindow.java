package ru.gr0946x.ui;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.fractals.Julia;
import ru.gr0946x.ui.painting.FractalPainter;
import ru.gr0946x.ui.painting.Painter;

import javax.swing.*;
import java.awt.*;

public class JuliaWindow extends JFrame {

    private final PaintPanel panel;
    private final Julia julia;
    private final Converter conv;
    private final Painter painter;

    public JuliaWindow(double cReal, double cImag) {
        setTitle("Множество Жюлиа для c = " +
                String.format("%.4f", cReal) + " + " +
                String.format("%.4f", cImag) + "i");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(600, 600));
        setLocationRelativeTo(null);

        julia = new Julia();
        julia.setC(cReal, cImag);

        conv = new Converter(-2.0, 2.0, -2.0, 2.0);

        // Используем тот же FractalPainter (с многопоточностью)
        painter = new FractalPainter(julia, conv, (value) -> {
            if (value >= 0.999f) return Color.BLACK;
            var r = (float) Math.abs(Math.sin(5 * value));
            var g = (float) Math.abs(Math.cos(8 * value) * Math.sin(3 * value));
            var b = (float) Math.abs((Math.sin(7 * value) + Math.cos(15 * value)) / 2f);
            return new Color(r, g, b);
        });

        panel = new PaintPanel(painter);
        panel.setBackground(Color.WHITE);

        setContent();
    }

    private void setContent() {
        var gl = new GroupLayout(getContentPane());
        setLayout(gl);
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGap(8)
                .addComponent(panel, GroupLayout.DEFAULT_SIZE,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(8)
        );
        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGap(8)
                .addComponent(panel, GroupLayout.DEFAULT_SIZE,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(8)
        );
    }
}