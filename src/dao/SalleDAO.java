package dao;

import model.Salle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalleDAO {
    private final Connection connection;

    public SalleDAO() throws SQLException {
        this.connection =DatabaseManager.getConnection();
    }

    public List<Salle> getAll() {
        List<Salle> salles = new ArrayList<>();
        String sql = "SELECT * FROM salle";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                salles.add(new Salle(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("capacite"),
                        rs.getString("type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salles;
    }

    public boolean insert(Salle salle) {
        String sql = "INSERT INTO salle (nom, capacite, type) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, salle.getNom());
            stmt.setInt(2, salle.getCapacite());
            stmt.setString(3, salle.getType());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    // Ajoute ces méthodes dans ta classe SalleDAO

    public boolean update(Salle salle) {
        String sql = "UPDATE salle SET nom = ?, capacite = ?, type = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, salle.getNom());
            stmt.setInt(2, salle.getCapacite());
            stmt.setString(3, salle.getType());
            stmt.setInt(4, salle.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM salle WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

}
