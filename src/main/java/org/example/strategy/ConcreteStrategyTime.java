package org.example.strategy;

import org.example.model.Task;
import org.example.model.Server;
import org.example.businesslogic.Strategy;

import java.util.List;

public class ConcreteStrategyTime implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task t){
        if(servers == null || servers.isEmpty()){
            return;
        }
        Server minServer = servers.get(0);

        for(Server server : servers){
            if(server.getWaitingPeriod() < minServer.getWaitingPeriod()){
                minServer = server;
            }
        }
        minServer.addTask(t);
    }
}
