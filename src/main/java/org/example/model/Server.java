package org.example.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private final BlockingQueue<Task> tasks;
    private final AtomicInteger waitingPeriod;
    private volatile boolean running;
    private volatile int currentTime;
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
        return tasks.isEmpty();
    }

    public Task[] getTasks(){
        return tasks.toArray(new Task[0]);
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

                    if (currentTask != null) {
                        currentTask.setStartTime(currentTime);
                    }
                }

                if (currentTask != null) {
                    Thread.sleep(1000);
                    currentTask.decreaseRemainingServiceTime();

                    if (currentTask.isFinished()) {
                        waitingPeriod.addAndGet(-currentTask.getServiceTime());
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
    public String toString(){
        if(tasks.isEmpty()){
            return "closed";
        }
        StringBuilder sb = new StringBuilder();
        for(Task task : tasks){
            sb.append(task).append(" ");
        }
        return sb.toString().trim();
    }
}
