package view;

import javax.swing.*;
import java.awt.*;

public class ReservationValidationView extends JFrame {
    public ReservationValidationView() {
        setTitle("Validation des Réservations");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BorderLayout());

        JTable table = new JTable(); // à compléter avec un TableModel
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton validerBtn = new JButton("Valider");
        JButton rejeterBtn = new JButton("Rejeter");
        buttonPanel.add(validerBtn);
        buttonPanel.add(rejeterBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);
        setVisible(true);
    }
}