package view;

import javax.swing.*;
import java.awt.*;

public class AccueilView extends JFrame {
    public AccueilView() {
        setTitle("Système de Réservation");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer la fenêtre

        // Création des composants
        JLabel label = new JLabel("Bienvenue !");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JButton bouton = new JButton("Réserver une salle");

        // Mise en page
        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);
        add(bouton, BorderLayout.SOUTH);

        setVisible(true);
    }
}
