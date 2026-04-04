package org.example.gui;

import javax.swing.*;
import java.awt.*;

public class SimulationFrame extends JFrame {
    private JTextArea textArea;

    public SimulationFrame() {
        setTitle("Queues Management Application");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    public void setSimulationData(String data){
        textArea.setText(data);
    }
}
