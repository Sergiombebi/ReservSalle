package view;

import javax.swing.*;
import java.awt.*;

public class AccueilResponsableView extends JFrame {
    public AccueilResponsableView() {
        setTitle("Tableau de bord - Responsable");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        JButton validerBtn = new JButton("Valider les réservations");
        JButton historiqueBtn = new JButton("Consulter les réservations traitées");

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(validerBtn);
        panel.add(historiqueBtn);

        add(panel);
        setVisible(true);
    }
}