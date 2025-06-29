package view;

import dao.DatabaseManager;
import dao.SalleDAO;
import model.Salle;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccueilAdminView extends JFrame {
    private DefaultTableModel userModel;
    private JTable userTable;
    private Connection connection;

    public AccueilAdminView() throws SQLException {
        // Initialisation de la connexion à la base de données
        try {
            connection = DatabaseManager.getConnection();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur de connexion à la base de données: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("Tableau de bord - Administrateur");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Utilisateurs", createUsersPanel());
        tabbedPane.addTab("Salles", createSallesPanel());
        tabbedPane.addTab("Ressources", createRessourcesPanel());
        tabbedPane.addTab("Réservations", createReservationsPanel());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Tableau de bord Administrateur", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 70, 70));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel("© 2023 Système de gestion", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerLabel.setForeground(Color.GRAY);
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Modèle de tableau pour les utilisateurs
        String[] userColumns = {"ID", "Nom", "Email", "Rôle"};
        userModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userModel);
        JScrollPane userScrollPane = new JScrollPane(userTable);

        // Charger les utilisateurs depuis la base de données
        loadUsersFromDatabase();

        // Boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addUserBtn = createStyledButton("Ajouter", new Color(46, 204, 113));
        JButton editUserBtn = createStyledButton("Modifier", new Color(52, 152, 219));
        JButton deleteUserBtn = createStyledButton("Supprimer", new Color(231, 76, 60));

        addUserBtn.addActionListener(e -> showAddUserDialog());
        editUserBtn.addActionListener(e -> showEditUserDialog());
        deleteUserBtn.addActionListener(e -> deleteSelectedUser());

        buttonPanel.add(addUserBtn);
        buttonPanel.add(editUserBtn);
        buttonPanel.add(deleteUserBtn);

        panel.add(new JLabel("Gestion des utilisateurs", SwingConstants.LEFT), BorderLayout.NORTH);
        panel.add(userScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadUsersFromDatabase() {
        try {
            userModel.setRowCount(0); // Vider le tableau

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, nom, email, role FROM utilisateur");

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("role")
                };
                userModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des utilisateurs: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Ajouter un utilisateur", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Nom:");
        JTextField nameField = new JTextField();
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Mot de passe:");
        JPasswordField passwordField = new JPasswordField();
        JLabel roleLabel = new JLabel("Rôle:");
        String[] roles = {"admin", "demandeur", "responsable"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);

        JButton saveBtn = createStyledButton("Enregistrer", new Color(46, 204, 113));
        JButton cancelBtn = createStyledButton("Annuler", new Color(231, 76, 60));

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleCombo.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Tous les champs sont obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String sql = "INSERT INTO utilisateur (nom, email, mot_de_passe, role) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, password);
                pstmt.setString(4, role);
                pstmt.executeUpdate();

                // Récupérer l'ID généré
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    userModel.addRow(new Object[]{id, name, email, role});
                }

                dialog.dispose();
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1062) { // Duplicate entry
                    JOptionPane.showMessageDialog(dialog, "Cet email est déjà utilisé", "Erreur", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de l'ajout: " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(roleLabel);
        panel.add(roleCombo);
        panel.add(saveBtn);
        panel.add(cancelBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à modifier", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) userModel.getValueAt(selectedRow, 0);
        String name = (String) userModel.getValueAt(selectedRow, 1);
        String email = (String) userModel.getValueAt(selectedRow, 2);
        String role = (String) userModel.getValueAt(selectedRow, 3);

        JDialog dialog = new JDialog(this, "Modifier l'utilisateur", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField(String.valueOf(id));
        idField.setEditable(false);
        JLabel nameLabel = new JLabel("Nom:");
        JTextField nameField = new JTextField(name);
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(email);
        JLabel roleLabel = new JLabel("Rôle:");
        String[] roles = {"admin", "demandeur", "responsable"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        roleCombo.setSelectedItem(role);

        JButton saveBtn = createStyledButton("Enregistrer", new Color(46, 204, 113));
        JButton cancelBtn = createStyledButton("Annuler", new Color(231, 76, 60));

        saveBtn.addActionListener(e -> {
            String newName = nameField.getText().trim();
            String newEmail = emailField.getText().trim();
            String newRole = (String) roleCombo.getSelectedItem();

            if (newName.isEmpty() || newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Le nom et l'email sont obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String sql = "UPDATE utilisateur SET nom = ?, email = ?, role = ? WHERE id = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, newName);
                pstmt.setString(2, newEmail);
                pstmt.setString(3, newRole);
                pstmt.setInt(4, id);
                pstmt.executeUpdate();

                // Mettre à jour le tableau
                userModel.setValueAt(newName, selectedRow, 1);
                userModel.setValueAt(newEmail, selectedRow, 2);
                userModel.setValueAt(newRole, selectedRow, 3);

                dialog.dispose();
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1062) { // Duplicate entry
                    JOptionPane.showMessageDialog(dialog, "Cet email est déjà utilisé", "Erreur", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de la modification: " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        panel.add(idLabel);
        panel.add(idField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(roleLabel);
        panel.add(roleCombo);
        panel.add(saveBtn);
        panel.add(cancelBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à supprimer", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) userModel.getValueAt(selectedRow, 0);
        String name = (String) userModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer l'utilisateur " + name + " ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Vérifier d'abord si l'utilisateur a des réservations
                PreparedStatement checkStmt = connection.prepareStatement(
                        "SELECT COUNT(*) FROM reservation WHERE id_utilisateur = ?");
                checkStmt.setInt(1, id);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                int reservationCount = rs.getInt(1);

                if (reservationCount > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Impossible de supprimer: cet utilisateur a des réservations actives",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Supprimer l'utilisateur
                PreparedStatement pstmt = connection.prepareStatement(
                        "DELETE FROM utilisateur WHERE id = ?");
                pstmt.setInt(1, id);
                pstmt.executeUpdate();

                userModel.removeRow(selectedRow);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la suppression: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Méthodes pour les autres onglets (à implémenter de manière similaire)
    private JPanel createSallesPanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        SalleDAO salleDAO = new SalleDAO();

        DefaultTableModel salleModel = new DefaultTableModel(new String[]{"ID", "Nom", "Capacité", "Type"}, 0);
        JTable table = new JTable(salleModel);

        for (Salle s : salleDAO.getAll()) {
            salleModel.addRow(new Object[]{s.getId(), s.getNom(), s.getCapacite(), s.getType()});
        }

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton addBtn = new JButton("Ajouter");
        addBtn.addActionListener(e -> {
            JTextField nomField = new JTextField();
            JTextField capaciteField = new JTextField();
            JTextField typeField = new JTextField();

            JPanel form = new JPanel(new GridLayout(0, 1));
            form.add(new JLabel("Nom:"));
            form.add(nomField);
            form.add(new JLabel("Capacité:"));
            form.add(capaciteField);
            form.add(new JLabel("Type:"));
            form.add(typeField);

            int res = JOptionPane.showConfirmDialog(this, form, "Ajouter Salle", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    int capacite = Integer.parseInt(capaciteField.getText().trim());
                    Salle salle = new Salle(0, nomField.getText(), capacite, typeField.getText());
                    if (salleDAO.insert(salle)) {
                        salleModel.setRowCount(0);
                        for (Salle s : salleDAO.getAll()) {
                            salleModel.addRow(new Object[]{s.getId(), s.getNom(), s.getCapacite(), s.getType()});
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Capacité invalide");
                }
            }
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.add(addBtn);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }


    private JPanel createRessourcesPanel() {
        // Implémentation similaire avec connexion à la base de données
        return new JPanel();
    }

    private JPanel createReservationsPanel() {
        // Implémentation similaire avec connexion à la base de données
        return new JPanel();
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public static void main(String[] args) {
        // Charger le driver JDBC
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur: Driver JDBC MySQL non trouvé");
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new AccueilAdminView();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}