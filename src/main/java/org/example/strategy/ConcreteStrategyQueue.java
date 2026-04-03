package org.example.strategy;

import org.example.businesslogic.Strategy;
import org.example.model.Server;
import org.example.model.Task;

import java.util.ArrayList;
import java.util.List;

public class ConcreteStrategyQueue implements Strategy {

    @Override
    public void addTask(List<Server> servers, Task t) {
        if(servers == null || servers.isEmpty()){
            return;
        }
        Server minServer = servers.get(0);

        for(Server server : servers){
            if(server.getQueue().size() < minServer.getQueue().size()){
                minServer = server;
            }
        }
        minServer.addTask(t);
    }
}
