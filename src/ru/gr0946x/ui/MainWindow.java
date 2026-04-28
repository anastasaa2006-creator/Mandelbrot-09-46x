package ru.gr0946x.ui;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.fractals.ColorScheme;
import ru.gr0946x.ui.fractals.Fractal;
import ru.gr0946x.ui.fractals.Mandelbrot;
import ru.gr0946x.ui.painting.FractalPainter;
import ru.gr0946x.ui.painting.Painter;
import ru.gr0946x.ui.fractals.FractalState;
import ru.gr0946x.ui.painting.TourDialog;


import java.io.File;
import java.util.Stack;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import static java.lang.Math.*;
import ru.gr0946x.ui.fractals.KeyFrame;
import java.util.List;

public class MainWindow extends JFrame {

    private final SelectablePanel mainPanel;
    private final Painter painter;
    private final Mandelbrot mandelbrot;
    private final Converter conv;

    private Stack<FractalState> undoStack = new Stack<>();
    private Stack<FractalState> redoStack = new Stack<>();
    private static final int MAX_UNDO_STEPS = 100;

    private static class DefaultColorScheme implements ColorScheme {
        @Override
        public Color getColor(float value) {
            if (value == 1.0) return Color.BLACK;
            float r = (float) Math.abs(Math.sin(5 * value));
            float g = (float) Math.abs(Math.cos(8 * value) * Math.sin(3 * value));
            float b = (float) Math.abs((Math.sin(7 * value) + Math.cos(15 * value)) / 2);
            return new Color(r, g, b);
        }
    }

    private static class RainbowScheme implements ColorScheme {
        @Override
        public Color getColor(float value) {
            if (value >= 0.999f) return Color.BLACK;
            float hue = value * 2.0f;
            if (hue > 1.0f) hue = 2.0f - hue;
            return new Color(Color.HSBtoRGB(hue, 1.0f, 1.0f));
        }
    }

    private static class HeatScheme implements ColorScheme {
        @Override
        public Color getColor(float value) {
            if (value >= 0.999f) return Color.BLACK;
            float r = Math.min(1.0f, value * 3.0f);
            float g = Math.min(1.0f, value * 1.5f);
            float b = 0;
            return new Color(r, g, b);
        }
    }

