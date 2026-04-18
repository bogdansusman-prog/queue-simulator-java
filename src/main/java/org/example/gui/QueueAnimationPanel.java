package org.example.gui;

import org.example.model.Server;
import org.example.model.Task;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class QueueAnimationPanel extends JPanel {
    private final Map<Integer, AnimatedClient> animatedClients = new HashMap<>();
    private List<Task> shoppingClients = new ArrayList<>();
    private List<Server> servers = new ArrayList<>();
    private int currentTime = 0;

    public QueueAnimationPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(850, 360));

        javax.swing.Timer timer = new javax.swing.Timer(40, e -> {
            updateAnimation();
            repaint();
        });
        timer.start();
    }

    public void updateState(List<Task> shoppingClients, List<Server> servers, int currentTime) {
        this.shoppingClients = new ArrayList<>(shoppingClients);
        this.servers = new ArrayList<>(servers);
        this.currentTime = currentTime;

        Set<Integer> visibleIds = new HashSet<>();

        // zona de sus: clienti care inca nu au ajuns la case
        int shoppingStartX = 70;
        int shoppingY = 85;
        int shoppingSpacing = 65;

        for (int i = 0; i < shoppingClients.size(); i++) {
            Task t = shoppingClients.get(i);
            visibleIds.add(t.getId());

            AnimatedClient client = animatedClients.computeIfAbsent(
                    t.getId(),
                    id -> new AnimatedClient(id, 20, shoppingY, t.getRemainingServiceTime())
            );

            client.setRemainingTime(t.getRemainingServiceTime());
            client.setTarget(shoppingStartX + i * shoppingSpacing, shoppingY);
            client.setExiting(false);
        }

        // cozile
        int servingX = 140;
        int queueStartX = 220;
        int queueTopY = 180;
        int laneGap = 85;
        int queueSpacing = 65;

        for (int i = 0; i < servers.size(); i++) {
            Task[] queueTasks = servers.get(i).getTasks();
            int y = queueTopY + i * laneGap;

            for (int j = 0; j < queueTasks.length; j++) {
                Task t = queueTasks[j];
                visibleIds.add(t.getId());

                AnimatedClient client = animatedClients.computeIfAbsent(
                        t.getId(),
                        id -> new AnimatedClient(id, 20, 20, t.getRemainingServiceTime())
                );

                client.setRemainingTime(t.getRemainingServiceTime());
                int x = (j == 0) ? servingX : queueStartX + (j - 1) * queueSpacing;

                client.setTarget(x, y);
                client.setExiting(false);
            }
        }

        // clientii care nu mai sunt nici sus, nici in cozi -> ies din ecran
        for (AnimatedClient client : animatedClients.values()) {
            if (!visibleIds.contains(client.getTaskId())) {
                client.startExiting();
                client.setRemainingTime(0);
            }
        }
    }

    private void updateAnimation() {
        Iterator<Map.Entry<Integer, AnimatedClient>> iterator = animatedClients.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, AnimatedClient> entry = iterator.next();
            AnimatedClient client = entry.getValue();
            client.update();

            if (client.isExiting() && client.isOutsideScreen(getWidth())) {
                iterator.remove();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setFont(new Font("Times New Roman", Font.BOLD, 14));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawHeader(g2);
        drawShoppingZone(g2);
        drawQueues(g2);
        drawClients(g2);
    }

    private void drawHeader(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.drawString("Time: " + currentTime, 20, 25);
    }

    private void drawShoppingZone(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.drawString("Clients shopping / waiting to arrive:", 20, 50);

        g2.setColor(new Color(235, 235, 235));
        g2.fillRoundRect(20, 60, getWidth() - 40, 80, 15, 15);

        g2.setColor(Color.GRAY);
        g2.drawRoundRect(20, 60, getWidth() - 40, 80, 15, 15);

        if (shoppingClients.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.drawString("none", 35, 105);
        }
    }

    private void drawQueues(Graphics2D g2) {
        int cashierX = 45;
        int cashierYStart = 175;
        int laneGap = 85;

        for (int i = 0; i < servers.size(); i++) {
            int y = cashierYStart + i * laneGap;

            g2.setColor(new Color(70, 70, 70));
            g2.fillRoundRect(cashierX, y, 70, 40, 12, 12);

            g2.setColor(Color.WHITE);
            g2.drawString("Q" + (i + 1), cashierX + 24, y + 24);

            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(135, y + 20, getWidth() - 30, y + 20);

            if (noClientsLeftAnywhere()) {
                g2.setColor(Color.GRAY);
                g2.drawString("closed", 170, y + 24);
            }
        }
    }

    private void drawClients(Graphics2D g2) {
        Set<Integer> shoppingIds = new HashSet<>();
        for(Task task : shoppingClients){
            shoppingIds.add(task.getId());
        }

        Shape oldClip = g2.getClip();

        //zona shopping
        g2.setClip(20, 60, getWidth() - 40, 80);

        for (AnimatedClient client : animatedClients.values()) {
            //desenez doar daca clientul e in zona de sus
            if(shoppingIds.contains(client.getTaskId())){
                drawClient(g2, client);
            }
        }

        g2.setClip(oldClip);
        //restaurare clip
        for(AnimatedClient client : animatedClients.values()) {
            if(!shoppingIds.contains(client.getTaskId())) {
                drawClient(g2, client);
            }
        }
    }

    private void drawClient(Graphics2D g2, AnimatedClient client) {
        int x = client.getX();
        int y = client.getY();
        int size = 34;

        if (client.isExiting()) {
            g2.setColor(new Color(180, 180, 180));
        } else {
            g2.setColor(new Color(100, 180, 255));
        }

        g2.fillOval(x, y, size, size);

        g2.setColor(Color.BLACK);
        g2.drawOval(x, y, size, size);

        g2.drawString("C" + client.getTaskId(), x + 5, y + 20);
        g2.drawString(String.valueOf(client.getRemainingTime()), x + 10, y - 5);
        if (client.isExiting()) {
            g2.setColor(Color.GRAY);
            g2.drawString("leaving", x - 2, y + 50);
        }
    }

    private boolean noClientsLeftAnywhere() {
        if (!shoppingClients.isEmpty()) {
            return false;
        }

        for (Server server : servers) {
            if (server.getTotalClientsCount() > 0) {
                return false;
            }
        }

        return true;
    }


}