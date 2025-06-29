package view;

import javax.swing.*;
import java.awt.*;

public class AccueilUserView extends JFrame {
    public AccueilUserView() {
        setTitle("Tableau de bord - Utilisateur");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        JButton createBtn = new JButton("Créer une réservation");
        JButton historiqueBtn = new JButton("Consulter mes réservations");

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(createBtn);
        panel.add(historiqueBtn);

        add(panel);
        setVisible(true);
    }
}