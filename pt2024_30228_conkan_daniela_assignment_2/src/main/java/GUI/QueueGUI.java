package GUI;

import BusinessLogic.SelectionPolicy;
import BusinessLogic.SimulationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QueueGUI extends JFrame {
    private final JTextField timeLimitField;
    private final JTextField maxProcessingTimeField;
    private final JTextField minProcessingTimeField;
    private final JTextField maxArrivalTimeField;
    private final JTextField minArrivalTimeField;
    private final JTextField noQueuesField;
    private final JTextField noClientsField;
    private final JComboBox<String> strategyComboBox;

    public QueueGUI() {
        setTitle("Simulation Setup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(9, 2));

        // Set pink background color for the JFrame
        getContentPane().setBackground(new Color(255, 192, 203));

        add(new JLabel("Time Limit:"));
        timeLimitField = new JTextField();
        timeLimitField.setBackground(new Color(255, 160, 180));
        add(timeLimitField);

        add(new JLabel("Max Processing Time:"));
        maxProcessingTimeField = new JTextField();
        maxProcessingTimeField.setBackground(new Color(255, 160, 180));
        add(maxProcessingTimeField);

        add(new JLabel("Min Processing Time:"));
        minProcessingTimeField = new JTextField();
        minProcessingTimeField.setBackground(new Color(255, 160, 180));
        add(minProcessingTimeField);

        add(new JLabel("Max Arrival Time:"));
        maxArrivalTimeField = new JTextField();
        maxArrivalTimeField.setBackground(new Color(255, 160, 180));
        add(maxArrivalTimeField);

        add(new JLabel("Min Arrival Time:"));
        minArrivalTimeField = new JTextField();
        minArrivalTimeField.setBackground(new Color(255, 160, 180));
        add(minArrivalTimeField);

        add(new JLabel("Number of Queues:"));
        noQueuesField = new JTextField();
        noQueuesField.setBackground(new Color(255, 160, 180));
        add(noQueuesField);

        add(new JLabel("Number of Clients:"));
        noClientsField = new JTextField();
        noClientsField.setBackground(new Color(255, 160, 180));
        add(noClientsField);

        add(new JLabel("Strategy:"));
        String[] strategies = {"Min Queue", "Min Waiting Time"};
        strategyComboBox = new JComboBox<>(strategies);
        strategyComboBox.setBackground(new Color(255, 160, 180));
        add(strategyComboBox);

        JButton runButton = new JButton("Run");
        runButton.setBackground(new Color(255, 160, 180));
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int timeLimit = Integer.parseInt(timeLimitField.getText());
                int maxProcessingTime = Integer.parseInt(maxProcessingTimeField.getText());
                int minProcessingTime = Integer.parseInt(minProcessingTimeField.getText());
                int maxArrivalTime = Integer.parseInt(maxArrivalTimeField.getText());
                int minArrivalTime = Integer.parseInt(minArrivalTimeField.getText());
                int noQueues = Integer.parseInt(noQueuesField.getText());
                int noClients = Integer.parseInt(noClientsField.getText());

                SelectionPolicy strategy;
                if (strategyComboBox.getSelectedIndex() == 0) {
                    strategy = SelectionPolicy.SHORTEST_QUEUE;
                } else {
                    strategy = SelectionPolicy.SHORTEST_TIME;
                }

                dispose();

                SimulationManager simulationManager = new SimulationManager(timeLimit, maxProcessingTime, minProcessingTime, maxArrivalTime, minArrivalTime, noQueues, noClients, strategy);

                simulationManager.generateNRandomTasks();

                Thread simulationThread = new Thread(simulationManager);
                simulationThread.start();
            }
        });
        add(runButton);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new QueueGUI();
            }
        });
    }
}
