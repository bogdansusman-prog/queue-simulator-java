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
        try (PrintWriter writer = new PrintWriter(new FileWriter("log.txt"))) {
            int currentTime = 0;

            javax.swing.SwingUtilities.invokeLater(() -> {
                frame.setSimulationData("");
                frame.updateAnimation(new ArrayList<>(generatedTasks),
                        new ArrayList<>(scheduler.getServers()),
                        0);
            });

            while (currentTime <= timeLimit) {
                scheduler.updateServerTimes(currentTime);

                Iterator<Task> iterator = generatedTasks.iterator();
                while (iterator.hasNext()) {
                    Task task = iterator.next();
                    if (task.getArrivalTime() <= currentTime) {
                        scheduler.dispatchTask(task);
                        iterator.remove();
                    }
                }

                updatePeakHour(currentTime);

                String state = formatState(currentTime);

                int displayTime = currentTime;
                List<Task> shoppingSnapshot = new ArrayList<>(generatedTasks);
                List<Server> serverSnapshot = new ArrayList<>(scheduler.getServers());

                javax.swing.SwingUtilities.invokeLater(() -> {
                    frame.updateAnimation(shoppingSnapshot, serverSnapshot, displayTime);
                    frame.appendSimulationData(state);
                });

                System.out.print(state);

                writer.print(state);
                writer.flush();

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

            double averageWaitingTime = computeAverageWaitingTime();

            String finalStats = "Simulation finished.\n"
                    + "Average waiting time: " + averageWaitingTime + "\n"
                    + "Average service time: " + averageServiceTime + "\n"
                    + "Peak hour: " + peakHour + "\n";

            int finalTime = currentTime;
            javax.swing.SwingUtilities.invokeLater(() -> {
                frame.updateAnimation(new ArrayList<>(generatedTasks),
                        new ArrayList<>(scheduler.getServers()),
                        finalTime);
                frame.appendSimulationData("\n" + finalStats);
                frame.setStatistics(averageWaitingTime, averageServiceTime, peakHour);
            });

            System.out.println(finalStats);
            writer.println(finalStats);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}