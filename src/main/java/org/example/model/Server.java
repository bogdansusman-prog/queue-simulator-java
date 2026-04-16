package org.example.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    //coada sigura + sincronizare automata
    private final BlockingQueue<Task> tasks;//existenta mai multor threaduri...
    //e thread-safe
    //permite simulationmanager-> adaga taskuri
    //permit server -> consume taskuri
    private final AtomicInteger waitingPeriod;
    //exista mai multe threaduri care citesc si modifica
    //operatia e indivizibila
    private volatile boolean running;
    private volatile int currentTime;
    //valoarea e mereu citita din memorie nu din thread
    //daca foloseam altceva nu vedeam niciodata o schimbare
    private Task currentTask;

    public Server(){
        // initialize queue and waiting period
        tasks = new LinkedBlockingQueue<>();
        waitingPeriod = new AtomicInteger(0);
        currentTime = 0;
        running = true;
        this.currentTask = null;
    }

    public void addTask(Task newTask){
        //add task to the queue
        tasks.add(newTask);
        //increment waitingPeriod
        waitingPeriod.addAndGet(newTask.getRemainingServiceTime());
    }

    public boolean isEmpty(){
        return tasks.isEmpty() && currentTask == null;
    }

    public Task[] getTasks() {
        int extra = (currentTask != null) ? 1 : 0;
        //verificam task in exectie
        Task[] queueTasks = tasks.toArray(new Task[0]);
        //iau taskurile din cooada (tranform in array din blocking queue)
        Task[] result = new Task[queueTasks.length + extra];
        //creez arrayul final


        //pun currenttask primul(e in lucru)
        int index = 0;
        if (currentTask != null) {

            result[index++] = currentTask;
        }
//adaug restul taskurilor
        for (Task task : queueTasks) {
            result[index++] = task;
        }

        return result;
    }

    public BlockingQueue<Task> getQueue(){
        return tasks;
    }

    public void stopServer(){
        running = false;
    }

    public int getWaitingPeriod(){
        return waitingPeriod.get();
    }

    public int getQueueSize(){
        return tasks.size();
    }
    public int getCurrentTime(){
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public void run() {
        while (running || !tasks.isEmpty() || currentTask != null) {
            try {
                if (currentTask == null) {
                    currentTask = tasks.poll(1, TimeUnit.SECONDS);
                    //sincronizez accesul automat

                    if (currentTask != null) {
                        currentTask.setStartTime(currentTime);
                    }
                }

                if (currentTask != null) {
                    Thread.sleep(1000);
                    currentTask.decreaseRemainingServiceTime();

                    if (currentTask.isFinished()) {
                        waitingPeriod.addAndGet(-currentTask.getServiceTime());//il scot din timpul total de asteptare
                        currentTask = null;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (currentTask != null) {
            sb.append(currentTask).append(" ");
        }

        for (Task task : tasks) {
            sb.append(task).append(" ");
        }

        if (sb.length() == 0) {
            return "closed";
        }

        return sb.toString().trim();
    }
}
