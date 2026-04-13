package org.example;

import org.example.businesslogic.SimulationManager;
import org.example.gui.SimulationFrame;

public class Main{
    public static void main(String[] args){
        SimulationFrame frame = new SimulationFrame();

        frame.addStartListener(() ->{
            if(!frame.validateInput()){
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
            Thread t = new Thread(() -> {
                manager.run();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    frame.setStartButtonEnabled(true);
                });
            });
            t.start();
        });
    }
}