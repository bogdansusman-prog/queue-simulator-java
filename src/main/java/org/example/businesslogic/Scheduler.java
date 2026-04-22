package org.example.businesslogic;

import org.example.model.Server;
import org.example.model.Task;
import org.example.strategy.ConcreteStrategyQueue;
import org.example.strategy.ConcreteStrategyTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scheduler {
    // List of servers (queues) managed by the scheduler
    private final List<Server> servers;
    //Maximum numbers of servers allowed
    private final int maxNoServers;
    //Strayegy user for task dispatching
    private Strategy strategy;
    //pool de thread-uri in care ruleaza serverele in paralel
    private final ExecutorService serverPool;

    public Scheduler(int maxNoServers){
        this.maxNoServers = maxNoServers;
        this.servers = new ArrayList<>();
        this.serverPool = Executors.newFixedThreadPool(maxNoServers);

        // creeaza serverele si le porneste in executor
        for(int i = 0; i < maxNoServers; i++){
            //-create server object
            Server server = new Server();
            servers.add(server);
            serverPool.execute(server);

            // - create thread with the object
//            Thread t = new Thread(server);
//            t.start();
            // thread pool =>
        }
    }

    public void changeStrategy(SelectionPolicy policy){
        // apply strategy pattern in order to instantiate the strategy
        //with the concrete strategy corresponding to policy
        if(policy == SelectionPolicy.SHORTEST_QUEUE){
            strategy = new ConcreteStrategyQueue();
        }
        else if(policy == SelectionPolicy.SHORTEST_TIME){
            strategy = new ConcreteStrategyTime();
        }
    }

    public void dispatchTask(Task task) {
        //call the strategy addTask method
        if (strategy != null) {
            strategy.addTask(servers, task);
        }
    }

    public List<Server> getServers(){
        return servers;
    }

    public boolean allQueuesEmpty(){
        for(Server server : servers){
            if(!server.isEmpty()){
                return false;
            }
        }
        return true;
    }

    public int getTotalClientsInQueues(){
        int total = 0;
        //include si clientul aflat in servire
        for(Server server : servers){
            total += server.getTotalClientsCount();
        }
        return total;
    }

    public void stopAllServers(){
        for(Server server : servers){
            server.stopServer();
        }
        serverPool.shutdown();
    }

    public void updateServerTimes(int currentTime) {
        // transmite timpul curent catre fiecare server
        for(Server server : servers){
            server.setCurrentTime(currentTime);
        }
    }
}
