package org.example.strategy;

import org.example.model.Task;
import org.example.model.Server;
import org.example.businesslogic.Strategy;

import java.util.List;

public class ConcreteStrategyTime implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task t){
        // TODO Auto-generated method stub
        if(servers == null || servers.isEmpty()){
            return;
        }
        Server minServer = servers.get(0);

        for(Server server : servers){
            if(server.getWaitingPeriod().get() < minServer.getWaitingPeriod().get()){
                minServer = server;
            }
        }
        minServer.addTask(t);
    }
}
