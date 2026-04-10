package org.example.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private final BlockingQueue<Task> tasks;
    private final AtomicInteger waitingPeriod;
    private volatile boolean running;
    private volatile int currentTime;

    public Server(){
        // initialize queue and waiting period
        tasks = new LinkedBlockingQueue<>();
        waitingPeriod = new AtomicInteger(0);
        currentTime = 0;
        running = true;
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
        while (running || !tasks.isEmpty()) {
            try {
                //take next task from queue
                Task currentTask = tasks.peek();
                if (currentTask != null) {
                    if (currentTask.getStartTime() == -1) {
                        currentTask.setStartTime(currentTime);
                    }
                    //stop thread for a time equal with the tasks prcessing time
                    Thread.sleep(1000);

                    currentTask.decreaseRemainingServiceTime();
                    //decrement the watingPeriod
                    waitingPeriod.decrementAndGet();

                    if (currentTask.isFinished()) {
                        tasks.take();
                    }
                } else{
                    Thread.sleep(100);
                }
            }catch (InterruptedException e){
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
