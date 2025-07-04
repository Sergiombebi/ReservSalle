package view;

import dao.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class AccueilAdminView extends JFrame {
    // Couleurs coherentes avec la page de connexion
    private static final Color PRIMARY_COLOR = new Color(52, 73, 94);
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);

    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(52, 73, 94);

    private DefaultTableModel userModel, salleModel, ressourceModel, reservationModel;
    private JTable userTable, salleTable, ressourceTable, reservationTable;
    private JTabbedPane tabbedPane;

    public AccueilAdminView() throws SQLException {
        try {
            // Vérifier que la connexion est possible
            DatabaseManager.getConnection();
            initializeUI();
            setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur de connexion a la base de donnees: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        }

    private void initializeUI() {
        setTitle("ReservSalle - Administration");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header moderne
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Contenu principal
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // Footer moderne
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        // Titre principal
        JLabel titleLabel = new JLabel("Administration ReservSalle");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Bouton de deconnexion
        JButton logoutBtn = new JButton("Deconnexion");
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.setBackground(ERROR_COLOR);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setPreferredSize(new Dimension(120, 35));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setOpaque(true);
        
        logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutBtn.setBackground(ERROR_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutBtn.setBackground(ERROR_COLOR);
            }
        });
        
        logoutBtn.addActionListener(e -> {
            dispose();
            new ConnexionView();
        });

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutBtn, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        centerPanel.setLayout(new BorderLayout());

        // Onglets modernes
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(CARD_COLOR);

        tabbedPane.addTab("Utilisateurs", createUsersPanel());
        tabbedPane.addTab("Salles", createSallesPanel());
        tabbedPane.addTab("Ressources", createRessourcesPanel());
        tabbedPane.addTab("Reservations", createReservationsPanel());

        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(PRIMARY_COLOR);
        footerPanel.setPreferredSize(new Dimension(0, 50));
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel footerLabel = new JLabel("ReservSalle v1.0 - Systeme de Reservation Moderne");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(200, 200, 200));

        footerPanel.add(footerLabel);
        return footerPanel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre de section
        JLabel sectionTitle = new JLabel("Gestion des Utilisateurs");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 18));
        sectionTitle.setForeground(TEXT_COLOR);
        sectionTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Table moderne
        String[] userColumns = {"ID", "Nom", "Email", "Role", "Actif"};
        userModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(userModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 13));
        userTable.setRowHeight(30);
        userTable.setGridColor(new Color(220, 220, 220));
        userTable.setSelectionBackground(ACCENT_COLOR.brighter());
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        userTable.getTableHeader().setBackground(new Color(240, 240, 240));

        JScrollPane userScrollPane = new JScrollPane(userTable);
        userScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        // Boutons modernes
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton addUserBtn = createModernButton("Ajouter Utilisateur", SUCCESS_COLOR);
        JButton editUserBtn = createModernButton("Modifier", ACCENT_COLOR);
        JButton deleteUserBtn = createModernButton("Supprimer", ERROR_COLOR);

        addUserBtn.addActionListener(e -> showAddUserDialog());
        editUserBtn.addActionListener(e -> showEditUserDialog());
        deleteUserBtn.addActionListener(e -> deleteSelectedUser());

        buttonPanel.add(addUserBtn);
        buttonPanel.add(editUserBtn);
        buttonPanel.add(deleteUserBtn);

        // Chargement des donnees
        loadUsersFromDatabase();

        panel.add(sectionTitle, BorderLayout.NORTH);
        panel.add(userScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSallesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel sectionTitle = new JLabel("Gestion des Salles");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 18));
        sectionTitle.setForeground(TEXT_COLOR);
        sectionTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        String[] salleColumns = {"ID", "Nom", "Capacite", "Type", "Localisation"};
        salleModel = new DefaultTableModel(salleColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        salleTable = new JTable(salleModel);
        salleTable.setFont(new Font("Arial", Font.PLAIN, 13));
        salleTable.setRowHeight(30);
        salleTable.setGridColor(new Color(220, 220, 220));
        salleTable.setSelectionBackground(ACCENT_COLOR.brighter());
        salleTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        salleTable.getTableHeader().setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(salleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton addSalleBtn = createModernButton("Ajouter Salle", SUCCESS_COLOR);
        JButton editSalleBtn = createModernButton("Modifier", ACCENT_COLOR);
        JButton deleteSalleBtn = createModernButton("Supprimer", ERROR_COLOR);

        buttonPanel.add(addSalleBtn);
        buttonPanel.add(editSalleBtn);
        buttonPanel.add(deleteSalleBtn);

        // Chargement des salles
        loadSallesFromDatabase();

        panel.add(sectionTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRessourcesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel sectionTitle = new JLabel("Gestion des Ressources");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 18));
        sectionTitle.setForeground(TEXT_COLOR);
        sectionTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        String[] ressourceColumns = {"ID", "Nom", "Description", "Quantite", "Etat"};
        ressourceModel = new DefaultTableModel(ressourceColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ressourceTable = new JTable(ressourceModel);
        ressourceTable.setFont(new Font("Arial", Font.PLAIN, 13));
        ressourceTable.setRowHeight(30);
        ressourceTable.setGridColor(new Color(220, 220, 220));
        ressourceTable.setSelectionBackground(ACCENT_COLOR.brighter());
        ressourceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        ressourceTable.getTableHeader().setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(ressourceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton addRessourceBtn = createModernButton("Ajouter Ressource", SUCCESS_COLOR);
        JButton editRessourceBtn = createModernButton("Modifier", ACCENT_COLOR);
        JButton deleteRessourceBtn = createModernButton("Supprimer", ERROR_COLOR);

        buttonPanel.add(addRessourceBtn);
        buttonPanel.add(editRessourceBtn);
        buttonPanel.add(deleteRessourceBtn);

        // Chargement des ressources
        loadRessourcesFromDatabase();

        panel.add(sectionTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel sectionTitle = new JLabel("Gestion des Reservations");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 18));
        sectionTitle.setForeground(TEXT_COLOR);
        sectionTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        String[] reservationColumns = {"ID", "Utilisateur", "Salle", "Date", "Debut", "Fin", "Etat"};
        reservationModel = new DefaultTableModel(reservationColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reservationTable = new JTable(reservationModel);
        reservationTable.setFont(new Font("Arial", Font.PLAIN, 13));
        reservationTable.setRowHeight(30);
        reservationTable.setGridColor(new Color(220, 220, 220));
        reservationTable.setSelectionBackground(ACCENT_COLOR.brighter());
        reservationTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        reservationTable.getTableHeader().setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(reservationTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton approveBtn = createModernButton("Approuver", SUCCESS_COLOR);
        JButton rejectBtn = createModernButton("Rejeter", ERROR_COLOR);
        JButton viewDetailsBtn = createModernButton("Details", ACCENT_COLOR);

        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);
        buttonPanel.add(viewDetailsBtn);

        // Chargement des reservations
        loadReservationsFromDatabase();

        panel.add(sectionTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(150, 35));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

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

    private void loadUsersFromDatabase() {
        try {
            userModel.setRowCount(0);
            // Donnees de demonstration en attendant l'implementation complete des DAO
            userModel.addRow(new Object[]{"1", "Admin", "admin@exemple.com", "admin", "Actif"});
            userModel.addRow(new Object[]{"2", "Jean Dupont", "jean.dupont@exemple.com", "demandeur", "Actif"});
            userModel.addRow(new Object[]{"3", "Pierre Durand", "pierre.durand@exemple.com", "responsable", "Actif"});
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des utilisateurs: " + e.getMessage());
        }
    }

    private void loadSallesFromDatabase() {
        try {
            salleModel.setRowCount(0);
            // Donnees de demonstration en attendant l'implementation complete des DAO
            salleModel.addRow(new Object[]{"1", "Salle A101", "30", "Cours", "Batiment A"});
            salleModel.addRow(new Object[]{"2", "Salle B203", "50", "Amphitheatre", "Batiment B"});
            salleModel.addRow(new Object[]{"3", "Salle C105", "20", "Laboratoire", "Batiment C"});
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des salles: " + e.getMessage());
        }
    }

    private void loadRessourcesFromDatabase() {
        try {
            ressourceModel.setRowCount(0);
            // Donnees de demonstration en attendant l'implementation complete des DAO
            ressourceModel.addRow(new Object[]{"1", "Videoprojecteur", "Projecteur HD", "10", "disponible"});
            ressourceModel.addRow(new Object[]{"2", "Ordinateur portable", "PC portable", "15", "disponible"});
            ressourceModel.addRow(new Object[]{"3", "Micro-casque", "Micro sans fil", "8", "disponible"});
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des ressources: " + e.getMessage());
        }
    }

    private void loadReservationsFromDatabase() {
        try {
            reservationModel.setRowCount(0);
            // Donnees de demonstration en attendant l'implementation complete des DAO
            reservationModel.addRow(new Object[]{"1", "Jean Dupont", "Salle A101", "2025-01-15", "09:00", "11:00", "en_attente"});
            reservationModel.addRow(new Object[]{"2", "Marie Martin", "Salle B203", "2025-01-16", "14:00", "16:00", "validee"});
            reservationModel.addRow(new Object[]{"3", "Pierre Durand", "Salle C105", "2025-01-17", "10:00", "12:00", "en_attente"});
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des reservations: " + e.getMessage());
        }
    }

    private void showAddUserDialog() {
        JOptionPane.showMessageDialog(this, "Fonctionnalite d'ajout d'utilisateur a implementer", 
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showEditUserDialog() {
        if (userTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez selectionner un utilisateur a modifier", 
                "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Fonctionnalite de modification d'utilisateur a implementer", 
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelectedUser() {
        if (userTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez selectionner un utilisateur a supprimer", 
                "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Etes-vous sur de vouloir supprimer cet utilisateur ?", 
            "Confirmation", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Fonctionnalite de suppression d'utilisateur a implementer", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}