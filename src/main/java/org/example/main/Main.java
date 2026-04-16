package org.example.main;

import org.example.businesslogic.SimulationManager;
import org.example.gui.SimulationFrame;

public class Main{
    public static void main(String[] args){
        SimulationFrame frame = new SimulationFrame();

        frame.addStartListener(() ->{
            if(!frame.validateInput()){
                return;
            }

            //pregatesc guiul
            frame.setStartButtonEnabled(false);
            frame.setSimulationData("");

            //motor simulare(consy
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
            //pornesc simularea pe alt thread
            Thread t = new Thread(() -> {
                manager.run();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    frame.setStartButtonEnabled(true);
                });
                //reactivez butonul
            });
            t.start();
        });
    }
}