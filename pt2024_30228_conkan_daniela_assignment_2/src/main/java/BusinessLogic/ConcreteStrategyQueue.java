package BusinessLogic;

import Model.Clients;
import Model.Server;

import java.util.List;

public class ConcreteStrategyQueue implements Strategy {
    @Override
    public void addClients(List<Server> queues, Clients c) {
        Server minQ = queues.getFirst();

            for (Server i : queues) {
                    if (minQ.getQueue().size() > i.getQueue().size()) {
                        minQ = i;
                    }
                }
            minQ.addTask(c);
    }
}
