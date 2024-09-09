package BusinessLogic;

import Model.Clients;
import Model.Server;

import java.util.List;

public interface Strategy {
        void addClients(List<Server> queues, Clients c);
}
