package org.example.gui;

import javax.swing.*;
import java.awt.*;

public class SimulationFrame extends JFrame {
    private JTextArea textArea;
    private JTextField tfClients;
    private JTextField tfServers;
    private JTextField tfTimeLimit;
    private JButton startButton;

    public SimulationFrame() {
        setTitle("Queues Management Application");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //TOP PANEL INPUTS
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 5, 5));

        inputPanel.add(new JLabel("Clients: "));
        tfClients = new JTextField("4");
        inputPanel.add(tfClients);

        inputPanel.add(new JLabel("Servers: "));
        tfServers = new JTextField("2");
        inputPanel.add(tfServers);

        inputPanel.add(new JLabel("TimeLimit: "));
        tfTimeLimit = new JTextField("60");
        inputPanel.add(tfTimeLimit);

        startButton = new JButton("Start");
        inputPanel.add(startButton);

        textArea = new JTextArea();
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    public void setSimulationData(String data){
        textArea.setText(data);
    }
    public void appendSimulationData(String data){
        textArea.append(data);
    }
    public int getNumberOfClients() {
        return Integer.parseInt(tfClients.getText());
    }

    public int getNumberOfServers() {
        return Integer.parseInt(tfServers.getText());
    }

    public int getTimeLimit() {
        return Integer.parseInt(tfTimeLimit.getText());
    }
    public void addStartListener(Runnable action){
        startButton.addActionListener(e -> action.run());
    }
}
