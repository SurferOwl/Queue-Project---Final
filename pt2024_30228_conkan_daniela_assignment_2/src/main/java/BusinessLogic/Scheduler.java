package BusinessLogic;

import Model.Server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
public class Scheduler {
    private final List<Server> queues = new CopyOnWriteArrayList<>();
    private final int maxNoQueues;
    private final int maxClients;
    private Strategy strategy;

    public Scheduler(int maxSer,int maxCli){
        maxNoQueues = maxSer;
        maxClients = maxCli;

        for (int i=0;i<maxNoQueues;i++){
            Server s = new Server();
            queues.add(s);
            s.start();
        }
    }

    public void changeStrategy(SelectionPolicy policy) {
        if (policy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ConcreteStrategyQueue();
        }

        if (policy == SelectionPolicy.SHORTEST_TIME) {
            strategy = new ConcreteStrategyTime();
        }
    }

    public List<Server> getQueues(){
        return queues;
    }

    public Strategy getStrategy() {
        return strategy;
    }
}
