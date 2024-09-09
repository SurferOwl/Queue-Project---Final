package BusinessLogic;

import Model.Clients;
import Model.Server;

import java.util.List;

public class ConcreteStrategyTime implements Strategy {
    @Override
    public void addClients(List<Server> queues, Clients c) {
        Server minQ = queues.getFirst();

        for (Server i : queues) {
                if (minQ.getWaitingTime().get() > i.getWaitingTime().get()) {
                    minQ = i;
                }
            }

        minQ.addTask(c);
    }
}
