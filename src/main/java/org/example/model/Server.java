package org.example.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;

    public Server(){
        // initialize queue and waiting period
        tasks = new LinkedBlockingQueue<>();
        waitingPeriod = new AtomicInteger(0);
    }

    public void addTask(Task newTask){
        //add task to the queue
        tasks.add(newTask);
        //increment waitingPeriod
        waitingPeriod.addAndGet(newTask.getServiceTime());
    }

    @Override
    public void run() {
        while (true) {
            try {
                //take next task from queue
                Task currentTask = tasks.peek();

                if (currentTask != null) {
                    //stop thread for a time equal with the tasks prcessing time
                    Thread.sleep(1000);

                    currentTask.setServiceTime(currentTask.getServiceTime() - 1);

                    //decrement the watingPeriod
                    waitingPeriod.decrementAndGet();

                    if (currentTask.getServiceTime() == 0) {
                        tasks.take();
                    }
                }
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public Task[] getTasks(){
        return tasks.toArray(new Task[0]);
    }

    public BlockingQueue<Task> getQueue(){
        return tasks;
    }

    public AtomicInteger getWaitingPeriod(){
        return waitingPeriod;
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
        return sb.toString();
    }


}
