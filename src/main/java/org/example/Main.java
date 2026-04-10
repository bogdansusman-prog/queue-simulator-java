package org.example;

import org.example.businesslogic.SimulationManager;
import org.example.gui.SimulationFrame;

public class Main {
    public static void main(String[] args) {

        SimulationFrame frame = new SimulationFrame();

        frame.addStartListener(() -> {
            SimulationManager manager = new SimulationManager(
                    frame,
                    frame.getNumberOfServers(),
                    frame.getNumberOfClients(),
                    frame.getTimeLimit()
            );

            Thread t = new Thread(manager);
            t.start();
        });
    }
}