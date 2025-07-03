package dao;

import model.Ressource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RessourceDAO {
    private Connection connection;

    public RessourceDAO() throws SQLException {
        this.connection = DatabaseManager.getConnection();
    }

    public List<Ressource> getAll() throws SQLException {
        List<Ressource> ressources = new ArrayList<>();
        String query = "SELECT * FROM ressource";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ressources.add(new Ressource(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getInt("quantite"),
                        rs.getString("etat")
                ));
            }
        }
        return ressources;
    }

    public boolean insert(Ressource ressource) throws SQLException {
        String query = "INSERT INTO ressource (nom, description, quantite, etat) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, ressource.getNom());
            pstmt.setString(2, ressource.getDescription());
            pstmt.setInt(3, ressource.getQuantite());
            pstmt.setString(4, ressource.getEtat());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ressource.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean update(Ressource ressource) throws SQLException {
        String query = "UPDATE ressource SET nom = ?, description = ?, quantite = ?, etat = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, ressource.getNom());
            pstmt.setString(2, ressource.getDescription());
            pstmt.setInt(3, ressource.getQuantite());
            pstmt.setString(4, ressource.getEtat());
            pstmt.setInt(5, ressource.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM ressource WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
    public boolean changerEtatRessource(int id, String nouvelEtat) throws SQLException {
        String sql = "UPDATE ressource SET etat = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nouvelEtat);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }
}