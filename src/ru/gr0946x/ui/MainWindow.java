package ru.gr0946x.ui;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.fractals.Fractal;
import ru.gr0946x.ui.fractals.Mandelbrot;
import ru.gr0946x.ui.painting.FractalPainter;
import ru.gr0946x.ui.painting.Painter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.lang.Math.*;

public class MainWindow extends JFrame {

    private final SelectablePanel mainPanel;
    private final Painter painter;
    private final Fractal mandelbrot;
    private final Converter conv;

    public MainWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 650));
        mandelbrot = new Mandelbrot();
        conv = new Converter(-2.0, 1.0, -1.0, 1.0);
        painter = new FractalPainter(mandelbrot, conv, (value) -> {
            if (value == 1.0) return Color.BLACK;
            var r = (float) abs(sin(5 * value));
            var g = (float) abs(cos(8 * value) * sin(3 * value));
            var b = (float) abs((sin(7 * value) + cos(15 * value)) / 2f);
            return new Color(r, g, b);
        });
        mainPanel = new SelectablePanel(painter, conv);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.addSelectListener((r) -> {
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
            mainPanel.repaint();
        });

        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    double x = conv.xScr2Crt(e.getX());
                    double y = conv.yScr2Crt(e.getY());
                    JuliaWindow jw = new JuliaWindow(x, y);
                    jw.setVisible(true);
                }
            }
        });

        createMenuBar();
        setContent();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");

        JMenuItem saveFracItem = new JMenuItem("Сохранить как .frac...");
        fileMenu.add(saveFracItem);

        JMenuItem openFracItem = new JMenuItem("Открыть .frac...");
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
        editMenu.add(undoItem);
        JMenuItem redoItem = new JMenuItem("Вернуть (Ctrl+Y)");
        editMenu.add(redoItem);

        JMenu viewMenu = new JMenu("Вид");
        JMenuItem zoomInItem = new JMenuItem("Увеличить");
        viewMenu.add(zoomInItem);
        JMenuItem zoomOutItem = new JMenuItem("Уменьшить");
        viewMenu.add(zoomOutItem);
        viewMenu.addSeparator();
        JMenuItem resetViewItem = new JMenuItem("Сбросить вид");
        viewMenu.add(resetViewItem);

        JMenu fractalMenu = new JMenu("Фрактал");
        JMenuItem juliaItem = new JMenuItem("Показать Жюлиа по точке...");
        fractalMenu.add(juliaItem);
        fractalMenu.addSeparator();
        JMenuItem colorSchemeItem = new JMenuItem("Цветовая схема");
        fractalMenu.add(colorSchemeItem);
        JMenuItem iterationsItem = new JMenuItem("Настройка итераций");
        fractalMenu.add(iterationsItem);

        JMenu tourMenu = new JMenu("Экскурсия");
        JMenuItem startTourItem = new JMenuItem("Начать экскурсию...");
        tourMenu.add(startTourItem);

        JMenu helpMenu = new JMenu("Помощь");
        JMenuItem aboutItem = new JMenuItem("О программе");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(fractalMenu);
        menuBar.add(tourMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Mandelbrot Fractal\nВерсия 1.0\n\nРазработчики:\n• Анастасия\n• Алина\n• Анеля\n• Настя\n• Кянан\n• Каир",
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
}