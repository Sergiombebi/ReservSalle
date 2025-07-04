package view;

import controller.NavigationController;
import dao.UtilisateurDAO;
import model.Utilisateur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ConnexionView extends JFrame {
    
    // Couleurs elegantes et modernes
    private static final Color PRIMARY_COLOR = new Color(52, 73, 94);
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;
    private JLabel statusLabel;

    public ConnexionView() {
        initializeUI();
        setVisible(true);
    }
    
    private void initializeUI() {
        setTitle("ReservSalle - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Formulaire central
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 120));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("ReservSalle", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Systeme de Reservation", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        return headerPanel;
    }
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setLayout(new GridBagLayout());
        
        // Carte de connexion
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setPreferredSize(new Dimension(400, 450));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titre
        JLabel titleLabel = new JLabel("CONNEXION", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        cardPanel.add(titleLabel, gbc);
        
        // Nom d'utilisateur
        gbc.gridwidth = 1; gbc.gridy = 1;
        JLabel userLabel = new JLabel("Nom d'utilisateur:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        cardPanel.add(userLabel, gbc);
        
        gbc.gridy = 2;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(300, 40));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        cardPanel.add(usernameField, gbc);
        
        // Mot de passe
        gbc.gridy = 3;
        JLabel passLabel = new JLabel("Mot de passe:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        cardPanel.add(passLabel, gbc);
        
        gbc.gridy = 4;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        cardPanel.add(passwordField, gbc);
        
        // Role
        gbc.gridy = 5;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        cardPanel.add(roleLabel, gbc);
        
        gbc.gridy = 6;
        String[] roles = {"Administrateur", "Demandeur", "Responsable"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        roleComboBox.setPreferredSize(new Dimension(300, 40));
        roleComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        cardPanel.add(roleComboBox, gbc);
        
        // Bouton de connexion - BIEN VISIBLE
        gbc.gridy = 7;
        gbc.insets = new Insets(30, 10, 10, 10);
        loginButton = new JButton("CONNEXION");
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setBackground(ACCENT_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(300, 50));
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setOpaque(true);
        loginButton.addActionListener(new LoginActionListener());
        
        // Effet hover
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(ACCENT_COLOR);
            }
        });
        
        cardPanel.add(loginButton, gbc);
        
        // Status
        gbc.gridy = 8;
        gbc.insets = new Insets(15, 10, 10, 10);
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        cardPanel.add(statusLabel, gbc);
        
        centerPanel.add(cardPanel);
        return centerPanel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(PRIMARY_COLOR);
        footerPanel.setPreferredSize(new Dimension(0, 80));
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JButton helpButton = new JButton("Aide - Comptes de demonstration");
        helpButton.setFont(new Font("Arial", Font.PLAIN, 13));
        helpButton.setBackground(Color.WHITE);
        helpButton.setForeground(PRIMARY_COLOR);
        helpButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        helpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        helpButton.addActionListener(e -> showHelpDialog());
        
        footerPanel.add(helpButton);
        return footerPanel;
    }
    
    private void showHelpDialog() {
        String helpMessage = """
            Comptes de demonstration disponibles:
            
            ADMINISTRATEUR:
            - Nom d'utilisateur: admin
            - Mot de passe: admin123
            
            DEMANDEUR:
            - Nom d'utilisateur: jean.dupont@exemple.com
            - Mot de passe: password123
            
            RESPONSABLE:
            - Nom d'utilisateur: pierre.durand@exemple.com
            - Mot de passe: password123
            """;
        
        JOptionPane.showMessageDialog(this, helpMessage, "Aide - Comptes de demonstration", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
        
        Timer timer = new Timer(5000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
    
    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String selectedRole = (String) roleComboBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                showStatus("Veuillez remplir tous les champs", ERROR_COLOR);
                return;
            }

            String role;
            switch (selectedRole) {
                case "Administrateur": role = "admin"; break;
                case "Demandeur": role = "demandeur"; break;
                case "Responsable": role = "responsable"; break;
                default: role = "admin";
            }
            
            loginButton.setEnabled(false);
            loginButton.setText("CONNEXION...");
            
            SwingUtilities.invokeLater(() -> {
                try {
            UtilisateurDAO dao = new UtilisateurDAO();
            Utilisateur user = dao.verifierConnexion(username, password, role);

            if (user != null) {
                        showStatus("Connexion reussie!", SUCCESS_COLOR);

                        Timer successTimer = new Timer(1000, successEvent -> {
                            dispose();
                            try {
                switch (role) {
                    case "admin":
                        NavigationController.showAdminAccueil();
                        break;
                    case "demandeur":
                            NavigationController.showDemandeurAccueil(user.getId());
                        break;
                    case "responsable":
                            NavigationController.showResponsableDashboard(user.getId());
                        break;
                }
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(ConnexionView.this, 
                                    "Erreur de navigation: " + ex.getMessage(), 
                                    "Erreur", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                        successTimer.setRepeats(false);
                        successTimer.start();
                        
                    } else {
                        showStatus("Identifiants incorrects", ERROR_COLOR);
                        loginButton.setEnabled(true);
                        loginButton.setText("CONNEXION");
                    }
                } catch (Exception ex) {
                    showStatus("Erreur de connexion: " + ex.getMessage(), ERROR_COLOR);
                    loginButton.setEnabled(true);
                    loginButton.setText("CONNEXION");
            }
        });
    }
}
} 