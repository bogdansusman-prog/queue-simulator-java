package org.example.model;

public class Task {
    private final int id;
    private final int arrivalTime;
    private final int serviceTime;
    private int remainingServiceTime;
    private int waitingTime;
    private int startTime = -1;

    public Task(int id, int arrivalTime, int serviceTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.remainingServiceTime = serviceTime;
        this.waitingTime = 0;
    }

    public int getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }


    public int getServiceTime() {
        return serviceTime;
    }

    public int getRemainingServiceTime() {
        return remainingServiceTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime){
        if(this.startTime == -1){
            this.startTime = startTime;
            this.waitingTime = startTime - arrivalTime;
        }
    }

    public void decreaseRemainingServiceTime(){
        if(this.remainingServiceTime > 0){
            remainingServiceTime--;
        }
    }

    public boolean isFinished(){
        return remainingServiceTime == 0;
    }

    @Override
    public String toString() {
        return "(" + id + ", " + arrivalTime + ", " + remainingServiceTime + ")";
    }
}
