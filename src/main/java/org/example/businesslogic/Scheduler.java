package org.example.businesslogic;

import org.example.model.Server;
import org.example.model.Task;
import org.example.strategy.ConcreteStrategyQueue;
import org.example.strategy.ConcreteStrategyTime;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int maxTasksPerServer){
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        this.servers = new ArrayList<>();

        // for maxNoServers
        for(int i = 0; i < maxNoServers; i++){
            //-create server object
            Server server = new Server();
            servers.add(server);

            // - create thread with the object
            Thread t = new Thread(server);
            t.start();
        }
    }

    public void changeStrategy(SelectionPolicy policy){
        // apply stratey pattern in order to instantiate the strategy
        //with the concrete strategy corresponding to policy
        if(policy == SelectionPolicy.SHORTEST_QUEUE){
            strategy = new ConcreteStrategyQueue();
        }
        if(policy == SelectionPolicy.SHORTEST_TIME){
            strategy = new ConcreteStrategyTime();
        }
    }

    public void dispatchTask(Task task){
        //call the strategy addTask method
        strategy.addTask(servers, task);
    }

    public List<Server> getServers(){
        return servers;
    }
}
