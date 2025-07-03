package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

import controller.NavigationController;
import dao.UtilisateurDAO;
import model.Utilisateur;

public class ConnexionView extends JFrame {

    public ConnexionView() {
        setTitle("Connexion - Réservation de Salles");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Look and Feel système
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = new JLabel("Bienvenue !", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(33, 37, 41));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 15);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);

        JTextField userField = new JTextField();
        userField.setFont(fieldFont);
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JPasswordField passField = new JPasswordField();
        passField.setFont(fieldFont);
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"admin", "Demandeur", "Responsable"});
        roleCombo.setFont(fieldFont);
        roleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JButton loginButton = new JButton("Se connecter");
        styleButton(loginButton, new Color(0, 123, 255));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ajout des composants au formulaire
        formPanel.add(new JLabel("Nom d'utilisateur :"));
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(userField);
        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(new JLabel("Mot de passe :"));
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(passField);
        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(new JLabel("Rôle :"));
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(roleCombo);
        formPanel.add(Box.createVerticalStrut(25));

        formPanel.add(loginButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Action de connexion
        loginButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String role = roleCombo.getSelectedItem().toString().toLowerCase();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            UtilisateurDAO dao = new UtilisateurDAO();
            Utilisateur user = dao.verifierConnexion(username, password, role);

            if (user != null) {

                switch (role) {
                    case "admin":
                        NavigationController.showAdminAccueil();
                        break;
                    case "demandeur":
                        try {
                            NavigationController.showDemandeurAccueil(user.getId());
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                    case "responsable":
                        try {
                            NavigationController.showResponsableDashboard(user.getId());
                        }catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Identifiants incorrects ou rôle invalide.",
                        "Erreur de connexion",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        add(mainPanel);
        setVisible(true);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }
}
