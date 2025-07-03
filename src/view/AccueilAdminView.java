package view;

import dao.DatabaseManager;
import dao.ReservationDAO;
import dao.RessourceDAO;
import dao.SalleDAO;
import model.Reservation;
import model.Ressource;
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
        tabbedPane.addTab("👥 Utilisateurs", createUsersPanel());
        tabbedPane.addTab("🏢 Salles", createSallesPanel());
        tabbedPane.addTab("📦 Ressources", createRessourcesPanel());
        tabbedPane.addTab("📋 Réservations", createReservationsPanel());
        tabbedPane.addTab("🗓 Calendrier par Salle", createCalendrierPanel());




        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 240));

        JLabel titleLabel = new JLabel("Tableau de bord Administrateur", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 70, 70));

        JButton logoutBtn = createStyledButton("Déconnexion", new Color(192, 57, 43));
        logoutBtn.setPreferredSize(new Dimension(130, 35));
        logoutBtn.addActionListener(e -> {
            dispose(); // Ferme cette fenêtre
            new ConnexionView().setVisible(true); // Redirige vers la vue de connexion
        });

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);


        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel("© 2023 Système de gestion", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerLabel.setForeground(Color.GRAY);
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createCalendrierPanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel salleLabel = new JLabel("Sélectionner une salle:");
        JComboBox<String> salleCombo = new JComboBox<>();

        DefaultTableModel model = new DefaultTableModel(new String[]{
                "Date", "Début", "Fin", "Réservé par", "État"
        }, 0);
        JTable calendarTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(calendarTable);

        topPanel.add(salleLabel);
        topPanel.add(salleCombo);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Remplir les salles
        SalleDAO salleDAO = new SalleDAO();
        List<Salle> salles = salleDAO.getAll();
        for (Salle s : salles) {
            salleCombo.addItem(s.getNom());
        }

        // Écouteur pour afficher les réservations
        salleCombo.addActionListener(e -> {
            String nomSalle = (String) salleCombo.getSelectedItem();
            if (nomSalle == null) return;

            try {
                ReservationDAO reservationDAO = new ReservationDAO();
                List<Reservation> reservations = reservationDAO.getReservationsParSalle(nomSalle);

                model.setRowCount(0); // Vider le tableau
                for (Reservation r : reservations) {
                    model.addRow(new Object[]{
                            r.getDate(), r.getHeureDebut(), r.getHeureFin(), r.getnom(), r.getEtat()
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
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
        panel.setBackground(new Color(245, 245, 250)); // couleur claire moderne

        SalleDAO salleDAO = new SalleDAO();

        DefaultTableModel salleModel = new DefaultTableModel(new String[]{"ID", "Nom", "Capacité", "Type"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // empêcher l'édition directe
            }
        };
        JTable table = new JTable(salleModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);

        // Charger les salles
        loadSalles(salleDAO, salleModel);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Boutons Ajouter, Modifier, Supprimer
        JButton addBtn = createStyledButton("Ajouter", new Color(46, 204, 113));      // vert
        JButton editBtn = createStyledButton("Modifier", new Color(52, 152, 219));    // bleu
        JButton deleteBtn = createStyledButton("Supprimer", new Color(231, 76, 60));  // rouge

        // Ajout
        addBtn.addActionListener(e -> {
            JTextField nomField = new JTextField();
            JTextField capaciteField = new JTextField();
            JTextField typeField = new JTextField();

            JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
            form.add(new JLabel("Nom:"));
            form.add(nomField);
            form.add(new JLabel("Capacité:"));
            form.add(capaciteField);
            form.add(new JLabel("Type:"));
            form.add(typeField);

            int res = JOptionPane.showConfirmDialog(this, form, "Ajouter Salle", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    int capacite = Integer.parseInt(capaciteField.getText().trim());
                    Salle salle = new Salle(0, nomField.getText().trim(), capacite, typeField.getText().trim());
                    if (salleDAO.insert(salle)) {
                        loadSalles(salleDAO, salleModel);
                        JOptionPane.showMessageDialog(this, "Salle ajoutée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Capacité invalide (doit être un nombre entier)", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Modification
        editBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une salle à modifier", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = (int) salleModel.getValueAt(selectedRow, 0);
            String nom = (String) salleModel.getValueAt(selectedRow, 1);
            int capacite = (int) salleModel.getValueAt(selectedRow, 2);
            String type = (String) salleModel.getValueAt(selectedRow, 3);

            JTextField nomField = new JTextField(nom);
            JTextField capaciteField = new JTextField(String.valueOf(capacite));
            JTextField typeField = new JTextField(type);

            JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
            form.add(new JLabel("Nom:"));
            form.add(nomField);
            form.add(new JLabel("Capacité:"));
            form.add(capaciteField);
            form.add(new JLabel("Type:"));
            form.add(typeField);

            int res = JOptionPane.showConfirmDialog(this, form, "Modifier Salle", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    int newCapacite = Integer.parseInt(capaciteField.getText().trim());
                    Salle salleModifiee = new Salle(id, nomField.getText().trim(), newCapacite, typeField.getText().trim());

                    // On fait une mise à jour via DAO (à créer)
                    if (updateSalle(salleDAO, salleModifiee)) {
                        loadSalles(salleDAO, salleModel);
                        JOptionPane.showMessageDialog(this, "Salle modifiée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la modification", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Capacité invalide (doit être un nombre entier)", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Suppression
        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une salle à supprimer", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = (int) salleModel.getValueAt(selectedRow, 0);
            String nom = (String) salleModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Êtes-vous sûr de vouloir supprimer la salle \"" + nom + "\" ?",
                    "Confirmation de suppression",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (deleteSalle(salleDAO, id)) {
                        loadSalles(salleDAO, salleModel);
                        JOptionPane.showMessageDialog(this, "Salle supprimée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la suppression", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur SQL: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(new Color(245, 245, 250));
        footer.add(addBtn);
        footer.add(editBtn);
        footer.add(deleteBtn);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    // Méthode pour charger les salles dans le modèle de tableau
    private void loadSalles(SalleDAO salleDAO, DefaultTableModel salleModel) {
        salleModel.setRowCount(0);
        for (Salle s : salleDAO.getAll()) {
            salleModel.addRow(new Object[]{s.getId(), s.getNom(), s.getCapacite(), s.getType()});
        }
    }

    // Méthode pour mettre à jour une salle
    private boolean updateSalle(SalleDAO salleDAO, Salle salle) {
        // Implémente dans SalleDAO une méthode update() puis appelle ici
        return salleDAO.update(salle);
    }

    // Méthode pour supprimer une salle
    private boolean deleteSalle(SalleDAO salleDAO, int idSalle) throws SQLException {
        return salleDAO.delete(idSalle);
    }



    private JPanel createRessourcesPanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 250)); // fond clair

        RessourceDAO ressourceDAO = new RessourceDAO();

        DefaultTableModel ressourceModel = new DefaultTableModel(new String[]{"ID", "Nom", "Description", "Quantité", "État"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(ressourceModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);

        loadRessources(ressourceDAO, ressourceModel);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Boutons CRUD
        JButton addBtn = createStyledButton("Ajouter", new Color(46, 204, 113));
        JButton editBtn = createStyledButton("Modifier", new Color(52, 152, 219));
        JButton deleteBtn = createStyledButton("Supprimer", new Color(231, 76, 60));

        // Ajouter
        addBtn.addActionListener(e -> {
            JTextField nomField = new JTextField();
            JTextArea descriptionArea = new JTextArea(4, 20);
            JTextField quantiteField = new JTextField();
            String[] etats = {"disponible", "en_panne"};
            JComboBox<String> etatCombo = new JComboBox<>(etats);

            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = 0;
            form.add(new JLabel("Nom:"), gbc);
            gbc.gridx = 1;
            form.add(nomField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            form.add(new JLabel("Description:"), gbc);
            gbc.gridx = 1;
            JScrollPane scrollDesc = new JScrollPane(descriptionArea);
            form.add(scrollDesc, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            form.add(new JLabel("Quantité:"), gbc);
            gbc.gridx = 1;
            form.add(quantiteField, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            form.add(new JLabel("État:"), gbc);
            gbc.gridx = 1;
            form.add(etatCombo, gbc);

            int res = JOptionPane.showConfirmDialog(this, form, "Ajouter Ressource", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    int quantite = Integer.parseInt(quantiteField.getText().trim());
                    String nom = nomField.getText().trim();
                    String description = descriptionArea.getText().trim();
                    String etat = (String) etatCombo.getSelectedItem();

                    if (nom.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Le nom est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Ressource ressource = new Ressource(0, nom, description, quantite, etat);
                    if (ressourceDAO.insert(ressource)) {
                        loadRessources(ressourceDAO, ressourceModel);
                        JOptionPane.showMessageDialog(this, "Ressource ajoutée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Quantité invalide (doit être un nombre entier)", "Erreur", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur SQL: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Modifier
        editBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une ressource à modifier", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = (int) ressourceModel.getValueAt(selectedRow, 0);
            String nom = (String) ressourceModel.getValueAt(selectedRow, 1);
            String description = (String) ressourceModel.getValueAt(selectedRow, 2);
            int quantite = (int) ressourceModel.getValueAt(selectedRow, 3);
            String etat = (String) ressourceModel.getValueAt(selectedRow, 4);

            JTextField nomField = new JTextField(nom);
            JTextArea descriptionArea = new JTextArea(description, 4, 20);
            JTextField quantiteField = new JTextField(String.valueOf(quantite));
            String[] etats = {"disponible", "en_panne"};
            JComboBox<String> etatCombo = new JComboBox<>(etats);
            etatCombo.setSelectedItem(etat);

            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = 0;
            form.add(new JLabel("Nom:"), gbc);
            gbc.gridx = 1;
            form.add(nomField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            form.add(new JLabel("Description:"), gbc);
            gbc.gridx = 1;
            JScrollPane scrollDesc = new JScrollPane(descriptionArea);
            form.add(scrollDesc, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            form.add(new JLabel("Quantité:"), gbc);
            gbc.gridx = 1;
            form.add(quantiteField, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            form.add(new JLabel("État:"), gbc);
            gbc.gridx = 1;
            form.add(etatCombo, gbc);

            int res = JOptionPane.showConfirmDialog(this, form, "Modifier Ressource", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    int newQuantite = Integer.parseInt(quantiteField.getText().trim());
                    String newNom = nomField.getText().trim();
                    String newDesc = descriptionArea.getText().trim();
                    String newEtat = (String) etatCombo.getSelectedItem();

                    if (newNom.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Le nom est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Ressource ressourceModifiee = new Ressource(id, newNom, newDesc, newQuantite, newEtat);
                    if (ressourceDAO.update(ressourceModifiee)) {
                        loadRessources(ressourceDAO, ressourceModel);
                        JOptionPane.showMessageDialog(this, "Ressource modifiée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la modification", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Quantité invalide (doit être un nombre entier)", "Erreur", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur SQL: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Supprimer
        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une ressource à supprimer", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = (int) ressourceModel.getValueAt(selectedRow, 0);
            String nom = (String) ressourceModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Êtes-vous sûr de vouloir supprimer la ressource \"" + nom + "\" ?",
                    "Confirmation de suppression",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (ressourceDAO.delete(id)) {
                        loadRessources(ressourceDAO, ressourceModel);
                        JOptionPane.showMessageDialog(this, "Ressource supprimée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la suppression", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur SQL: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(new Color(245, 245, 250));
        footer.add(addBtn);
        footer.add(editBtn);
        footer.add(deleteBtn);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    // Méthode pour charger les ressources dans le tableau
    private void loadRessources(RessourceDAO ressourceDAO, DefaultTableModel model) {
        model.setRowCount(0);
        try {
            for (Ressource r : ressourceDAO.getAll()) {
                model.addRow(new Object[]{r.getId(), r.getNom(), r.getDescription(), r.getQuantite(), r.getEtat()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des ressources: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }


    private JPanel createReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        DefaultTableModel reservationModel = new DefaultTableModel(new String[]{
                "Utilisateur", "Salle", "Date", "Début", "Fin", "État"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(reservationModel);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        try {
            ReservationDAO reservationDAO = new ReservationDAO();
            List<Reservation> reservations = reservationDAO.getAllReservations();
            for (Reservation r : reservations) {
                reservationModel.addRow(new Object[]{
                        r.getnom(),
                        r.getNomSalle(),
                        r.getDate(),
                        r.getHeureDebut(),
                        r.getHeureFin(),
                        r.getEtat()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des réservations : " + e.getMessage());
        }

        return panel;
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
}