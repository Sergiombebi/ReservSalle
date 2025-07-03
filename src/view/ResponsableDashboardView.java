package view;

import dao.ReservationDAO;
import dao.RessourceDAO;
import model.Reservation;
import model.Ressource;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ResponsableDashboardView extends JFrame {

    private DefaultTableModel reservationModel;
    private DefaultTableModel ressourceModel;
    private JTable reservationTable;
    private JTable ressourceTable;
    private JLabel statsLabel;
    private JLabel ressourceStatsLabel;

    // Couleurs professionnelles
    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color DANGER_COLOR = new Color(244, 67, 54);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color CARD_COLOR = Color.WHITE;

    public ResponsableDashboardView() {
        initializeFrame();
        setupUI();
        loadData();
    }

    private void initializeFrame() {
        setTitle("Tableau de Bord Responsable - Gestion des Réservations");
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void setupUI() {
        // Header avec titre et informations
        add(createHeader(), BorderLayout.NORTH);

        // Contenu principal avec onglets
        add(createMainContent(), BorderLayout.CENTER);

        // Footer avec informations système
        add(createFooter(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Titre principal
        JLabel titleLabel = new JLabel("Tableau de Bord Responsable");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setIcon(new ImageIcon(createColoredIcon("👤", Color.WHITE)));

        // Informations utilisateur
        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userInfo.setBackground(PRIMARY_COLOR);

        JLabel userLabel = new JLabel("Connecté en tant que: Responsable");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(Color.WHITE);

        JLabel dateLabel = new JLabel("Date: " + LocalDate.now().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(Color.WHITE);

        userInfo.add(userLabel);
        userInfo.add(Box.createHorizontalStrut(15));
        userInfo.add(dateLabel);
        JButton logoutBtn = createStyledButton("🚪 Déconnexion", DANGER_COLOR);
        logoutBtn.addActionListener(e -> {
            dispose(); // ferme cette fenêtre
            controller.NavigationController.showConnection(); // redirige vers ConnexionView
        });
        userInfo.add(Box.createHorizontalStrut(15));
        userInfo.add(logoutBtn);


        header.add(titleLabel, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);

        return header;
    }

    private JComponent createMainContent() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabs.setBackground(BACKGROUND_COLOR);

        // Onglet Réservations avec icône
        tabs.addTab("📋 Réservations à Valider", createReservationPanel());

        // Onglet Ressources avec icône
        tabs.addTab("🛠 Gestion des Ressources", createRessourcePanel());

        // Onglet Statistiques
        //tabs.addTab("📊 Statistiques", createStatisticsPanel());

        return tabs;
    }

    private JPanel createReservationPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel supérieur avec statistiques
        JPanel topPanel = createReservationStatsPanel();

        // Panel central avec tableau
        JPanel centerPanel = createReservationTablePanel();

        // Panel inférieur avec actions
        JPanel bottomPanel = createReservationActionsPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createReservationStatsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(BACKGROUND_COLOR);

        statsLabel = new JLabel("Réservations en attente: 0");
        statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statsLabel.setForeground(WARNING_COLOR.darker());

        JButton refreshBtn = createStyledButton("🔄 Actualiser", PRIMARY_COLOR);
        refreshBtn.addActionListener(e -> loadReservationData());

        panel.add(statsLabel);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(refreshBtn);

        return panel;
    }

    private JPanel createReservationTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Configuration du tableau
        String[] columns = {"ID", "Utilisateur", "Salle/Ressource", "Date", "Heure Début", "Heure Fin", "État"};
        reservationModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reservationTable = new JTable(reservationModel);
        setupTableAppearance(reservationTable);

        // Renderer personnalisé pour les états
        reservationTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(reservationTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReservationActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(BACKGROUND_COLOR);

        JButton validerBtn = createStyledButton("✅ Valider la Réservation", SUCCESS_COLOR);
        JButton refuserBtn = createStyledButton("❌ Refuser la Réservation", DANGER_COLOR);
        JButton detailsBtn = createStyledButton("ℹ️ Voir Détails", PRIMARY_COLOR);

        validerBtn.addActionListener(e -> gererReservation(reservationTable, true));
        refuserBtn.addActionListener(e -> gererReservation(reservationTable, false));
        detailsBtn.addActionListener(e -> afficherDetailsReservation());

        panel.add(validerBtn);
        panel.add(refuserBtn);
        panel.add(detailsBtn);

        return panel;
    }

    private JPanel createRessourcePanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel supérieur avec statistiques des ressources
        JPanel topPanel = createRessourceStatsPanel();

        // Panel central avec tableau des ressources
        JPanel centerPanel = createRessourceTablePanel();

        // Panel inférieur avec actions
        JPanel bottomPanel = createRessourceActionsPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createRessourceStatsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(BACKGROUND_COLOR);

        ressourceStatsLabel = new JLabel("Ressources: 0 | Disponibles: 0 | En panne: 0");
        ressourceStatsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ressourceStatsLabel.setForeground(PRIMARY_COLOR.darker());

        JButton refreshRessourcesBtn = createStyledButton("🔄 Actualiser", PRIMARY_COLOR);
        refreshRessourcesBtn.addActionListener(e -> loadRessourceData());

        panel.add(ressourceStatsLabel);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(refreshRessourcesBtn);

        return panel;
    }

    private JPanel createRessourceTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        String[] columns = {"ID", "Nom de la Ressource", "Quantité", "État", "Dernière Maj"};
        ressourceModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ressourceTable = new JTable(ressourceModel);
        setupTableAppearance(ressourceTable);

        // Renderer pour les états des ressources
        ressourceTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(ressourceTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRessourceActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(BACKGROUND_COLOR);

        JButton toggleEtatBtn = createStyledButton("🔁 Changer l'État", WARNING_COLOR);
        JButton ajouterBtn = createStyledButton("➕ Ajouter Ressource", SUCCESS_COLOR);
        JButton modifierBtn = createStyledButton("✏️ Modifier", PRIMARY_COLOR);

        toggleEtatBtn.addActionListener(e -> toggleRessourceEtat());
        ajouterBtn.addActionListener(e -> ajouterRessource());
        modifierBtn.addActionListener(e -> modifierRessource());

        panel.add(toggleEtatBtn);
        panel.add(ajouterBtn);
        panel.add(modifierBtn);

        return panel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Cartes statistiques
        panel.add(createStatCard("Réservations du Jour", "12", "📅", SUCCESS_COLOR));
        panel.add(createStatCard("Ressources Disponibles", "8", "✅", PRIMARY_COLOR));
        panel.add(createStatCard("En Attente", "3", "⏳", WARNING_COLOR));
        panel.add(createStatCard("Problèmes", "1", "⚠️", DANGER_COLOR));

        return panel;
    }

    private JPanel createStatCard(String title, String value, String icon, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        iconLabel.setForeground(color);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.DARK_GRAY);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(valueLabel);
        textPanel.add(titleLabel);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(Color.LIGHT_GRAY);
        footer.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel footerLabel = new JLabel("Système de Gestion des Réservations - Version 2.0");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(Color.DARK_GRAY);

        footer.add(footerLabel);
        return footer;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void setupTableAppearance(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(PRIMARY_COLOR.brighter());
        table.setSelectionForeground(Color.WHITE);
//        table.setAlternatingRowColor(new Color(248, 248, 248));
    }

    // Renderer personnalisé pour les états
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                String status = value.toString().toLowerCase();
                switch (status) {
                    case "en_attente":
                    case "en attente":
                        c.setBackground(new Color(255, 248, 225));
                        c.setForeground(WARNING_COLOR.darker());
                        break;
                    case "validee":
                    case "validée":
                    case "disponible":
                        c.setBackground(new Color(232, 245, 233));
                        c.setForeground(SUCCESS_COLOR.darker());
                        break;
                    case "refusee":
                    case "refusée":
                    case "en_panne":
                        c.setBackground(new Color(255, 235, 238));
                        c.setForeground(DANGER_COLOR.darker());
                        break;
                    default:
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                }
            }

            return c;
        }
    }

    // Méthodes de gestion des données
    private void loadData() {
        loadReservationData();
        loadRessourceData();
    }

    private void loadReservationData() {
        try {
            reservationModel.setRowCount(0);
            List<Reservation> reservations = new ReservationDAO().getReservationsEnAttente();

            for (Reservation r : reservations) {
                reservationModel.addRow(new Object[]{
                        r.getId(),
                        r.getnom(),
                        r.getNomSalle(),
                        r.getDate(),
                        r.getHeureDebut(),
                        r.getHeureFin(),
                        r.getEtat()
                });
            }

            statsLabel.setText("Réservations en attente: " + reservations.size());

        } catch (SQLException e) {
            showErrorDialog("Erreur de chargement des réservations", e.getMessage());
        }
    }

    private void loadRessourceData() {
        try {
            ressourceModel.setRowCount(0);
            List<Ressource> ressources = new RessourceDAO().getAll();

            int disponibles = 0, enPanne = 0;

            for (Ressource r : ressources) {
                ressourceModel.addRow(new Object[]{
                        r.getId(),
                        r.getNom(),
                        r.getQuantite(),
                        r.getEtat(),
                        LocalDate.now().toString() // Placeholder pour dernière mise à jour
                });

                if ("disponible".equals(r.getEtat())) disponibles++;
                else if ("en_panne".equals(r.getEtat())) enPanne++;
            }

            ressourceStatsLabel.setText(String.format(
                    "Ressources: %d | Disponibles: %d | En panne: %d",
                    ressources.size(), disponibles, enPanne
            ));

        } catch (SQLException e) {
            showErrorDialog("Erreur de chargement des ressources", e.getMessage());
        }
    }

    // Méthodes d'actions
    private void gererReservation(JTable table, boolean valider) {
        int row = table.getSelectedRow();
        if (row == -1) {
            showWarningDialog("Veuillez sélectionner une réservation.");
            return;
        }

        int idReservation = (int) reservationModel.getValueAt(row, 0);
        String action = valider ? "valider" : "refuser";

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir " + action + " cette réservation ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean result = valider
                        ? new ReservationDAO().validerReservation(idReservation)
                        : new ReservationDAO().refuserReservation(idReservation);

                if (result) {
                    reservationModel.removeRow(row);
                    showSuccessDialog("Réservation " + (valider ? "validée" : "refusée") + " avec succès.");
                    loadReservationData(); // Actualiser les statistiques
                }
            } catch (SQLException e) {
                showErrorDialog("Erreur lors de la " + action, e.getMessage());
            }
        }
    }

    private void toggleRessourceEtat() {
        int row = ressourceTable.getSelectedRow();
        if (row == -1) {
            showWarningDialog("Veuillez sélectionner une ressource.");
            return;
        }

        int id = (int) ressourceModel.getValueAt(row, 0);
        String etat = ressourceModel.getValueAt(row, 3).toString();
        String nouvelEtat = etat.equals("disponible") ? "en_panne" : "disponible";

        try {
            boolean ok = new RessourceDAO().changerEtatRessource(id, nouvelEtat);
            if (ok) {
                ressourceModel.setValueAt(nouvelEtat, row, 3);
                ressourceModel.setValueAt(LocalDate.now().toString(), row, 4);
                showSuccessDialog("État de la ressource mis à jour !");
                loadRessourceData(); // Actualiser les statistiques
            }
        } catch (SQLException ex) {
            showErrorDialog("Erreur lors du changement d'état", ex.getMessage());
        }
    }

    private void afficherDetailsReservation() {
        int row = reservationTable.getSelectedRow();
        if (row == -1) {
            showWarningDialog("Veuillez sélectionner une réservation.");
            return;
        }

        // Créer un dialog avec les détails de la réservation
        StringBuilder details = new StringBuilder();
        details.append("Détails de la réservation:\n\n");
        details.append("ID: ").append(reservationModel.getValueAt(row, 0)).append("\n");
        details.append("Utilisateur: ").append(reservationModel.getValueAt(row, 1)).append("\n");
        details.append("Salle/Ressource: ").append(reservationModel.getValueAt(row, 2)).append("\n");
        details.append("Date: ").append(reservationModel.getValueAt(row, 3)).append("\n");
        details.append("Heure: ").append(reservationModel.getValueAt(row, 4))
                .append(" - ").append(reservationModel.getValueAt(row, 5)).append("\n");
        details.append("État: ").append(reservationModel.getValueAt(row, 6)).append("\n");

        JOptionPane.showMessageDialog(this, details.toString(),
                "Détails de la Réservation", JOptionPane.INFORMATION_MESSAGE);
    }

    private void ajouterRessource() {
        // Placeholder pour l'ajout de ressource
        showInfoDialog("Fonctionnalité d'ajout de ressource à implémenter.");
    }

    private void modifierRessource() {
        int row = ressourceTable.getSelectedRow();
        if (row == -1) {
            showWarningDialog("Veuillez sélectionner une ressource à modifier.");
            return;
        }

        // Placeholder pour la modification
        showInfoDialog("Fonctionnalité de modification de ressource à implémenter.");
    }

    // Méthodes utilitaires pour les dialogues
    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Attention", JOptionPane.WARNING_MESSAGE);
    }

    private void showInfoDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private Image createColoredIcon(String text, Color color) {
        // Méthode simple pour créer une icône colorée
        // Dans un vrai projet, utiliser des vraies icônes
        return new java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_ARGB);
    }
}