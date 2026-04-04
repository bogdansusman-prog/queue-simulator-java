package org.example.businesslogic;

import org.example.gui.SimulationFrame;
import org.example.model.Task;
import org.example.model.Server;

import java.util.*;


public class SimulationManager implements Runnable{
    //followind data read from UI
    //maximum processing time, it is read from UI
    public int timeLimit = 60;
    public int maxProcessingTime = 4;
    public int minProcessingTime = 2;
    public int numberOfServers = 2;
    public int numberOfClients = 4;
    public int minArrivalTime = 2;
    public int maxArrivalTime = 30;
    public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;

    //entity responsible with queue management and clien distribution
    private Scheduler scheduler;
    //frame for displaying simulation
    private SimulationFrame frame;
    //pool of tasks (client shopping in the store)
    private List<Task> generatedTasks;
    private List<Server> generatedServers;

    public SimulationManager(){
        //initialize the scheduler
        // => create and start numberOfServers threads
        scheduler = new Scheduler(numberOfServers, numberOfClients);

        // => initialize selection strategy => createStrategy
        scheduler.changeStrategy(selectionPolicy);

        //initialize frame to display simulation
        frame = new SimulationFrame();

        //generate numberOfClients clients using generateNRandomTasks()
        //store the, to generatedTasks
        generatedTasks = new ArrayList<>();
        generateNRandomTasks();
    }
    private void generateNRandomTasks(){
        //generate N random tasks;
        Random random = new Random();

        for(int i=1;i<=numberOfServers;i++){
            // - random provcessing
            // -minProcessingTime < processingTime < maxProcessingTme
            int serviceTime = random.nextInt(maxProcessingTime - minProcessingTime + 1)+minProcessingTime;
            // - random arrival time
            int arrivalTime = random.nextInt(maxArrivalTime - minArrivalTime + 1)+minArrivalTime;

            Task task = new Task(i, arrivalTime, serviceTime);
            generatedTasks.add(task);
        }
        generatedTasks.sort(Comparator.comparingInt(Task::getArrivalTime));
    }

    private String formatState(int currentTime){
        StringBuilder result = new StringBuilder();

        result.append("Time ").append(currentTime).append("\n");
        result.append("Waiting clients: ");

        if (generatedTasks.isEmpty()){
            result.append("none");
        }else{
            for(Task task : generatedTasks){
                result.append(task).append(" ");
            }
        }
        result.append("\n");
        int index= 1;
        for(Server server : scheduler.getServers()){
            result.append("Queue ").append(index).append(": " ).append(server)
                    .append("\n");
            index++;
        }
        result.append("\n");
        return result.toString();
    }
    @Override
    public void run(){
        int currentTime = 0;
        while(currentTime < timeLimit){
            //iterate generatedTasks list and pick tasks that have the arrivalTime = currentTime
            Iterator<Task> iterator = generatedTasks.iterator();
            while(iterator.hasNext()){
                Task task = iterator.next();
                if(task.getArrivalTime() == currentTime){
                    //-send task to queue by calling dispatchTask method from Scheduler
                    scheduler.dispatchTask(task);
                    // - delete client from list
                    iterator.remove();
                }
            }
            //update UI frame
            frame.setSimulationData(formatState(currentTime));
            System.out.print(formatState(currentTime));

            currentTime++;

            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static void main(String[] args){
        SimulationManager gen = new SimulationManager();
        Thread t = new Thread(gen);
        t.start();
    }
}
