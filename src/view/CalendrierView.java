package view;

import dao.ReservationDAO;
import dao.SalleDAO;
import model.Reservation;
import model.Salle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class CalendrierView extends JFrame {
    private JComboBox<String> salleCombo;
    private JComboBox<String> moisCombo;
    private JComboBox<Integer> anneeCombo;
    private JTable calendrierTable;
    private DefaultTableModel calendrierModel;
    private JLabel infoLabel;
    private JLabel legendeLabel;
    private Map<String, Color> colorMap;
    
    // Couleurs pour les différents états
    private static final Color COULEUR_VALIDEE = new Color(144, 238, 144);
    private static final Color COULEUR_EN_ATTENTE = new Color(255, 255, 204);
    private static final Color COULEUR_REJETEE = new Color(255, 182, 193);
    private static final Color COULEUR_LIBRE = Color.WHITE;
    private static final Color COULEUR_WEEKEND = new Color(240, 240, 240);

    public CalendrierView() {
        initializeColors();
        setupFrame();
        setupComponents();
        loadInitialData();
        setVisible(true);
    }

    private void initializeColors() {
        colorMap = new HashMap<>();
        colorMap.put("validee", COULEUR_VALIDEE);
        colorMap.put("en_attente", COULEUR_EN_ATTENTE);
        colorMap.put("rejetee", COULEUR_REJETEE);
        colorMap.put("libre", COULEUR_LIBRE);
        colorMap.put("weekend", COULEUR_WEEKEND);
    }

    private void setupFrame() {
        setTitle("📅 Calendrier des Réservations - Vue Mensuelle");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Style Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupComponents() {
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(248, 249, 250));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header avec titre et contrôles
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Calendrier central
        mainPanel.add(createCalendrierPanel(), BorderLayout.CENTER);

        // Panel d'informations et légende
        mainPanel.add(createInfoPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Titre
        JLabel titleLabel = new JLabel("📅 Calendrier des Réservations");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Panel de contrôles
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setBackground(new Color(52, 73, 94));

        // Sélection de la salle
        JLabel salleLabel = new JLabel("Salle:");
        salleLabel.setForeground(Color.WHITE);
        salleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        salleCombo = new JComboBox<>();
        salleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        salleCombo.setPreferredSize(new Dimension(150, 30));
        salleCombo.addActionListener(e -> updateCalendrier());

        // Sélection du mois
        JLabel moisLabel = new JLabel("Mois:");
        moisLabel.setForeground(Color.WHITE);
        moisLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        moisCombo = new JComboBox<>();
        moisCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        moisCombo.setPreferredSize(new Dimension(120, 30));
        populateMoisCombo();
        moisCombo.addActionListener(e -> updateCalendrier());

        // Sélection de l'année
        JLabel anneeLabel = new JLabel("Année:");
        anneeLabel.setForeground(Color.WHITE);
        anneeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        anneeCombo = new JComboBox<>();
        anneeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        anneeCombo.setPreferredSize(new Dimension(80, 30));
        populateAnneeCombo();
        anneeCombo.addActionListener(e -> updateCalendrier());

        // Boutons
        JButton refreshBtn = createStyledButton("🔄 Actualiser", new Color(46, 204, 113));
        refreshBtn.addActionListener(e -> updateCalendrier());

        JButton exportBtn = createStyledButton("📤 Exporter", new Color(52, 152, 219));
        exportBtn.addActionListener(e -> exporterCalendrier());

        JButton fermerBtn = createStyledButton("✖ Fermer", new Color(231, 76, 60));
        fermerBtn.addActionListener(e -> dispose());

        // Ajout des composants
        controlPanel.add(salleLabel);
        controlPanel.add(salleCombo);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(moisLabel);
        controlPanel.add(moisCombo);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(anneeLabel);
        controlPanel.add(anneeCombo);
        controlPanel.add(Box.createHorizontalStrut(15));
        controlPanel.add(refreshBtn);
        controlPanel.add(exportBtn);
        controlPanel.add(fermerBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(controlPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createCalendrierPanel() {
        JPanel calendrierPanel = new JPanel(new BorderLayout());
        calendrierPanel.setBackground(Color.WHITE);
        calendrierPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        // Créer le modèle de table pour le calendrier
        String[] colonnes = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        calendrierModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        calendrierTable = new JTable(calendrierModel);
        setupCalendrierTable();

        JScrollPane scrollPane = new JScrollPane(calendrierTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.WHITE);

        calendrierPanel.add(scrollPane, BorderLayout.CENTER);

        return calendrierPanel;
    }

    private void setupCalendrierTable() {
        calendrierTable.setRowHeight(80);
        calendrierTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        calendrierTable.setGridColor(new Color(189, 195, 199));
        calendrierTable.setShowGrid(true);
        calendrierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Renderer personnalisé pour les cellules
        calendrierTable.setDefaultRenderer(Object.class, new CalendrierCellRenderer());

        // Ajuster la largeur des colonnes
        for (int i = 0; i < calendrierTable.getColumnCount(); i++) {
            TableColumn column = calendrierTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(150);
        }

        // En-têtes stylisés
        calendrierTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        calendrierTable.getTableHeader().setBackground(new Color(52, 73, 94));
        calendrierTable.getTableHeader().setForeground(Color.WHITE);
        calendrierTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(248, 249, 250));
        infoPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Informations générales
        infoLabel = new JLabel("Sélectionnez une salle et une période pour voir le calendrier");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(127, 140, 141));

        // Légende
        JPanel legendePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        legendePanel.setBackground(new Color(248, 249, 250));

        legendeLabel = new JLabel("Légende: ");
        legendeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel valideeLabel = createLegendLabel("Validée", COULEUR_VALIDEE);
        JLabel attenteLabel = createLegendLabel("En attente", COULEUR_EN_ATTENTE);
        JLabel rejeteeLabel = createLegendLabel("Rejetée", COULEUR_REJETEE);
        JLabel libreLabel = createLegendLabel("Libre", COULEUR_LIBRE);

        legendePanel.add(legendeLabel);
        legendePanel.add(valideeLabel);
        legendePanel.add(attenteLabel);
        legendePanel.add(rejeteeLabel);
        legendePanel.add(libreLabel);

        infoPanel.add(infoLabel, BorderLayout.WEST);
        infoPanel.add(legendePanel, BorderLayout.EAST);

        return infoPanel;
    }

    private JLabel createLegendLabel(String text, Color color) {
        JLabel label = new JLabel("  " + text + "  ");
        label.setOpaque(true);
        label.setBackground(color);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        return label;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void populateMoisCombo() {
        String[] mois = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        for (String m : mois) {
            moisCombo.addItem(m);
        }
        moisCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
    }

    private void populateAnneeCombo() {
        int anneeActuelle = LocalDate.now().getYear();
        for (int i = anneeActuelle - 1; i <= anneeActuelle + 2; i++) {
            anneeCombo.addItem(i);
        }
        anneeCombo.setSelectedItem(anneeActuelle);
    }

    private void loadInitialData() {
        try {
            // Charger les salles
            SalleDAO salleDAO = new SalleDAO();
            List<Salle> salles = salleDAO.getAll();
            
            salleCombo.addItem("Toutes les salles");
            for (Salle salle : salles) {
                salleCombo.addItem(salle.getNom());
            }

            if (salleCombo.getItemCount() > 1) {
                salleCombo.setSelectedIndex(1); // Sélectionner la première salle
            }

            updateCalendrier();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des données: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCalendrier() {
        try {
            String salleSelectionnee = (String) salleCombo.getSelectedItem();
            int moisSelectionne = moisCombo.getSelectedIndex() + 1;
            int anneeSelectionnee = (Integer) anneeCombo.getSelectedItem();

            // Vider le modèle
            calendrierModel.setRowCount(0);

            // Créer le calendrier pour le mois
            LocalDate premierJour = LocalDate.of(anneeSelectionnee, moisSelectionne, 1);
            LocalDate dernierJour = premierJour.plusMonths(1).minusDays(1);
            
            // Ajuster pour commencer le lundi
            LocalDate debutCalendrier = premierJour.minusDays(premierJour.getDayOfWeek().getValue() - 1);
            LocalDate finCalendrier = dernierJour.plusDays(7 - dernierJour.getDayOfWeek().getValue());

            // Charger les réservations
            ReservationDAO reservationDAO = new ReservationDAO();
            List<Reservation> reservations = new ArrayList<>();
            
            if (salleSelectionnee != null && !salleSelectionnee.equals("Toutes les salles")) {
                reservations = reservationDAO.getReservationsParSalle(salleSelectionnee);
            } else {
                reservations = reservationDAO.getAllReservations();
            }

            // Créer le calendrier
            LocalDate currentDate = debutCalendrier;
            while (currentDate.isBefore(finCalendrier) || currentDate.isEqual(finCalendrier)) {
                Object[] semaine = new Object[7];
                
                for (int i = 0; i < 7; i++) {
                    semaine[i] = creerCelluleJour(currentDate, reservations, moisSelectionne);
                    currentDate = currentDate.plusDays(1);
                }
                
                calendrierModel.addRow(semaine);
            }

            // Mettre à jour les informations
            updateInfoLabel(salleSelectionnee, moisSelectionne, anneeSelectionnee, reservations.size());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la mise à jour du calendrier: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private CelluleJour creerCelluleJour(LocalDate date, List<Reservation> reservations, int moisCourant) {
        CelluleJour cellule = new CelluleJour(date);
        
        // Vérifier si c'est le mois courant
        cellule.setMoisCourant(date.getMonthValue() == moisCourant);
        
        // Ajouter les réservations pour cette date
        for (Reservation reservation : reservations) {
            if (reservation.getDate().equals(date)) {
                cellule.ajouterReservation(reservation);
            }
        }
        
        return cellule;
    }

    private void updateInfoLabel(String salle, int mois, int annee, int nbReservations) {
        String moisNom = moisCombo.getItemAt(mois - 1);
        String info = String.format("Affichage: %s - %s %d (%d réservations)", 
                                   salle, moisNom, annee, nbReservations);
        infoLabel.setText(info);
    }

    private void exporterCalendrier() {
        // Fonction d'export (à implémenter selon les besoins)
        JOptionPane.showMessageDialog(this, 
            "Fonction d'export à implémenter (PDF, Excel, etc.)",
            "Export", JOptionPane.INFORMATION_MESSAGE);
    }

    // Classe interne pour représenter une cellule du calendrier
    private class CelluleJour {
        private LocalDate date;
        private List<Reservation> reservations;
        private boolean moisCourant;

        public CelluleJour(LocalDate date) {
            this.date = date;
            this.reservations = new ArrayList<>();
            this.moisCourant = true;
        }

        public void ajouterReservation(Reservation reservation) {
            reservations.add(reservation);
        }

        public LocalDate getDate() { return date; }
        public List<Reservation> getReservations() { return reservations; }
        public boolean isMoisCourant() { return moisCourant; }
        public void setMoisCourant(boolean moisCourant) { this.moisCourant = moisCourant; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("<html><div style='padding: 2px;'>");
            sb.append("<b>").append(date.getDayOfMonth()).append("</b><br/>");
            
            if (reservations.size() > 0) {
                for (int i = 0; i < Math.min(reservations.size(), 3); i++) {
                    Reservation r = reservations.get(i);
                    sb.append("<small>").append(r.getHeureDebut()).append(" - ").append(r.getHeureFin()).append("</small><br/>");
                }
                if (reservations.size() > 3) {
                    sb.append("<small>... et ").append(reservations.size() - 3).append(" autres</small>");
                }
            }
            
            sb.append("</div></html>");
            return sb.toString();
        }
    }

    // Renderer personnalisé pour les cellules
    private class CalendrierCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                     boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof CelluleJour) {
                CelluleJour cellule = (CelluleJour) value;
                
                // Couleur de fond selon l'état
                if (!cellule.isMoisCourant()) {
                    setBackground(new Color(245, 245, 245));
                    setForeground(Color.LIGHT_GRAY);
                } else if (cellule.getDate().getDayOfWeek().getValue() >= 6) {
                    setBackground(COULEUR_WEEKEND);
                    setForeground(Color.BLACK);
                } else if (cellule.getReservations().isEmpty()) {
                    setBackground(COULEUR_LIBRE);
                    setForeground(Color.BLACK);
                } else {
                    // Déterminer la couleur selon l'état des réservations
                    boolean hasValidee = cellule.getReservations().stream()
                        .anyMatch(r -> "validee".equals(r.getEtat()));
                    boolean hasAttente = cellule.getReservations().stream()
                        .anyMatch(r -> "en_attente".equals(r.getEtat()));
                    
                    if (hasValidee) {
                        setBackground(COULEUR_VALIDEE);
                    } else if (hasAttente) {
                        setBackground(COULEUR_EN_ATTENTE);
                    } else {
                        setBackground(COULEUR_REJETEE);
                    }
                    setForeground(Color.BLACK);
                }
                
                setText(cellule.toString());
                setVerticalAlignment(SwingConstants.TOP);
                setHorizontalAlignment(SwingConstants.LEFT);
            }
            
            return this;
        }
    }
}