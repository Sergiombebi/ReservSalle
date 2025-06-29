package view;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import controller.NavigationController;

public class SalleGestionView extends JFrame {

    public SalleGestionView() {
        // Configuration de la fenêtre
        setTitle("Gestion des Salles");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600)); // Taille minimale pour une meilleure lisibilité
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Panel principal avec BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 245));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header avec titre et bouton retour
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(new Color(50, 60, 70));
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Gestion des Salles");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JButton backBtn = createIconButton("◀", new Color(200, 200, 200), 20);
        backBtn.addActionListener(e -> {
            dispose();
            NavigationController.showAdminAccueil();
        });
        backBtn.setToolTipText("Retour à l'accueil");

        headerPanel.add(backBtn, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel des actions avec GridBagLayout pour un meilleur contrôle
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(new Color(240, 240, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Boutons d'action avec icônes et tooltips
        JButton addBtn = createActionButton("Ajouter", new Color(46, 204, 113), "➕", "Ajouter une nouvelle salle");
        JButton updateBtn = createActionButton("Modifier", new Color(52, 152, 219), "✏️", "Modifier une salle existante");
        JButton deleteBtn = createActionButton("Supprimer", new Color(231, 76, 60), "❌", "Supprimer une salle (avec confirmation)");
        JButton listBtn = createActionButton("Liste", new Color(155, 89, 182), "📋", "Voir la liste des salles");

        // Disposition dans GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0;
        actionPanel.add(addBtn, gbc);
        gbc.gridx = 1;
        actionPanel.add(updateBtn, gbc);
        gbc.gridx = 2;
        actionPanel.add(deleteBtn, gbc);
        gbc.gridx = 3;
        actionPanel.add(listBtn, gbc);

        mainPanel.add(actionPanel, BorderLayout.WEST);

        // Panel pour l'affichage de la liste des salles
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));
        listPanel.setBackground(Color.WHITE);

        JList<String> salleList = new JList<>(new String[]{"Salle 1", "Salle 2", "Salle 3"}); // Remplacer par données dynamiques
        salleList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salleList.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(salleList);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel listTitle = new JLabel("Salles existantes");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        listTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        listPanel.add(listTitle, BorderLayout.NORTH);

        mainPanel.add(listPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JButton createActionButton(String text, Color bgColor, String icon, String tooltip) {
        JButton button = new JButton("<html><center>" + icon + "<br>" + text + "</center></html>");
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 120));

        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        button.setToolTipText(tooltip);
        return button;
    }

    private JButton createIconButton(String icon, Color color, int size) {
        JButton button = new JButton(icon);
        button.setFont(new Font("Segoe UI", Font.PLAIN, size));
        button.setForeground(color);
        button.setBackground(new Color(50, 60, 70));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        return button;
    }
}