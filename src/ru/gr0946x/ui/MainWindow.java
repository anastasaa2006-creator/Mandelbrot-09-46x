package ru.gr0946x.ui;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.fractals.Fractal;
import ru.gr0946x.ui.fractals.Mandelbrot;
import ru.gr0946x.ui.painting.FractalPainter;
import ru.gr0946x.ui.painting.Painter;
import ru.gr0946x.model.FractalState;
import java.util.Stack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static java.lang.Math.*;

public class MainWindow extends JFrame {

    private final SelectablePanel mainPanel;
    private final Painter painter;
    private final Fractal mandelbrot;
    private final Converter conv;
    // Стеки для отмены и повтора действий
    private Stack<FractalState> undoStack = new Stack<>();
    private Stack<FractalState> redoStack = new Stack<>();
    private static final int MAX_UNDO_STEPS = 100;

    public MainWindow(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 650));
        mandelbrot = new Mandelbrot();
        conv = new Converter(-2.0, 1.0, -1.0, 1.0);
        painter = new FractalPainter(mandelbrot, conv, (value)->{
            if (value == 1.0) return Color.BLACK;
            var r = (float)abs(sin(5 * value));
            var g = (float)abs(cos(8 * value) * sin (3 * value));
            var b = (float)abs((sin(7 * value) + cos(15 * value)) / 2f);
            return new Color(r, g, b);
        });
        mainPanel = new SelectablePanel(painter);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.addSelectListener((r)->{
            // Сохраняем состояние перед изменением
            saveCurrentStateToUndo();
            clearRedoStack();

            var xMin = conv.xScr2Crt(r.x);
            var xMax = conv.xScr2Crt(r.x + r.width);
            var yMin = conv.yScr2Crt(r.y + r.height);
            var yMax = conv.yScr2Crt(r.y);
            conv.setXShape(xMin, xMax);
            conv.setYShape(yMin, yMax);
            mainPanel.repaint();
        });
        // Сохраняем начальное состояние для отмены действий
        saveCurrentStateToUndo();
        setContent();

        // Настройка горячих клавиш для отмены/повтора
        setupUndoRedoShortcuts();
    }

    private void setContent(){
        var gl = new GroupLayout(getContentPane());
        setLayout(gl);
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGap(8)
                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(8)
        );
        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGap(8)
                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(8)
        );
    }

    // ==================== МЕТОДЫ ДЛЯ ОТМЕНЫ ДЕЙСТВИЙ (UNDO/REDO) ====================

    private void saveCurrentStateToUndo() {
        // Получаем актуальные размеры из панели, а не из painter
        int currentWidth = mainPanel != null ? mainPanel.getWidth() : painter.getWidth();
        int currentHeight = mainPanel != null ? mainPanel.getHeight() : painter.getHeight();

        if (currentWidth <= 0) currentWidth = painter.getWidth();
        if (currentHeight <= 0) currentHeight = painter.getHeight();

        FractalState state = new FractalState(
                conv.getXMin(), conv.getXMax(),
                conv.getYMin(), conv.getYMax(),
                currentWidth, currentHeight
        );
        undoStack.push(state);

        while (undoStack.size() > MAX_UNDO_STEPS) {
            undoStack.remove(0);
        }
    }

    private void clearRedoStack() {
        redoStack.clear();
    }

    private void undo() {
        if (undoStack.size() <= 1) {
            JOptionPane.showMessageDialog(this,
                    "Нет действий для отмены",
                    "Отмена",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Сохраняем текущее состояние в redo
        FractalState currentState = new FractalState(
                conv.getXMin(), conv.getXMax(),
                conv.getYMin(), conv.getYMax(),
                mainPanel != null ? mainPanel.getWidth() : painter.getWidth(),
                mainPanel != null ? mainPanel.getHeight() : painter.getHeight()
        );
        redoStack.push(currentState);

        // Удаляем текущее состояние из undo и восстанавливаем предыдущее
        undoStack.pop();
        FractalState previousState = undoStack.peek();
        applyState(previousState);

        // Принудительно перерисовываем
        mainPanel.repaint();
        mainPanel.revalidate();
    }
    private void redo() {
        if (redoStack.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Нет действий для повтора",
                    "Повтор",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        FractalState stateToRedo = redoStack.pop();
        saveCurrentStateToUndo();
        applyState(stateToRedo);

        // Принудительно перерисовываем
        mainPanel.repaint();
        mainPanel.revalidate();
    }

    private void applyState(FractalState state) {
        conv.setXShape(state.getXMin(), state.getXMax());
        conv.setYShape(state.getYMin(), state.getYMax());
        painter.setWidth(state.getWidth());
        painter.setHeight(state.getHeight());

        // ВАЖНО: обновляем размеры панели mainPanel
        if (mainPanel != null) {
            mainPanel.setPreferredSize(new Dimension(state.getWidth(), state.getHeight()));
            mainPanel.revalidate();
        }
    }
    private void setupUndoRedoShortcuts() {
        // Ctrl+Z для отмены
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ctrl Z"), "undo");
        getRootPane().getActionMap().put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });

        // Ctrl+Y для повтора
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ctrl Y"), "redo");
        getRootPane().getActionMap().put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        });
    }
}