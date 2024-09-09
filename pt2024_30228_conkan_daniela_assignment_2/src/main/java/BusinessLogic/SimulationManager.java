    package BusinessLogic;

    import Model.Clients;

    import javax.swing.*;
    import java.awt.*;
    import java.io.BufferedWriter;
    import java.io.FileWriter;
    import java.io.IOException;
    import java.util.Collections;
    import java.util.Iterator;
    import java.util.List;
    import java.util.Random;
    import java.util.concurrent.CopyOnWriteArrayList;

    public class SimulationManager implements Runnable {

        private final int timeLimit;
        private final int maxProcessingTime;
        private final int minProcessingTime;
        private final int maxArrivalTime;
        private final int minArrivalTime;
        private final int noQueues;
        private final int noClients;

        private static int currentTime = 0;
        private SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;

        private final Scheduler scheduler;

        private final List<Clients> generatedClients;

        public SimulationManager(int timeLimit, int maxProcessingTime, int minProcessingTime, int maxArrivalTime, int minArrivalTime, int noQueues, int noClients, SelectionPolicy s) {
            this.timeLimit = timeLimit;
            this.maxProcessingTime = maxProcessingTime;
            this.minProcessingTime = minProcessingTime;
            this.maxArrivalTime = maxArrivalTime;
            this.minArrivalTime = minArrivalTime;
            this.noQueues = noQueues;
            this.noClients = noClients;
            selectionPolicy = s;
            scheduler = new Scheduler(noQueues, noClients);
            scheduler.changeStrategy(selectionPolicy);

            generatedClients = new CopyOnWriteArrayList<>();
        }

        public void generateNRandomTasks() {
            Random random = new Random();

            for (int i = 0; i < noClients; i++) {
                Clients c = new Clients(i, random.nextInt(maxArrivalTime - minArrivalTime + 2) + minArrivalTime, random.nextInt(maxProcessingTime - minProcessingTime + 2) + minProcessingTime);
                generatedClients.add(c);
            }

            Collections.sort(generatedClients);
        }

        public void run() {
            int peakHourClients = 0, peakHour = 0;

            JFrame simulationWindow = new JFrame("Simulation Window");
            simulationWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            simulationWindow.setLayout(new BorderLayout());

            JPanel simulationPanel = new JPanel(new GridLayout(noQueues + 3, 1));
            Color pinkColor = new Color(255, 192, 203);
            simulationPanel.setBackground(pinkColor);
            simulationWindow.add(simulationPanel, BorderLayout.CENTER);

            JPanel currentTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            currentTimePanel.setBackground(pinkColor);
            JLabel currentTimeLabel = new JLabel("Current Time: ");
            JTextField currentTimeField = new JTextField(10);
            currentTimeField.setEditable(false);
            currentTimePanel.add(currentTimeLabel);
            currentTimePanel.add(currentTimeField);
            simulationPanel.add(currentTimePanel);

            JPanel resultsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            resultsPanel.setBackground(pinkColor);

            JLabel averageWaitingTimeLabel = new JLabel("Avg Waiting Time: ");
            JTextField averageWaitingTimeField = new JTextField(5);
            averageWaitingTimeField.setEditable(false);

            JLabel averageServiceTimeLabel = new JLabel("Avg Service Time: ");
            JTextField averageServiceTimeField = new JTextField(5);
            averageServiceTimeField.setEditable(false);

            JLabel peakHourLabel = new JLabel("Peak Hour: ");
            JTextField peakHourField = new JTextField(5);
            peakHourField.setEditable(false);

            resultsPanel.add(averageWaitingTimeLabel);
            resultsPanel.add(averageWaitingTimeField);
            resultsPanel.add(averageServiceTimeLabel);
            resultsPanel.add(averageServiceTimeField);
            resultsPanel.add(peakHourLabel);
            resultsPanel.add(peakHourField);

            simulationPanel.add(resultsPanel);

            JPanel[] queuePanels = new JPanel[noQueues];
            JTextField[] queueTextField = new JTextField[noQueues];
            for (int i = 0; i < noQueues; i++) {
                queuePanels[i] = new JPanel(new GridLayout(0, 1));
                queuePanels[i].setBackground(pinkColor);
                queuePanels[i].setBorder(BorderFactory.createTitledBorder("Queue" + (i + 1)));
                simulationPanel.add(queuePanels[i]);

                queueTextField[i] = new JTextField();
                queueTextField[i].setEditable(false);
                queuePanels[i].add(queueTextField[i]);
            }

            // Display generated clients
            JTextArea clientsTextArea = new JTextArea();
            clientsTextArea.setEditable(false);
            clientsTextArea.setBackground(pinkColor);
            clientsTextArea.setForeground(Color.BLACK);
            JScrollPane clientsScrollPane = new JScrollPane(clientsTextArea);
            clientsScrollPane.setBorder(BorderFactory.createTitledBorder("Generated Clients"));
            simulationPanel.add(clientsScrollPane);

            for (Clients client : this.generatedClients) {
                clientsTextArea.append(client.toString() + "\n");
            }

            simulationWindow.setSize(600, 400);
            simulationWindow.setLocationRelativeTo(null);
            simulationWindow.setVisible(true);

            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter("src/main/Logs.txt",false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            try {
                bufferedWriter.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while (currentTime < timeLimit) {
                int avgWaitingTime = 0, avgServiceTime = 0, maxClientsPeak = 0;
                currentTimeField.setText(String.valueOf(currentTime));
                    Iterator<Clients> iterator = generatedClients.iterator();
                    while (iterator.hasNext()) {
                        Clients client = iterator.next();
                        if (client.getArrivalTime() == currentTime) {
                            try {
                                bufferedWriter.write("Client arrived: " + client.toString()+"\n");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("Client arrived: " + client.toString());
                            scheduler.getStrategy().addClients(scheduler.getQueues(), client);
                            generatedClients.remove(client);
                        }
                    }

                try {
                    bufferedWriter.write("Current time: " + currentTime + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Current time: " + currentTime);
                for (int i = 0; i < noQueues; i++) {
                    avgWaitingTime += scheduler.getQueues().get(i).getWaitingTime().get();
                    avgServiceTime += scheduler.getQueues().get(i).getTotalServiceTime().get();
                    StringBuilder s = new StringBuilder();
                    System.out.println("Queue" + i + ": ");
                    try {
                        bufferedWriter.write("Queue" + i + ": \n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    for (Clients client : scheduler.getQueues().get(i).getQueue()) {
                        maxClientsPeak++;
                        s.append(client.toString());
                        s.append(" ");
                        queueTextField[i].setText(s.toString());
                        System.out.println(client.toString());
                        try {
                            bufferedWriter.write(client.toString()+"\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                if (maxClientsPeak > peakHourClients) {
                    peakHourClients = maxClientsPeak;
                    peakHour = currentTime;
                }

                avgServiceTime /= noClients;
                avgWaitingTime /= noQueues;

                peakHourField.setText(String.valueOf(peakHour));
                averageServiceTimeField.setText(String.valueOf(avgServiceTime));
                averageWaitingTimeField.setText(String.valueOf(avgWaitingTime));

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                for (int i = 0; i < noQueues; i++)
                    queueTextField[i].setText("");

                currentTime++;

                if(generatedClients.isEmpty()){
                    int noQueuesClosed = 0;
                    for(int i = 0; i< noQueues ; i++)
                        if(scheduler.getQueues().get(i).getQueue().isEmpty())
                            noQueuesClosed++;

                    if(noQueuesClosed == noQueues)
                        break;}
            }

            try {
                bufferedWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for(int i = 0;i < noQueues; i++)
                scheduler.getQueues().get(i).terminate();
            System.exit(0);
        }

        public static void main(String[] args) {
            int timeLimit = 35;
            int maxProcessingTime = 15;
            int minProcessingTime = 5;
            int noQueues = 5;
            int noClients = 10;

            SimulationManager simulationManager = new SimulationManager(timeLimit, maxProcessingTime, minProcessingTime,10,5, noQueues, noClients,SelectionPolicy.SHORTEST_TIME);

            simulationManager.generateNRandomTasks();

            System.out.println(simulationManager.generatedClients.size());

            Thread simulationThread = new Thread(simulationManager);
            simulationThread.start();

        }

    }