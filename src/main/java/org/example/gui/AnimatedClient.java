package org.example.gui;

public class AnimatedClient {
    private final int taskId;
    private int x;
    private int y;
    private int targetX;
    private int targetY;
    private int remainingTime;
    private boolean exiting;

    public AnimatedClient(int taskId, int x, int y, int targetX, int targetY, int remainingTime, boolean exiting) {
        this.taskId = taskId;
        this.x = x;
        this.y = y;
        this.targetX = targetX;
        this.targetY = targetY;
        this.remainingTime = remainingTime;
        this.exiting = exiting;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void setTarget(int targetX, int targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public void setExiting(boolean exiting) {
        this.exiting = exiting;
    }

    public boolean isExiting() {
        return exiting;
    }

    public boolean isOutsideScreen(int width){
        return x > width + 80;
    }

    public void update(){
        int speed = 8;

        if(x < targetX) x = Math.min(x + speed, targetX);
        else if(x > targetX) x = Math.max(x - speed, targetX);

        if(y < targetY) y = Math.min(y + speed, targetY);
        else if(y > targetY) y = Math.max(y - speed, targetY);

        if(exiting){
            x+=speed;
        }
    }
}
