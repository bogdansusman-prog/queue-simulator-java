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

    private final Scheduler scheduler;
    private final SimulationFrame frame;
    private final List<Task> generatedTasks;
    private final List<Task> allTasks;

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

        scheduler = new Scheduler(numberOfServers);
        scheduler.changeStrategy(selectionPolicy);

        generatedTasks = new ArrayList<>();
        allTasks = new ArrayList<>();
        generateNRandomTasks();
    }

    private void generateNRandomTasks() {
        Random random = new Random();
        int totalServiceTime = 0;

        for (int i = 1; i <= numberOfClients; i++) {
            int serviceTime = random.nextInt(maxProcessingTime - minProcessingTime + 1) + minProcessingTime;
            int arrivalTime = random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;

            Task task = new Task(i, arrivalTime, serviceTime);
            generatedTasks.add(task);
            allTasks.add(task);

            totalServiceTime += serviceTime;
        }

        generatedTasks.sort(Comparator.comparingInt(Task::getArrivalTime));
        averageServiceTime = (double) totalServiceTime / numberOfClients;
    }

    private String formatState(int currentTime) {
        StringBuilder result = new StringBuilder();
//folosesc append pentru ca sa nu tot creez un obiect nou si sa eficientizez lucrurile(adauga la fina = append)
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
        int totalClients = scheduler.getTotalClientsInQueues();
        if (totalClients > peakClients) {
            peakClients = totalClients;
            peakHour = currentTime;
        }
    }

    private double computeAverageWaitingTime() {
        int totalWaitingTime = 0;
        int count = 0;

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
        //deschid fisierul log.txt pentru scrierea terminalului
        try (PrintWriter writer = new PrintWriter(new FileWriter("log.txt"))) {
            int currentTime = 0;
            //curat zona de afisare din interfata gui
            javax.swing.SwingUtilities.invokeLater(() -> frame.setSimulationData(""));

            while (currentTime <= timeLimit) {
                //actualizez timpul ramas pentru procese
                scheduler.updateServerTimes(currentTime);

                //folosesc iterator ca sa pot sterge din lista fara sa puste
                //sincronizez cu iteratorul intern
                Iterator<Task> iterator = generatedTasks.iterator();
                while (iterator.hasNext()) {
                    Task task = iterator.next();
                    if (task.getArrivalTime() <= currentTime) {
                        scheduler.dispatchTask(task);
                        iterator.remove();
                        //daca foloseaza list.remove in bucla pusca
                    }
                }

                updatePeakHour(currentTime);

                //formatez starea curenta
                String state = formatState(currentTime);

                //afisez in gui
                javax.swing.SwingUtilities.invokeLater(() -> frame.appendSimulationData(state));
                System.out.print(state);

                //scriu in fisier
                writer.print(state);
                writer.flush();

                //daca nu mai sunt taskurti ceau
                if (generatedTasks.isEmpty() && scheduler.allQueuesEmpty()) {
                    break;
                }

                currentTime++;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            scheduler.stopAllServers();


            //calculez  timpul mediu
            double averageWaitingTime = computeAverageWaitingTime();

            String finalStats = "Simulation finished.\n"
                    + "Average waiting time: " + averageWaitingTime + "\n"
                    + "Average service time: " + averageServiceTime + "\n"
                    + "Peak hour: " + peakHour + "\n";


            //afisez in gui

            javax.swing.SwingUtilities.invokeLater(() -> frame.appendSimulationData("\n" + finalStats));
            //afisez in consola in acelasi timp
            System.out.println(finalStats);
            //scriu rezultatele finale in fisier
            writer.println(finalStats);

        } catch (IOException e) {
            //tratare eroare la lucrul cu fisier
            e.printStackTrace();
        }
    }

}