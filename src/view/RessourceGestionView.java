package view;

import javax.swing.*;
import java.awt.*;

public class RessourceGestionView extends JFrame {
    public RessourceGestionView() {
        setTitle("Gestion des Ressources");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        JButton addBtn = new JButton("Ajouter une ressource");
        JButton updateBtn = new JButton("Modifier une ressource");
        JButton deleteBtn = new JButton("Supprimer une ressource");
        JButton listBtn = new JButton("Lister les ressources");

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(listBtn);

        add(panel);
        setVisible(true);
    }
}