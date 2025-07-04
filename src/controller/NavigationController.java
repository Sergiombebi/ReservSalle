package controller;

import view.*;

import javax.swing.*;
import java.sql.SQLException;

public class NavigationController {
    private static JFrame currentView;  // Garde une reference a la vue active

    public static void showConnection() {
        closeCurrentView();
        currentView = new ConnexionView();
    }

    public static void showAdminAccueil() {
        closeCurrentView();
        try {
            currentView = new AccueilAdminView();
            currentView.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de l’ouverture du tableau de bord administrateur :\n" + e.getMessage(),
                    "Erreur SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void showDemandeurAccueil(int utilisateurId) throws SQLException {
        closeCurrentView();
        // TODO: A restaurer quand les problemes d'encodage seront corriges
        // currentView = new DemandeurReservationView(utilisateurId);
        // currentView.setVisible(true);
        JOptionPane.showMessageDialog(null, "Vue Demandeur temporairement desactivee");
    }
    public static void showResponsableDashboard(int utilisateurId) throws SQLException {
        closeCurrentView();
        // TODO: A restaurer quand les problemes d'encodage seront corriges
        // currentView = new ResponsableDashboardView();
        // currentView.setVisible(true);
        JOptionPane.showMessageDialog(null, "Vue Responsable temporairement desactivee");
    }



    public static void showSalleGestion() {
        closeCurrentView();
        // TODO: A restaurer quand les problemes d'encodage seront corriges
        // currentView = new SalleGestionView();
        JOptionPane.showMessageDialog(null, "Vue Gestion Salle temporairement desactivee");
    }

    private static void closeCurrentView() {
        if (currentView != null) {
            currentView.dispose();
        }
    }
}