package controller;

import view.*;

import javax.swing.*;
import java.sql.SQLException;

public class NavigationController {
    private static JFrame currentView;  // Garde une référence à la vue active

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


    public static void showSalleGestion() {
        closeCurrentView();
        currentView = new SalleGestionView();
    }

    private static void closeCurrentView() {
        if (currentView != null) {
            currentView.dispose();
        }
    }
}