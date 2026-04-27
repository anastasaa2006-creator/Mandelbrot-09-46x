package ru.gr0946x.ui;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.painting.Painter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SelectablePanel extends PaintPanel {

    private SelectedRect rect = null;
    private final ArrayList<SelectListener> selectHandlers = new ArrayList<>();

    private int lastMouseX, lastMouseY;
    private boolean panning = false;
    private Converter conv;

    public SelectablePanel(Painter painter, Converter conv) {
        super(painter);
        this.conv = conv;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    panning = true;
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                } else {
                    rect = new SelectedRect(e.getX(), e.getY());
                    paintSelectedRect();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    panning = false;
                } else {
                    paintSelectedRect();
                    if (rect != null) {
                        for (SelectListener handler : selectHandlers) {
                            handler.onSelect(new Rectangle(
                                    rect.getUpperLeft().x,
                                    rect.getUpperLeft().y,
                                    rect.getWidth(),
                                    rect.getHeight()
                            ));
                        }
                        rect = null;
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (panning) {
                    int dx = e.getX() - lastMouseX;
                    int dy = e.getY() - lastMouseY;
                    double xShift = (conv.getXMax() - conv.getXMin()) * dx / getWidth();
                    double yShift = (conv.getYMax() - conv.getYMin()) * dy / getHeight();
                    conv.setXShape(conv.getXMin() - xShift, conv.getXMax() - xShift);
                    conv.setYShape(conv.getYMin() - yShift, conv.getYMax() - yShift);
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                    repaint();
                } else {
                    paintSelectedRect();
                    if (rect != null) {
                        rect.setLastPoint(e.getX(), e.getY());
                    }
                    paintSelectedRect();
                }
            }
        });
    }

    public void addSelectListener(SelectListener listener) {
        selectHandlers.add(listener);
    }

    public void removeSelectListener(SelectListener listener) {
        selectHandlers.remove(listener);
    }

    private void paintSelectedRect() {
        Graphics g = getGraphics();
        if (g != null && rect != null) {
            g.setXORMode(Color.WHITE);
            g.setColor(Color.BLACK);
            g.drawRect(
                    rect.getUpperLeft().x,
                    rect.getUpperLeft().y,
                    rect.getWidth(),
                    rect.getHeight()
            );
            g.setPaintMode();
        }
    }
    public void updateConverter(Converter conv) {
        this.conv = conv;
    }
}