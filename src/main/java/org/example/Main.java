package org.example;

import org.example.businesslogic.SimulationManager;
import org.example.gui.SimulationFrame;

public class Main {
    public static void main(String[] args) {
        SimulationFrame frame = new SimulationFrame();

        frame.addStartListener(() -> {
            try {
                int numberOfServers = frame.getNumberOfServers();
                int numberOfClients = frame.getNumberOfClients();
                int timeLimit = frame.getTimeLimit();

                if (numberOfServers <= 0 || numberOfClients <= 0 || timeLimit <= 0) {
                    frame.showErrorMessage("All values must be positive integers greater than 0.");
                    return;
                }

                frame.setStartButtonEnabled(false);

                SimulationManager manager = new SimulationManager(
                        frame,
                        numberOfServers,
                        numberOfClients,
                        timeLimit
                );

                Thread t = new Thread(() -> {
                    manager.run();
                    javax.swing.SwingUtilities.invokeLater(() -> frame.setStartButtonEnabled(true));
                });
                t.start();

            } catch (NumberFormatException e) {
                frame.showErrorMessage("Please enter only integer numbers.");
            }
        });
    }
}