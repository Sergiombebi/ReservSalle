package view;

import dao.ReservationDAO;
import model.Reservation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservationValidationView extends JFrame {
    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private JLabel statsLabel;
    private JTextArea commentaireArea;
    private ReservationDAO reservationDAO;
    
    // Couleurs professionnelles
    private static final Color PRIMARY_COLOR = new Color(52, 73, 94);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color INFO_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);

    public ReservationValidationView() {
        try {
            reservationDAO = new ReservationDAO();
            initializeFrame();
            setupComponents();
            loadReservations();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur de connexion a la base de donnees: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeFrame() {
        setTitle("Validation des Reservations - Interface Responsable");
        setSize(1200, 700);
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
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Contenu principal
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.7);
        splitPane.setBackground(BACKGROUND_COLOR);
        splitPane.setBorder(null);

        // Panel gauche : Liste des réservations
        splitPane.setLeftComponent(createReservationListPanel());

        // Panel droit : Détails et actions
        splitPane.setRightComponent(createDetailsPanel());

        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Footer
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Titre principal
        JLabel titleLabel = new JLabel("Validation des Reservations");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        // Sous-titre
        JLabel subtitleLabel = new JLabel("Gerez et validez les demandes de reservation");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(189, 195, 199));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);

        // Statistiques
        statsLabel = new JLabel("Chargement des donnees...");
        statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.setBackground(PRIMARY_COLOR);
        
        JButton refreshBtn = createStyledButton("Actualiser", INFO_COLOR);
        refreshBtn.addActionListener(e -> loadReservations());
        
        JButton closeBtn = createStyledButton("Fermer", DANGER_COLOR);
        closeBtn.addActionListener(e -> dispose());

        statsPanel.add(statsLabel);
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(refreshBtn);
        statsPanel.add(closeBtn);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(statsPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createReservationListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Titre de la section
        JLabel sectionTitle = new JLabel("Reservations en attente de validation");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(PRIMARY_COLOR);
        sectionTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Configuration du tableau
        String[] columns = {"ID", "Utilisateur", "Salle", "Date", "Heure", "Durée", "État"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        reservationTable = new JTable(tableModel);
        setupTable();

        // Ecouteur de selection
        reservationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailsPanel();
            }
        });

        JScrollPane scrollPane = new JScrollPane(reservationTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        listPanel.add(sectionTitle, BorderLayout.NORTH);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        return listPanel;
    }

    private void setupTable() {
        // Apparence du tableau
        reservationTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reservationTable.setRowHeight(30);
        reservationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationTable.setSelectionBackground(new Color(230, 245, 255));
        reservationTable.setSelectionForeground(Color.BLACK);
        reservationTable.setGridColor(new Color(230, 230, 230));
        reservationTable.setShowGrid(true);

        // En-tetes
        reservationTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        reservationTable.getTableHeader().setBackground(PRIMARY_COLOR);
        reservationTable.getTableHeader().setForeground(Color.WHITE);
        reservationTable.getTableHeader().setPreferredSize(new Dimension(0, 35));

        // Largeurs des colonnes
        int[] columnWidths = {50, 150, 120, 100, 100, 80, 100};
        for (int i = 0; i < columnWidths.length; i++) {
            reservationTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Renderer personnalisé pour la colonne État
        reservationTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        // Effet hover
        reservationTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = reservationTable.rowAtPoint(e.getPoint());
                reservationTable.setRowSelectionInterval(row, row);
            }
        });
    }

    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Titre
        JLabel detailsTitle = new JLabel("Details de la reservation");
        detailsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        detailsTitle.setForeground(PRIMARY_COLOR);
        detailsTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Zone de détails
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Informations de la réservation (sera rempli dynamiquement)
        JLabel infoLabel = new JLabel("<html><div style='padding: 10px;'>" +
            "<h3>Sélectionnez une réservation</h3>" +
            "<p>Les détails s'afficheront ici</p>" +
            "</div></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(108, 117, 125));

        // Zone de commentaire
        JLabel commentLabel = new JLabel("💬 Commentaire de validation:");
        commentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        commentLabel.setForeground(PRIMARY_COLOR);

        commentaireArea = new JTextArea(4, 20);
        commentaireArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        commentaireArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(5, 5, 5, 5)
        ));
        commentaireArea.setBackground(new Color(248, 249, 250));
        // Placeholder simulé avec tooltip et text initial
        commentaireArea.setToolTipText("Ajoutez un commentaire (optionnel)...");

        JScrollPane commentScrollPane = new JScrollPane(commentaireArea);
        commentScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton validerBtn = createStyledButton("✅ Valider", SUCCESS_COLOR);
        validerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        validerBtn.addActionListener(e -> validerReservation());

        JButton rejeterBtn = createStyledButton("Rejeter", DANGER_COLOR);
        rejeterBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rejeterBtn.addActionListener(e -> rejeterReservation());

        JButton detailsBtn = createStyledButton("Plus de details", INFO_COLOR);
        detailsBtn.addActionListener(e -> afficherDetailsComplets());

        buttonPanel.add(validerBtn);
        buttonPanel.add(rejeterBtn);
        buttonPanel.add(detailsBtn);

        // Assemblage
        detailsPanel.add(detailsTitle, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(infoLabel, BorderLayout.NORTH);
        
        JPanel commentPanel = new JPanel(new BorderLayout(0, 5));
        commentPanel.setBackground(Color.WHITE);
        commentPanel.add(commentLabel, BorderLayout.NORTH);
        commentPanel.add(commentScrollPane, BorderLayout.CENTER);
        
        centerPanel.add(commentPanel, BorderLayout.CENTER);
        detailsPanel.add(centerPanel, BorderLayout.CENTER);
        detailsPanel.add(buttonPanel, BorderLayout.SOUTH);

        return detailsPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(248, 249, 250));
        footerPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel footerLabel = new JLabel("Astuce: Double-cliquez sur une reservation pour voir tous les details");
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        footerLabel.setForeground(new Color(108, 117, 125));

        JLabel copyrightLabel = new JLabel("2025 Systeme de Reservation de Salles");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copyrightLabel.setForeground(new Color(108, 117, 125));

        footerPanel.add(footerLabel, BorderLayout.WEST);
        footerPanel.add(copyrightLabel, BorderLayout.EAST);

        return footerPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void loadReservations() {
        try {
            List<Reservation> reservations = reservationDAO.getReservationsEnAttente();
            tableModel.setRowCount(0);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            for (Reservation reservation : reservations) {
                String duree = calculerDuree(reservation);
                String horaire = reservation.getHeureDebut().format(timeFormatter) + 
                               " - " + reservation.getHeureFin().format(timeFormatter);

                tableModel.addRow(new Object[]{
                    reservation.getId(),
                    reservation.getnom() != null ? reservation.getnom() : "Utilisateur inconnu",
                    reservation.getNomSalle() != null ? reservation.getNomSalle() : "Salle inconnue",
                    reservation.getDate().format(dateFormatter),
                    horaire,
                    duree,
                    "En attente"
                });
            }

            // Mettre a jour les statistiques
            updateStatsLabel(reservations.size());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des reservations: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String calculerDuree(Reservation reservation) {
        long minutes = java.time.Duration.between(
            reservation.getHeureDebut(), 
            reservation.getHeureFin()
        ).toMinutes();
        
        if (minutes < 60) {
            return minutes + " min";
        } else {
            long heures = minutes / 60;
            long minutesRestantes = minutes % 60;
            return heures + "h" + (minutesRestantes > 0 ? String.format("%02d", minutesRestantes) : "");
        }
    }

    private void updateStatsLabel(int count) {
        if (count == 0) {
            statsLabel.setText("Aucune reservation en attente");
        } else {
            statsLabel.setText(count + " reservation(s) en attente");
        }
    }

    private void updateDetailsPanel() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // Recuperer l'ID de la reservation selectionnee
        String id = tableModel.getValueAt(selectedRow, 0).toString();
        System.out.println("Reservation selectionnee: " + id);
    }

    private void validerReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez selectionner une reservation a valider.",
                "Aucune selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Etes-vous sur de vouloir valider cette reservation ?",
            "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = reservationDAO.validerReservation(id);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Reservation validee avec succes !",
                        "Succes", JOptionPane.INFORMATION_MESSAGE);
                    loadReservations(); // Recharger la liste
                    commentaireArea.setText(""); // Vider le commentaire
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la validation de la reservation.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur de base de donnees: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void rejeterReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez selectionner une reservation a rejeter.",
                "Aucune selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Etes-vous sur de vouloir rejeter cette reservation ?",
            "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = reservationDAO.refuserReservation(id);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Reservation rejetee avec succes !",
                        "Succes", JOptionPane.INFORMATION_MESSAGE);
                    loadReservations(); // Recharger la liste
                    commentaireArea.setText(""); // Vider le commentaire
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors du rejet de la reservation.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur de base de donnees: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void afficherDetailsComplets() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez selectionner une reservation pour voir les details.",
                "Aucune selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Afficher une boite de dialogue avec tous les details
        String details = "Details complets de la reservation:\n\n";
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            details += tableModel.getColumnName(i) + ": " + 
                      tableModel.getValueAt(selectedRow, i) + "\n";
        }

        JTextArea textArea = new JTextArea(details);
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, 
            "Details de la reservation", JOptionPane.INFORMATION_MESSAGE);
    }

    // Renderer pour la colonne Etat
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                     boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String status = value.toString().toLowerCase();
                
                switch (status) {
                    case "en attente":
                        setBackground(isSelected ? table.getSelectionBackground() : WARNING_COLOR);
                        setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
                        break;
                    case "validee":
                        setBackground(isSelected ? table.getSelectionBackground() : SUCCESS_COLOR);
                        setForeground(isSelected ? table.getSelectionForeground() : Color.WHITE);
                        break;
                    case "rejetee":
                        setBackground(isSelected ? table.getSelectionBackground() : DANGER_COLOR);
                        setForeground(isSelected ? table.getSelectionForeground() : Color.WHITE);
                        break;
                    default:
                        setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                        setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
                }
            }
            
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            
            return this;
        }
    }
}