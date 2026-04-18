package org.example.gui;

import org.example.businesslogic.SelectionPolicy;

import javax.swing.*;
import java.awt.*;

public class SimulationFrame extends JFrame {
    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel rowPanel1;
    private JPanel rowPanel2;
    private JPanel rowPanel3;
    private JPanel rowPanel4;
    private JPanel rowPanel5;
    private JPanel rowPanel6;
    private JPanel rowPanel7;
    private JPanel rowPanel8;
    private JSpinner spClients;
    private JSpinner spQueues;
    private JSpinner spTimeLimit;
    private JSpinner spMinArrival;
    private JSpinner spMaxArrival;
    private JSpinner spMinService;
    private JSpinner spMaxService;
    private JComboBox<String> cbPolicy;
    private JButton btnStart;
    private JButton btnClear;
    private JPanel bottomPanel;
    private JPanel centerPanel;
    private JTextArea textAreaLog;
    private JTextPane textPaneStats;

    private QueueAnimationPanel animationPanel;


    public SimulationFrame() {
        setTitle("Queues Management Application");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        configureComponents();

        animationPanel = new QueueAnimationPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(animationPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void configureComponents() {
        spClients.setModel(new SpinnerNumberModel(4, 1, 100000, 1));
        spQueues.setModel(new SpinnerNumberModel(2, 1, 1000, 1));
        spTimeLimit.setModel(new SpinnerNumberModel(60, 1, 100000, 1));

        spMinArrival.setModel(new SpinnerNumberModel(2, 0, 100000, 1));
        spMaxArrival.setModel(new SpinnerNumberModel(30, 0, 100000, 1));

        spMinService.setModel(new SpinnerNumberModel(2, 1, 100000, 1));
        spMaxService.setModel(new SpinnerNumberModel(4, 1, 100000, 1));

        cbPolicy.setModel(new DefaultComboBoxModel<>(new String[]{
                "SHORTEST_QUEUE",
                "SHORTEST_TIME"
        }));
        cbPolicy.setSelectedItem("SHORTEST_TIME");

        textAreaLog.setEditable(false);
        textAreaLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        textAreaLog.setLineWrap(false);

        btnClear.addActionListener(e -> textAreaLog.setText(""));

        textPaneStats.setEditable(false);
        textPaneStats.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        textPaneStats.setText("Statistics will appear here...");
    }

    public int getNumberOfClients(){
        return (Integer) spClients.getValue();
    }

    public int getNumberOfServers(){
        return (Integer) spQueues.getValue();
    }

    public int getTimeLimit(){
        return (Integer) spTimeLimit.getValue();
    }

    public int getMinArrivalTime(){
        return (Integer) spMinArrival.getValue();
    }

    public int getMaxArrivalTime(){
        return (Integer) spMaxArrival.getValue();
    }

    public int getMinServiceTime(){
        return (Integer) spMinService.getValue();
    }

    public int getMaxServiceTime(){
        return (Integer) spMaxService.getValue();
    }

    public SelectionPolicy getSelectionPolicy(){
        String value = (String) cbPolicy.getSelectedItem();
        return SelectionPolicy.valueOf(value);
    }

    public boolean validateInput() {
        int clients = getNumberOfClients();
        int queues = getNumberOfServers();
        int timeLimit = getTimeLimit();
        int minArrival = getMinArrivalTime();
        int maxArrival = getMaxArrivalTime();
        int minService = getMinServiceTime();
        int maxService = getMaxServiceTime();

        if (clients <= 0 || queues <= 0 || timeLimit <= 0) {
            showErrorMessage("Clients, queues and time limit must be greater than 0.");
            return false;
        }

        if (minArrival > maxArrival) {
            showErrorMessage("Min arrival time cannot be greater than max arrival time.");
            return false;
        }

        if (minService > maxService) {
            showErrorMessage("Min service time cannot be greater than max service time.");
            return false;
        }

        if (maxArrival > timeLimit) {
            showErrorMessage("Max arrival time cannot be greater than simulation time limit.");
            return false;
        }

        return true;
    }

    public void addStartListener(Runnable action) {
        btnStart.addActionListener(e -> action.run());
    }

    public void setStartButtonEnabled(boolean enabled) {
        btnStart.setEnabled(enabled);
    }

    public void setSimulationData(String data) {
        textAreaLog.setText(data);
    }

    public void appendSimulationData(String data) {
        textAreaLog.append(data);
    }

    public void updateAnimation(java.util.List<org.example.model.Task> shoppingClients,
                                java.util.List<org.example.model.Server> servers,
                                int currentTime) {
        animationPanel.updateState(shoppingClients, servers, currentTime);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public void setStatistics(double avgWaiting, double avgService, int peakHour) {
        String stats = String.format(
                "Simulation finished\n\n" +
                        "Average waiting time : %.2f\n" +
                        "Average service time : %.2f\n" +
                        "Peak hour           : %d",
                avgWaiting, avgService, peakHour
        );

        textPaneStats.setText(stats);
    }
}