    public MainWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 650));
        mandelbrot = new Mandelbrot();
        conv = new Converter(-2.0, 1.0, -1.0, 1.0);

        painter = new FractalPainter(mandelbrot, conv, new DefaultColorScheme());

        mainPanel = new SelectablePanel(painter, conv);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.addSelectListener((r) -> {

            saveCurrentStateToUndo();
            clearRedoStack();

            double xMin = conv.xScr2Crt(r.x);
            double xMax = conv.xScr2Crt(r.x + r.width);
            double yMin = conv.yScr2Crt(r.y + r.height);
            double yMax = conv.yScr2Crt(r.y);

            double xRange = xMax - xMin;
            double yRange = yMax - yMin;
            double aspect = (double) getWidth() / getHeight();

            if (xRange / yRange > aspect) {
                double newYRange = xRange / aspect;
                double centerY = (yMin + yMax) / 2;
                yMin = centerY - newYRange / 2;
                yMax = centerY + newYRange / 2;
            } else {
                double newXRange = yRange * aspect;
                double centerX = (xMin + xMax) / 2;
                xMin = centerX - newXRange / 2;
                xMax = centerX + newXRange / 2;
            }

            conv.setXShape(xMin, xMax);
            conv.setYShape(yMin, yMax);

            double zoomLevel = conv.getXMax() - conv.getXMin();
            mandelbrot.updateIterationsByZoom(zoomLevel);

            mainPanel.repaint();
        });

        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    double x = conv.xScr2Crt(e.getX());
                    double y = conv.yScr2Crt(e.getY());
                    JuliaWindow jw = new JuliaWindow(x, y);
                    jw.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent e) {
                            conv.setXShape(-2.0, 1.0);
                            conv.setYShape(-1.0, 1.0);
                            mainPanel.repaint();
                        }
                    });
                    jw.setVisible(true);
                }
            }
        });
        createMenuBar();

        setContent();

        saveCurrentStateToUndo();
        setupUndoRedoShortcuts();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");

        JMenuItem saveFracItem = new JMenuItem("Сохранить как .frac...");
        saveFracItem.addActionListener(e -> saveFractalFile());
        fileMenu.add(saveFracItem);

        JMenuItem openFracItem = new JMenuItem("Открыть .frac...");
        openFracItem.addActionListener(e -> openFractalFile());
        fileMenu.add(openFracItem);

        fileMenu.addSeparator();

        JMenuItem saveJpgItem = new JMenuItem("Сохранить как JPG...");
        fileMenu.add(saveJpgItem);
        JMenuItem savePngItem = new JMenuItem("Сохранить как PNG...");
        fileMenu.add(savePngItem);

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Правка");
        JMenuItem undoItem = new JMenuItem("Отменить (Ctrl+Z)");
        undoItem.addActionListener(e -> undo());
        editMenu.add(undoItem);
        JMenuItem redoItem = new JMenuItem("Вернуть (Ctrl+Y)");
        redoItem.addActionListener(e -> redo());
        editMenu.add(redoItem);

        JMenu fractalMenu = new JMenu("Фрактал");

        JMenuItem juliaItem = new JMenuItem("Показать Жюлиа по точке...");
        juliaItem.addActionListener(e -> {
            String xInput = JOptionPane.showInputDialog(this, "Введите X (действительная часть)");
            if (xInput != null) {
                try {
                    double x = Double.parseDouble(xInput);
                    String yInput = JOptionPane.showInputDialog(this, "Введите Y (мнимая часть)");
                    if (yInput != null) {
                        double y = Double.parseDouble(yInput);
                        JuliaWindow jw = new JuliaWindow(x, y);
                        jw.setVisible(true);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Некорректный ввод координат");
                }
            }
        });
        fractalMenu.add(juliaItem);

        fractalMenu.addSeparator();

        JMenuItem colorSchemeItem = new JMenuItem("Цветовая схема");
        colorSchemeItem.addActionListener(e -> {
            ColorScheme[] schemes = {
                    new DefaultColorScheme(),
                    new RainbowScheme(),
                    new HeatScheme()
            };
            String[] names = {"По умолчанию", "Радужная", "Огненная"};
            int choice = JOptionPane.showOptionDialog(this, "Выберите цветовую схему", "Цветовая схема",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, names, names[0]);
            if (choice >= 0) {
                FractalPainter newPainter = new FractalPainter(mandelbrot, conv, schemes[choice]);
                mainPanel.setPainter(newPainter);
                mainPanel.repaint();
            }
        });
        fractalMenu.add(colorSchemeItem);

        JMenuItem iterationsItem = new JMenuItem("Настройка итераций...");
        iterationsItem.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this,
                    "Максимум итераций (50–5000):\nТекущее: " + mandelbrot.getMaxIterations(),
                    String.valueOf(mandelbrot.getMaxIterations()));
            if (input != null) {
                try {
                    int val = Integer.parseInt(input);
                    mandelbrot.setMaxIterations(val);
                    mainPanel.repaint();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Введите целое число!");
                }
            }
        });
        fractalMenu.add(iterationsItem);

        JMenu tourMenu = new JMenu("Экскурсия");
        JMenuItem startTourItem = new JMenuItem("Начать экскурсию...");
        startTourItem.addActionListener(e -> showTourDialog());
        tourMenu.add(startTourItem);

        JMenu helpMenu = new JMenu("Помощь");
        JMenuItem aboutItem = new JMenuItem("О программе");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(fractalMenu);
        menuBar.add(tourMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Mandelbrot Fractal\nВерсия 1.0\n\nРазработчики:\n• Анастасия А\n• Алина\n• Анеля\n• Анастасия М\n• Кянан\n• Каир",
                "О программе",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void setContent() {
        var gl = new GroupLayout(getContentPane());
        setLayout(gl);
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGap(8)
                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(8));
        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGap(8)
                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(8));
    }

    private void saveFractalFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Сохранить фрактал");
        chooser.setFileFilter(new FileNameExtensionFilter("Файлы фрактала (*.frac)", "frac"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();

            if (!path.toLowerCase().endsWith(".frac")) {
                file = new File(path + ".frac");
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                FractalState state = new FractalState(
                        conv.getXMin(), conv.getXMax(),
                        conv.getYMin(), conv.getYMax(),
                        painter.getWidth(), painter.getHeight(),
                        mandelbrot.getMaxIterations()
                );
                oos.writeObject(state);
                JOptionPane.showMessageDialog(this, "Фрактал сохранён!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка сохранения: " + ex.getMessage());
            }
        }
    }

    private void openFractalFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Открыть фрактал");
        chooser.setFileFilter(new FileNameExtensionFilter("Файлы фрактала (*.frac)", "frac"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                FractalState state = (FractalState) ois.readObject();
                saveCurrentStateToUndo();
                clearRedoStack();
                applyState(state);
                mandelbrot.setMaxIterations(state.getMaxIterations());
                mainPanel.repaint();
                JOptionPane.showMessageDialog(this, "Фрактал загружен!");
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка загрузки: " + ex.getMessage());
            }
        }
    }

    private void saveCurrentStateToUndo() {
        int currentWidth = mainPanel.getWidth();
        int currentHeight = mainPanel.getHeight();

        if (currentWidth <= 0) currentWidth = getWidth();
        if (currentHeight <= 0) currentHeight = getHeight();
        if (currentWidth <= 0) currentWidth = 800;
        if (currentHeight <= 0) currentHeight = 650;

        FractalState state = new FractalState(
                conv.getXMin(), conv.getXMax(),
                conv.getYMin(), conv.getYMax(),
                currentWidth, currentHeight,
                mandelbrot.getMaxIterations()
        );
        undoStack.push(state);
        while (undoStack.size() > MAX_UNDO_STEPS) undoStack.remove(0);
    }

    private void clearRedoStack() {
        redoStack.clear();
    }

    private void undo() {
        if (undoStack.size() <= 1) {
            JOptionPane.showMessageDialog(this, "Нет действий для отмены", "Отмена", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        FractalState currentState = new FractalState(
                conv.getXMin(), conv.getXMax(),
                conv.getYMin(), conv.getYMax(),
                mainPanel.getWidth(), mainPanel.getHeight(),
                mandelbrot.getMaxIterations()
        );
        redoStack.push(currentState);
        undoStack.pop();
        FractalState previousState = undoStack.peek();

        conv.setXShape(previousState.getXMin(), previousState.getXMax());
        conv.setYShape(previousState.getYMin(), previousState.getYMax());
        painter.setWidth(previousState.getWidth());
        painter.setHeight(previousState.getHeight());
        mainPanel.updateConverter(conv);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void redo() {
        if (redoStack.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Нет действий для повтора", "Повтор", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        FractalState stateToRedo = redoStack.pop();
        saveCurrentStateToUndo();
        applyState(stateToRedo);
        mainPanel.repaint();
    }

    private void applyState(FractalState state) {
        conv.setXShape(state.getXMin(), state.getXMax());
        conv.setYShape(state.getYMin(), state.getYMax());
        painter.setWidth(state.getWidth());
        painter.setHeight(state.getHeight());
        mainPanel.updateConverter(conv);
        mandelbrot.setMaxIterations(state.getMaxIterations());   // ← ЭТА СТРОКА
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void setupUndoRedoShortcuts() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl Z"), "undo");
        getRootPane().getActionMap().put("undo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { undo(); }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl Y"), "redo");
        getRootPane().getActionMap().put("redo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { redo(); }
        });
    }

    public double getCurrentXMin() {
        return conv.getXMin();
    }

    public double getCurrentXMax() {
        return conv.getXMax();
    }

    public double getCurrentYMin() {
        return conv.getYMin();
    }

    public double getCurrentYMax() {
        return conv.getYMax();
    }

    public void animateTour(List<KeyFrame> keyframes) {
        new Thread(() -> {
            for (int i = 0; i < keyframes.size() - 1; i++) {
                KeyFrame start = keyframes.get(i);
                KeyFrame end = keyframes.get(i + 1);

                double startXMin = start.getXMin();
                double startXMax = start.getXMax();
                double startYMin = start.getYMin();
                double startYMax = start.getYMax();

                double endXMin = end.getXMin();
                double endXMax = end.getXMax();
                double endYMin = end.getYMin();
                double endYMax = end.getYMax();

                int steps = 50;
                long stepDelay = (long) (start.getDurationSeconds() * 1000 / steps);

                for (int step = 0; step <= steps; step++) {
                    double t = (double) step / steps;

                    double xMin = startXMin + (endXMin - startXMin) * t;
                    double xMax = startXMax + (endXMax - startXMax) * t;
                    double yMin = startYMin + (endYMin - startYMin) * t;
                    double yMax = startYMax + (endYMax - startYMax) * t;

                    conv.setXShape(xMin, xMax);
                    conv.setYShape(yMin, yMax);
                    mainPanel.repaint();

                    try {
                        Thread.sleep(stepDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            JOptionPane.showMessageDialog(MainWindow.this, "Экскурсия завершена!");
        }).start();
    }

    private void showTourDialog() {
        TourDialog dialog = new TourDialog(this);
        dialog.setVisible(true);
    }
}