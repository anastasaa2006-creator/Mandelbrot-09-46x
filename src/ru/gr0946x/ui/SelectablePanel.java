package ru.gr0946x.ui;

import ru.gr0946x.ui.painting.Painter;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SelectablePanel extends PaintPanel {
    private SelectedRect rect = null;

    private final ArrayList<SelectListener> selectHandlers = new ArrayList<>();

    public void addSelectListener(SelectListener listener) {
        selectHandlers.add(listener);
    }

    public void removeSelectListener(SelectListener listener) {
        selectHandlers.remove(listener);
    }

    public SelectablePanel(Painter painter) {
        super(painter);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                rect = new SelectedRect(e.getX(), e.getY());
                paintSelectedRect();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                paintSelectedRect();
                if (rect != null) {
                    for (var handler : selectHandlers) {
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
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                paintSelectedRect();
                if (rect != null) {
                    rect.setLastPoint(e.getX(), e.getY());
                }
                paintSelectedRect();
            }
        });
    }

    private void paintSelectedRect() {
        // ВАЖНО: берём Graphics каждый раз заново, не кэшируем!
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
}