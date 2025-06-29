package view;

import javax.swing.*;
import java.awt.*;

public class CalendrierView extends JFrame {
    public CalendrierView() {
        setTitle("Calendrier des Réservations");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        JTable calendrierTable = new JTable(); // à compléter avec un modèle
        JScrollPane scrollPane = new JScrollPane(calendrierTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        setVisible(true);
    }
}