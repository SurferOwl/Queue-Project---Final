package Model;

import java.util.concurrent.atomic.AtomicInteger;

public class Clients implements Comparable<Clients>{

    private final Integer ID;
    private final Integer arrivalTime;
    private final AtomicInteger serviceTime = new AtomicInteger(0);

    public synchronized Integer getArrivalTime() {
        return arrivalTime;
    }

    public synchronized AtomicInteger getServiceTime() {
        return serviceTime;
    }

    public Clients(Integer id, Integer aT, Integer sT){
        ID = id;
        arrivalTime = aT;
        serviceTime.set(sT);
    }


    public synchronized int compareTo(Clients otherClient) {
        return this.arrivalTime.compareTo(otherClient.arrivalTime);
    }

    @Override
    public synchronized String toString() {
        return "Clients{" +
                "ID=" + ID +
                ", arrivalTime=" + arrivalTime +
                ", serviceTime=" + serviceTime +
                '}';
    }
}


