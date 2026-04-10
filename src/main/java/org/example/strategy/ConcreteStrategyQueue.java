package org.example.strategy;

import org.example.businesslogic.Strategy;
import org.example.model.Server;
import org.example.model.Task;

import java.util.List;

public class ConcreteStrategyQueue implements Strategy {

    @Override
    public void addTask(List<Server> servers, Task task) {
        if(servers == null || servers.isEmpty()){
            return;
        }
        Server minServer = servers.get(0);

        for(Server server : servers){
            if(server.getQueueSize() < minServer.getQueueSize()){
                minServer = server;
            }
        }
        minServer.addTask(task);
    }
}
