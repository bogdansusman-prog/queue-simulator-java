package org.example.main;

import org.example.businesslogic.SimulationManager;
import org.example.gui.SimulationFrame;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulationFrame frame = new SimulationFrame();
            ExecutorService pool = Executors.newFixedThreadPool(1);

            frame.addStartListener(() -> {
                if (!frame.validateInput()) {
                    return;
                }

                frame.setStartButtonEnabled(false);
                frame.setSimulationData("");

                SimulationManager manager = new SimulationManager(
                        frame,
                        frame.getNumberOfServers(),
                        frame.getNumberOfClients(),
                        frame.getTimeLimit(),
                        frame.getMinArrivalTime(),
                        frame.getMaxArrivalTime(),
                        frame.getMinServiceTime(),
                        frame.getMaxServiceTime(),
                        frame.getSelectionPolicy()
                );

                pool.execute(() -> {
                    try {
                        manager.run();
                    } finally {
                        SwingUtilities.invokeLater(() -> frame.setStartButtonEnabled(true));
                    }
                });
            });

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    pool.shutdown();
                }
            });
        });
    }
}