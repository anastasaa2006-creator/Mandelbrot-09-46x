package ru.gr0946x.ui;

import ru.gr0946x.ui.painting.Painter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class PaintPanel extends JPanel {

    private Painter painter;

    public PaintPanel(Painter painter) {
        this.painter = painter;
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                painter.setWidth(getWidth());
                painter.setHeight(getHeight());
                repaint();
            }
        });
    }

    public void setPainter(Painter painter) {
        this.painter = painter;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        painter.paint(g);
    }
}