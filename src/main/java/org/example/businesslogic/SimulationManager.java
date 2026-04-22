package org.example.businesslogic;

import org.example.gui.SimulationFrame;
import org.example.model.Task;
import org.example.model.Server;

import java.util.*;
import java.io.*;

public class SimulationManager implements Runnable {
    public int timeLimit = 60;
    public int maxProcessingTime = 4;
    public int minProcessingTime = 2;
    public int numberOfServers = 2;
    public int numberOfClients = 4;
    public int minArrivalTime = 2;
    public int maxArrivalTime = 30;
    public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;

    //componenta care gestioneaza cozile si serverele
    private final Scheduler scheduler;
    //referinta catre interfata grafica
    private final SimulationFrame frame;
    //taskurile care nu ajuns inca in sistem
    private final List<Task> generatedTasks;
    //totate taskurile generate, folosite pentru statistici finale
    private final List<Task> allTasks;

    //stats pentru simulare
    private double averageServiceTime;
    private int peakHour;
    private int peakClients;

    public SimulationManager(SimulationFrame frame,
                             int servers,
                             int clients,
                             int timeLimit,
                             int minArrivalTime,
                             int maxArrivalTime,
                             int minProcessingTime,
                             int maxProcessingTime,
                             SelectionPolicy selectionPolicy) {
        this.frame = frame;
        this.numberOfServers = servers;
        this.numberOfClients = clients;
        this.timeLimit = timeLimit;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minProcessingTime = minProcessingTime;
        this.maxProcessingTime = maxProcessingTime;
        this.selectionPolicy = selectionPolicy;

        //initializeaza scheduler si strategia de repartizare
        scheduler = new Scheduler(numberOfServers);
        scheduler.changeStrategy(selectionPolicy);

        generatedTasks = new ArrayList<>();
        allTasks = new ArrayList<>();
        generateNRandomTasks();
    }

    private void generateNRandomTasks() {
        Random random = new Random();
        int totalServiceTime = 0;

        //genereaza task-uri random in funstie de intervalele introduse
        for (int i = 1; i <= numberOfClients; i++) {
            int serviceTime = random.nextInt(maxProcessingTime - minProcessingTime + 1) + minProcessingTime;
            int arrivalTime = random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;

            Task task = new Task(i, arrivalTime, serviceTime);
            generatedTasks.add(task);
            allTasks.add(task);

            totalServiceTime += serviceTime;
        }

        //sorteaza task-urile dupa timpul de sosire
        generatedTasks.sort(Comparator.comparingInt(Task::getArrivalTime));

        //calculeaza timpul mediu de servire
        averageServiceTime = (double) totalServiceTime / numberOfClients;
    }

    private String formatState(int currentTime) {
        StringBuilder result = new StringBuilder();

        //construieste starea curenta a simularii pentru afisare in log
        result.append("Time ").append(currentTime).append("\n");
        result.append("Waiting clients: ");

        if (generatedTasks.isEmpty()) {
            result.append("none");
        } else {
            for (Task task : generatedTasks) {
                result.append(task).append(" ");
            }
        }

        result.append("\n");

        int index = 1;
        for (Server server : scheduler.getServers()) {
            result.append("Queue ").append(index).append(": ").append(server).append("\n");
            index++;
        }

        result.append("\n");
        return result.toString();
    }

    private void updatePeakHour(int currentTime) {
        //actualizeaza ora de varf daca numarul curent de clienti este maxim
        int totalClients = scheduler.getTotalClientsInQueues();
        if (totalClients > peakClients) {
            peakClients = totalClients;
            peakHour = currentTime;
        }
    }

    private double computeAverageWaitingTime() {
        int totalWaitingTime = 0;
        int count = 0;

        //calculeaza timpul mediu de asteptare pt taskurile care au fost servite
        for (Task task : allTasks) {
            if (task.getStartTime() != -1) {
                totalWaitingTime += task.getWaitingTime();
                count++;
            }
        }

        if (count == 0) {
            return 0;
        }

        return (double) totalWaitingTime / count;
    }

    @Override
    public void run() {
        //scrie evolutia simularii in fisierul log.txct
        try (PrintWriter writer = new PrintWriter(new FileWriter("log.txt"))) {
            int currentTime = 0;

            //initializeaza log-ul si animatia la momentul 0
            javax.swing.SwingUtilities.invokeLater(() -> {
                frame.setSimulationData("");
                frame.updateAnimation(new ArrayList<>(generatedTasks),
                        new ArrayList<>(scheduler.getServers()),
                        0);
            });

            //bucla principala a simularii
            while (currentTime <= timeLimit) {
                scheduler.updateServerTimes(currentTime);

                // muta in cozi task-urile care au ajuns la momentul curent
                Iterator<Task> iterator = generatedTasks.iterator();
                while (iterator.hasNext()) {
                    Task task = iterator.next();
                    if (task.getArrivalTime() <= currentTime) {
                        scheduler.dispatchTask(task);
                        iterator.remove();
                    }
                }

                updatePeakHour(currentTime);

                //formateaza starea curenta pentru afisare
                String state = formatState(currentTime);

                //snapshot-uri folosite pentru actualizarea sigura a animatiei in GUI
                int displayTime = currentTime;
                List<Task> shoppingSnapshot = new ArrayList<>(generatedTasks);
                List<Server> serverSnapshot = new ArrayList<>(scheduler.getServers());

                //actualizeaza interfata grafica pe thread-ul Swing
                javax.swing.SwingUtilities.invokeLater(() -> {
                    frame.updateAnimation(shoppingSnapshot, serverSnapshot, displayTime);
                    frame.appendSimulationData(state);
                });

                //afisez in consola
                System.out.print(state);

                //scrie starea in fisier
                writer.print(state);
                writer.flush();

                // daca nu mai sunt task-uri de generat si toate cozile sunt goale,
                // simularea se opreste
                if (generatedTasks.isEmpty() && scheduler.allQueuesEmpty()) {
                    break;
                }

                currentTime++;

                //simuleaza trecerea unei unitati de timp reale
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            //opreste toate serverele la final
            scheduler.stopAllServers();

            //calculeaza statistica finala
            double averageWaitingTime = computeAverageWaitingTime();

            String finalStats = "Simulation finished.\n"
                    + "Average waiting time: " + averageWaitingTime + "\n"
                    + "Average service time: " + averageServiceTime + "\n"
                    + "Peak hour: " + peakHour + "\n";

            int finalTime = currentTime;
            //afiseaza rezultatele finale in interfata
            javax.swing.SwingUtilities.invokeLater(() -> {
                frame.updateAnimation(new ArrayList<>(generatedTasks),
                        new ArrayList<>(scheduler.getServers()),
                        finalTime);
                frame.appendSimulationData("\n" + finalStats);
                frame.setStatistics(averageWaitingTime, averageServiceTime, peakHour);
            });
            //adfiseaza rezultatele finale si in consola
            System.out.println(finalStats);
            writer.println(finalStats);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}