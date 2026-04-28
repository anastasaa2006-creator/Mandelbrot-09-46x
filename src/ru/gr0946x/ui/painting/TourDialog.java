package ru.gr0946x.ui.painting;

import ru.gr0946x.ui.fractals.KeyFrame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TourDialog extends JDialog {

    private final MainWindow mainWindow;
    private List<KeyFrame> keyframes = new ArrayList<>();
    private JList<String> keyframeList;
    private DefaultListModel<String> listModel;

    public TourDialog(MainWindow mainWindow) {
        super(mainWindow, "Экскурсия по фракталу", true);
        this.mainWindow = mainWindow;
        setSize(500, 400);
        setLocationRelativeTo(mainWindow);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        listModel = new DefaultListModel<>();
        keyframeList = new JList<>(listModel);
        keyframeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(keyframeList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        JLabel label = new JLabel("Добавьте ключевые кадры для экскурсии", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        add(label, BorderLayout.NORTH);
    }
}