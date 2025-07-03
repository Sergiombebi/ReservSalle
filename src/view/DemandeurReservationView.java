package view;

import dao.ReservationDAO;
import dao.RessourceDAO;
import dao.SalleDAO;
import dao.UtilisateurDAO;
import model.Reservation;
import model.Ressource;
import model.Salle;
import model.Utilisateur;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class DemandeurReservationView extends JFrame {
    private JComboBox<Salle> salleCombo;
    private JTextField dateField, debutField, finField;
    private Map<Ressource, JSpinner> ressourceQuantites = new HashMap<>();
    private JTable table;
    private DefaultTableModel model;
    private final int utilisateurId;

    // Couleurs du thème
    private final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color ACCENT_COLOR = new Color(46, 204, 113);
    private final Color BACKGROUND_COLOR = new Color(245, 246, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color LIGHT_GRAY = new Color(236, 240, 241);

    public DemandeurReservationView(int utilisateurId) throws SQLException {
        this.utilisateurId = utilisateurId;
        initializeFrame();
        setupComponents();
        loadReservations();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Système de Réservation - Espace Demandeur");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Icône de l'application
        try {
            setIconImage(createIconImage());
        } catch (Exception e) {
            // Ignorer si l'icône ne peut pas être créée
        }
    }

    private void setupComponents() throws SQLException {
        // Header
        add(createHeader(), BorderLayout.NORTH);

        // Contenu principal
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        mainPanel.add(createReservationForm(), BorderLayout.NORTH);
        mainPanel.add(createReservationTable(), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeader() throws SQLException {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Titre principal
        JLabel titleLabel = new JLabel("Gestion des Réservations");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        // Sous-titre
        JLabel subtitleLabel = new JLabel("Réservez vos salles et ressources facilement");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(189, 195, 199));

        // Panneau de titres
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);

        // Icône utilisateur
        Utilisateur user = new ReservationDAO().getUtilisateurParId(utilisateurId);
        String nom = (user != null) ? user.getNom() : "inconnu";
        JLabel userIcon = new JLabel("👤 Utilisateur : " + nom);
        JButton logoutButton = createStyledButton("Déconnexion", new Color(231, 76, 60));
        logoutButton.setPreferredSize(new Dimension(130, 30));
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.addActionListener(e -> {
            dispose(); // Fermer la vue actuelle
            new ConnexionView().setVisible(true); // Retour à la vue de connexion
        });

        userIcon.setFont(new Font("Segoe UI", Font.BOLD, 16));

        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userIcon.setForeground(Color.WHITE);
        userIcon.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(titlePanel, BorderLayout.WEST);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(PRIMARY_COLOR);
        rightPanel.add(userIcon);
        rightPanel.add(Box.createHorizontalStrut(15));
        rightPanel.add(logoutButton);
        header.add(rightPanel, BorderLayout.EAST);


        return header;
    }

    private JPanel createReservationForm() throws SQLException {
        JPanel formCard = createCard();
        formCard.setLayout(new BorderLayout());

        // Titre de la section
        JLabel sectionTitle = new JLabel("📅 Nouvelle Réservation");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(PRIMARY_COLOR);
        sectionTitle.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Formulaire principal
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ligne 1: Salle et Date
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createStyledLabel("🏢 Salle:"), gbc);

        gbc.gridx = 1;
        salleCombo = createStyledComboBox();
        formPanel.add(salleCombo, gbc);

        gbc.gridx = 2;
        formPanel.add(createStyledLabel("📅 Date:"), gbc);

        gbc.gridx = 3;
        dateField = createStyledTextField("2025-07-01");
        formPanel.add(dateField, gbc);

        // Ligne 2: Horaires
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createStyledLabel("🕐 Début:"), gbc);

        gbc.gridx = 1;
        debutField = createStyledTextField("08:00");
        formPanel.add(debutField, gbc);

        gbc.gridx = 2;
        formPanel.add(createStyledLabel("🕑 Fin:"), gbc);

        gbc.gridx = 3;
        finField = createStyledTextField("10:00");
        formPanel.add(finField, gbc);

        // Bouton de réservation
        gbc.gridx = 4; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        JButton reserverBtn = createStyledButton("Réserver", ACCENT_COLOR);
        reserverBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        reserverBtn.addActionListener(e -> effectuerReservation());

        JButton annulerBtn = createStyledButton("❌ Annuler", new Color(231, 76, 60));
        annulerBtn.addActionListener(e -> {
            try {
                annulerReservation();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton modifierBtn = createStyledButton("✏️ Modifier", new Color(241, 196, 15));
        modifierBtn.addActionListener(e -> modifierReservation());

// Créer le panneau des boutons avec 3 lignes (pas 2 !)
        JPanel boutonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        boutonPanel.setBackground(CARD_COLOR);
        boutonPanel.add(reserverBtn);
        boutonPanel.add(annulerBtn);
        boutonPanel.add(modifierBtn);

// Ajouter le panneau de boutons au formulaire
        formPanel.add(boutonPanel, gbc);

        // Charger les salles
        List<Salle> salles = new SalleDAO().getAll();
        for (Salle s : salles) {
            salleCombo.addItem(s);
        }

        // Section des ressources
        JPanel resourcesPanel = createResourcesPanel();

        formCard.add(sectionTitle, BorderLayout.NORTH);
        formCard.add(formPanel, BorderLayout.CENTER);
        formCard.add(resourcesPanel, BorderLayout.SOUTH);

        return formCard;
    }

    private void modifierReservation() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showStyledMessage("Veuillez sélectionner une réservation à modifier.", "Alerte", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String etat = model.getValueAt(selectedRow, 4).toString().toLowerCase();
        if (!etat.equals("en_attente")) {
            showStyledMessage("Seules les réservations en attente peuvent être modifiées.", "Interdit", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Pré-remplir les champs
        salleCombo.setSelectedItem(obtenirSalleParNom(model.getValueAt(selectedRow, 0).toString()));
        dateField.setText(model.getValueAt(selectedRow, 1).toString());
        debutField.setText(model.getValueAt(selectedRow, 2).toString());
        finField.setText(model.getValueAt(selectedRow, 3).toString());

        // Supprimer l'ancienne réservation (temporairement)
        String salleNom = model.getValueAt(selectedRow, 0).toString();
        String date = model.getValueAt(selectedRow, 1).toString();
        String debut = model.getValueAt(selectedRow, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Souhaitez-vous réellement modifier cette réservation ?\nL'ancienne sera supprimée.",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ReservationDAO dao = new ReservationDAO();
                boolean ok = dao.supprimerReservation(utilisateurId, salleNom, date, debut);
                if (ok) {
                    showStyledMessage("Ancienne réservation supprimée. Modifiez les champs et cliquez sur Réserver.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    loadReservations();
                }
            } catch (SQLException e) {
                showStyledMessage("Erreur SQL : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Salle obtenirSalleParNom(String nom) {
        for (int i = 0; i < salleCombo.getItemCount(); i++) {
            Salle s = salleCombo.getItemAt(i);
            if (s.getNom().equalsIgnoreCase(nom)) {
                return s;
            }
        }
        return null;
    }



    private JPanel createResourcesPanel() throws SQLException {
        JPanel resourcesCard = new JPanel(new BorderLayout());
        resourcesCard.setBackground(CARD_COLOR);
        resourcesCard.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel resourcesTitle = new JLabel("🔧 Ressources Disponibles");
        resourcesTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resourcesTitle.setForeground(PRIMARY_COLOR);
        resourcesTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel resourcesGrid = new JPanel();
        resourcesGrid.setLayout(new GridLayout(0, 3, 15, 10));
        resourcesGrid.setBackground(CARD_COLOR);

        List<Ressource> ressources = new RessourceDAO().getAll();
        for (Ressource r : ressources) {
            JPanel resourceItem = createResourceItem(r);
            resourcesGrid.add(resourceItem);
        }

        resourcesCard.add(resourcesTitle, BorderLayout.NORTH);
        resourcesCard.add(resourcesGrid, BorderLayout.CENTER);

        return resourcesCard;
    }

    private JPanel createResourceItem(Ressource r) {
        JPanel item = new JPanel(new BorderLayout(10, 5));
        item.setBackground(LIGHT_GRAY);
        item.setBorder(new EmptyBorder(15, 15, 15, 15));
        item.setPreferredSize(new Dimension(200, 80));

        // Nom de la ressource
        JLabel nameLabel = new JLabel(r.getNom());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(TEXT_COLOR);

        // Quantité disponible
        JLabel availableLabel = new JLabel("Disponible: " + r.getQuantite());
        availableLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        availableLabel.setForeground(new Color(127, 140, 141));

        // Spinner pour la quantité
        JSpinner quantiteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, r.getQuantite(), 1));
        quantiteSpinner.setPreferredSize(new Dimension(80, 25));
        ressourceQuantites.put(r, quantiteSpinner);

        // Organisation
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(LIGHT_GRAY);
        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(availableLabel, BorderLayout.CENTER);

        item.add(infoPanel, BorderLayout.CENTER);
        item.add(quantiteSpinner, BorderLayout.EAST);

        return item;
    }

    private JScrollPane createReservationTable() throws SQLException {
        // Titre de la section
        JPanel tableSection = new JPanel(new BorderLayout());
        tableSection.setBackground(BACKGROUND_COLOR);

        //filtre

        JLabel tableTitle = new JLabel("📋 Mes Réservations");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(PRIMARY_COLOR);
        tableTitle.setBorder(new EmptyBorder(20, 0, 15, 0));

        // Table
        String[] cols = {"🏢 Salle", "📅 Date", "🕐 Début", "🕑 Fin", "📊 État"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        customizeTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableSection.add(tableTitle, BorderLayout.NORTH);
        tableSection.add(scrollPane, BorderLayout.CENTER);

        return scrollPane;
    }

    private void customizeTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(35);
        table.setGridColor(LIGHT_GRAY);
        table.setSelectionBackground(new Color(52, 152, 219, 50));
        table.setSelectionForeground(TEXT_COLOR);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Header personnalisé
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        // Renderer personnalisé pour les états
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String etat = value.toString();
                    switch (etat.toLowerCase()) {
                        case "en_attente":
                            c.setBackground(new Color(255, 193, 7, 50));
                            setText("⏳ En attente");
                            break;
                        case "approuve":
                            c.setBackground(new Color(40, 167, 69, 50));
                            setText("✅ Approuvé");
                            break;
                        case "refuse":
                            c.setBackground(new Color(220, 53, 69, 50));
                            setText("❌ Refusé");
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                            setText(etat);
                    }
                }

                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        // Effet hover
        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != -1) {
                    table.setRowSelectionInterval(row, row);
                }
            }
        });
    }

    // Méthodes utilitaires pour créer des composants stylisés
    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                new EmptyBorder(25, 25, 25, 25)
        ));
        return card;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createStyledTextField(String defaultText) {
        JTextField field = new JTextField(defaultText);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setPreferredSize(new Dimension(120, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JComboBox<Salle> createStyledComboBox() {
        JComboBox<Salle> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setPreferredSize(new Dimension(150, 30));
        combo.setBackground(Color.WHITE);
        return combo;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private Image createIconImage() {
        // Créer une icône simple
        int size = 32;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(PRIMARY_COLOR);
        g2d.fillRect(0, 0, size, size);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2d.drawString("R", 10, 22);
        g2d.dispose();
        return img;
    }

    // Méthodes fonctionnelles conservées
    private void loadReservations() {
        try {
            model.setRowCount(0);
            List<Reservation> reservations = new ReservationDAO().getReservationsParUtilisateur(utilisateurId);
            for (Reservation r : reservations) {
                model.addRow(new Object[]{
                        r.getNomSalle(),
                        r.getDate(),
                        r.getHeureDebut(),
                        r.getHeureFin(),
                        r.getEtat()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des réservations: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void effectuerReservation() {
        Salle salle = (Salle) salleCombo.getSelectedItem();
        if (salle == null) {
            showStyledMessage("Veuillez sélectionner une salle.", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            LocalTime debut = LocalTime.parse(debutField.getText().trim());
            LocalTime fin = LocalTime.parse(finField.getText().trim());

            // Vérifier disponibilité
            ReservationDAO dao = new ReservationDAO();
            boolean libre = dao.estCreneauDisponible(salle.getId(), date, debut, fin);
            if (!libre) {
                List<String> dispo = dao.getCreneauxDisponibles(salle.getId(), date);
                StringBuilder message = new StringBuilder("Ce créneau est déjà réservé.\n\nCréneaux encore disponibles pour cette salle :\n");
                if (dispo.isEmpty()) {
                    message.append("Aucun créneau disponible.");
                } else {
                    for (String c : dispo) message.append("• ").append(c).append("\n");
                }
                showStyledMessage(message.toString(), "Conflit d'horaire", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Ajouter la réservation
            Reservation reservation = new Reservation(0, utilisateurId, salle.getId(), date, debut, fin, "en_attente");
            boolean ok = dao.ajouterReservation(reservation);

            if (ok) {
                int idReservation = dao.getLastInsertId();

                // Ajouter les ressources sélectionnées
                for (Map.Entry<Ressource, JSpinner> entry : ressourceQuantites.entrySet()) {
                    int qty = (Integer) entry.getValue().getValue();
                    if (qty > 0) {
                        dao.ajouterReservationRessource(idReservation, entry.getKey().getId(), qty);
                    }
                }

                showStyledMessage("Réservation enregistrée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                loadReservations();

                // Réinitialiser les spinners
                for (JSpinner spinner : ressourceQuantites.values()) {
                    spinner.setValue(0);
                }
            } else {
                showStyledMessage("Échec de la réservation.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            showStyledMessage("Données invalides: " + ex.getMessage(), "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    private void annulerReservation() throws SQLException {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            showStyledMessage("Veuillez sélectionner une réservation à annuler.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String etat = model.getValueAt(selected, 4).toString().toLowerCase();
        if (!etat.contains("attente")) {
            showStyledMessage("Seules les réservations en attente peuvent être annulées.", "Refus", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String salle = model.getValueAt(selected, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment annuler la réservation pour la salle " + salle + " ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            ReservationDAO dao = new ReservationDAO();
            try {
                boolean ok = dao.supprimerReservation(utilisateurId, salle,
                        model.getValueAt(selected, 1).toString(),
                        model.getValueAt(selected, 2).toString());
                if (ok) {
                    showStyledMessage("Réservation annulée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    loadReservations();
                }
            } catch (SQLException e) {
                showStyledMessage("Erreur SQL : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


}