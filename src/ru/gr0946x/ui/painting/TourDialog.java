package ru.gr0946x.ui.painting;

import ru.gr0946x.ui.fractals.KeyFrame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TourDialog extends JDialog {

    private List<KeyFrame> keyframes = new ArrayList<>();
    private DefaultListModel<String> listModel;
    private JList<String> keyframeList;
    private JTextField durationField;

    public TourDialog(JFrame parent) {
        super(parent, "Экскурсия по фракталу", true);
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        // Список кадров
        listModel = new DefaultListModel<>();
        keyframeList = new JList<>(listModel);
        keyframeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(keyframeList), BorderLayout.CENTER);

        // Панель управления
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

        // Кнопки внизу
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
        // Заглушка - потом добавим реальные координаты
        listModel.addElement("Кадр " + (listModel.size() + 1));
    }

    private void removeFrame() {
        int index = keyframeList.getSelectedIndex();
        if (index >= 0) {
            listModel.remove(index);
        }
    }

    private void startTour() {
        if (listModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Добавьте хотя бы 2 кадра!");
            return;
        }
        if (listModel.size() < 2) {
            JOptionPane.showMessageDialog(this, "Добавьте минимум 2 кадра!");
            return;
        }
        JOptionPane.showMessageDialog(this, "Экскурсия начнётся!");
        dispose();
    }
}