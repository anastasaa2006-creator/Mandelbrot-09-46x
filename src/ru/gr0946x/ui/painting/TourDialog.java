package ru.gr0946x.ui.painting;

import ru.gr0946x.ui.fractals.KeyFrame;
import ru.gr0946x.ui.painting.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TourDialog extends JDialog {

    private final MainWindow mainWindow;
    private List<KeyFrame> keyframes = new ArrayList<>();
    private DefaultListModel<String> listModel;
    private JList<String> keyframeList;
    private JTextField durationField;

    public TourDialog(MainWindow mainWindow) {
        super(mainWindow, "Экскурсия по фракталу", true);
        this.mainWindow = mainWindow;
        setSize(550, 450);
        setLocationRelativeTo(mainWindow);
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        listModel = new DefaultListModel<>();
        keyframeList = new JList<>(listModel);
        keyframeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(keyframeList), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Управление"));

        controlPanel.add(new JLabel("Длительность (сек):"));
        durationField = new JTextField("2.0");
        controlPanel.add(durationField);

        JButton addButton = new JButton("Добавить текущий вид");
        addButton.addActionListener(e -> addCurrentFrame());
        controlPanel.add(addButton);

        JButton removeButton = new JButton("Удалить выбранный");
        removeButton.addActionListener(e -> removeFrame());
        controlPanel.add(removeButton);

        add(controlPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton startButton = new JButton("Начать экскурсию");
        startButton.addActionListener(e -> startTour());
        buttonPanel.add(startButton);

        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addCurrentFrame() {
        try {
            double duration = Double.parseDouble(durationField.getText().trim());
            if (duration < 0.5) duration = 0.5;

            KeyFrame frame = new KeyFrame(
                    mainWindow.getCurrentXMin(),
                    mainWindow.getCurrentXMax(),
                    mainWindow.getCurrentYMin(),
                    mainWindow.getCurrentYMax(),
                    duration
            );
            keyframes.add(frame);

            listModel.addElement(String.format("%d: [%.2f,%.2f] x [%.2f,%.2f] (%.1f сек)",
                    keyframes.size(),
                    frame.getXMin(), frame.getXMax(),
                    frame.getYMin(), frame.getYMax(),
                    duration
            ));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Введите число!");
        }
    }

    private void removeFrame() {
        int index = keyframeList.getSelectedIndex();
        if (index >= 0) {
            keyframes.remove(index);
            listModel.remove(index);
        }
    }

    private void startTour() {
        if (keyframes.size() < 2) {
            JOptionPane.showMessageDialog(this, "Добавьте минимум 2 кадра!");
            return;
        }
        new Thread(() -> mainWindow.animateTour(keyframes)).start();
        dispose();
    }

}