package view;

import javax.swing.*;
import java.awt.*;

public class ReservationCreationView extends JFrame {
    public ReservationCreationView() {
        setTitle("Nouvelle Réservation");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Salle:"));
        JComboBox<String> salleBox = new JComboBox<>();
        panel.add(salleBox);

        panel.add(new JLabel("Date:"));
        JTextField dateField = new JTextField();
        panel.add(dateField);

        panel.add(new JLabel("Heure début:"));
        JTextField heureDebut = new JTextField();
        panel.add(heureDebut);

        panel.add(new JLabel("Heure fin:"));
        JTextField heureFin = new JTextField();
        panel.add(heureFin);

        panel.add(new JLabel("Ressources nécessaires:"));
        JTextField ressourceField = new JTextField();
        panel.add(ressourceField);

        JButton reserverBtn = new JButton("Réserver");
        panel.add(new JLabel());
        panel.add(reserverBtn);

        add(panel);
        setVisible(true);
    }
}