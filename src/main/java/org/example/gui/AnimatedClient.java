package org.example.gui;

public class AnimatedClient {
    //id ul taskului clientului animat
    private final int taskId;
    //coordonatele curente ale clientului
    private int x;
    private int y;
    //pozitia spre care se indreapta clientul
    private int targetX;
    private int targetY;
    private int remainingTime;
    //indica daca clientul pleaca
    private boolean exiting;
    // coordonata finala pe axa Y pentru efectul de iesire
    private int exitY;
    // previne aplicarea de mai multe ori a offset-ului de iesire
    private boolean exitYOffsetApplied;

    public AnimatedClient(int taskId, int x, int y, int remainingTime) {
        this.taskId = taskId;
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.remainingTime = remainingTime;
        this.exiting = false;
        this.exitY = y;
        this.exitYOffsetApplied = false;
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
        // actualizeaza noua pozitie tinta a clientului
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public void setExiting(boolean exiting) {
        this.exiting = exiting;
    }

    public boolean isExiting() {
        return exiting;
    }

    // verifica daca clientul a iesit complet din zona vizibila
    public boolean isOutsideScreen(int width){
        return x > width + 80;
    }

    public void update() {
        int speed = 8;

        if (!exiting) {
            // deplasare normala catre pozitia tinta
            if (x < targetX) x = Math.min(x + speed, targetX);
            else if (x > targetX) x = Math.max(x - speed, targetX);

            if (y < targetY) y = Math.min(y + speed, targetY);
            else if (y > targetY) y = Math.max(y - speed, targetY);
        } else {
            // mai intai coboara putin
            if (y < exitY) {
                y = Math.min(y + 2, exitY);
            } else if (y > exitY) {
                y = Math.max(y - 2, exitY);
            }

            // apoi merge in dreapta
            x += 6+speed;
        }
    }

    public void startExiting(){
        // marcheaza clientul ca iesit si
        // aplica o singura data offset-ul pe verticala
        this.exiting = true;

         if(!exitYOffsetApplied){
            this.exitY = this.y + 12;
            exitYOffsetApplied = true;
        }
    }
}
