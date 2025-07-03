package dao;

import model.Reservation;
import model.Utilisateur;


import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    private final Connection conn = DatabaseManager.getConnection();

    public ReservationDAO() throws SQLException {
    }

    public boolean ajouterReservation(Reservation r) {
        String sql = "INSERT INTO reservation (id_utilisateur, id_salle, date, heure_debut, heure_fin, etat) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, r.getIdUtilisateur());
            stmt.setInt(2, r.getIdSalle());
            stmt.setDate(3, Date.valueOf(r.getDate()));
            stmt.setTime(4, Time.valueOf(r.getHeureDebut()));
            stmt.setTime(5, Time.valueOf(r.getHeureFin()));
            stmt.setString(6, r.getEtat());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean estCreneauDisponible(int idSalle, LocalDate date, LocalTime debut, LocalTime fin) {
        String sql = "SELECT * FROM reservation WHERE id_salle = ? AND date = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idSalle);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalTime db = rs.getTime("heure_debut").toLocalTime();
                LocalTime fn = rs.getTime("heure_fin").toLocalTime();
                if (!(fin.isBefore(db) || debut.isAfter(fn))) return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public List<Reservation> getReservationsParUtilisateur(int idUser) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, s.nom AS nom_salle FROM reservation r JOIN salle s ON r.id_salle = s.id WHERE r.id_utilisateur = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Reservation(
                        rs.getInt("id"),
                        rs.getInt("id_utilisateur"),
                        rs.getString("nom_salle"),
                        rs.getDate("date").toLocalDate(),
                        rs.getTime("heure_debut").toLocalTime(),
                        rs.getTime("heure_fin").toLocalTime(),
                        rs.getString("etat")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean ajouterReservationRessource(int idReservation, int idRessource, int quantite) {
        String sql = "INSERT INTO reservation_ressource (id_reservation, id_ressource, quantite) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idReservation);
            stmt.setInt(2, idRessource);
            stmt.setInt(3, quantite);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public int getLastInsertId() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public Utilisateur getUtilisateurParId(int id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<String> getCreneauxDisponibles(int idSalle, LocalDate date) {
        List<String> dispo = new ArrayList<>();

        // Créneaux types de 1h entre 8h et 18h
        List<LocalTime[]> tous = new ArrayList<>();
        for (int heure = 8; heure < 18; heure++) {
            tous.add(new LocalTime[]{LocalTime.of(heure, 0), LocalTime.of(heure + 1, 0)});
        }

        String sql = "SELECT heure_debut, heure_fin FROM reservation WHERE id_salle = ? AND date = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idSalle);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalTime db = rs.getTime("heure_debut").toLocalTime();
                LocalTime fn = rs.getTime("heure_fin").toLocalTime();

                tous.removeIf(cr -> !(cr[1].isBefore(db) || cr[0].isAfter(fn)));
            }

            for (LocalTime[] cr : tous) {
                dispo.add(cr[0] + " - " + cr[1]);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dispo;
    }
    public boolean supprimerReservation(int idUser, String nomSalle, String date, String heureDebut) throws SQLException {
        String sql = """
        DELETE r FROM reservation r
        JOIN salle s ON r.id_salle = s.id
        WHERE r.id_utilisateur = ? AND s.nom = ? AND r.date = ? AND r.heure_debut = ? AND r.etat = 'en_attente'
    """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            stmt.setString(2, nomSalle);
            stmt.setDate(3, Date.valueOf(date));
            stmt.setTime(4, Time.valueOf(heureDebut));
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean validerReservation(int id) throws SQLException {
        String sql = "UPDATE reservation SET etat = 'validee' WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    public boolean refuserReservation(int id) throws SQLException {
        String sql = "UPDATE reservation SET etat = 'rejetee' WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    public List<Reservation> getReservationsEnAttente() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = """
        SELECT r.id, r.date, r.heure_debut, r.heure_fin, r.etat,
               u.nom AS utilisateur_nom, s.nom AS salle_nom
        FROM reservation r
        JOIN utilisateur u ON r.id_utilisateur = u.id
        JOIN salle s ON r.id_salle = s.id
        WHERE r.etat = 'en_attente'
        ORDER BY r.date, r.heure_debut
    """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setDate(rs.getDate("date").toLocalDate());
                r.setHeureDebut(rs.getTime("heure_debut").toLocalTime());
                r.setHeureFin(rs.getTime("heure_fin").toLocalTime());
                r.setEtat(rs.getString("etat"));
                r.setnom(rs.getString("utilisateur_nom"));
                r.setNomSalle(rs.getString("salle_nom"));

                list.add(r);
            }
        }

        return list;
    }

    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> liste = new ArrayList<>();
        String sql = "SELECT r.id, u.nom AS nom_utilisateur, s.nom AS nom_salle, r.date, r.heure_debut, r.heure_fin, r.etat " +
                "FROM reservation r " +
                "JOIN utilisateur u ON r.id_utilisateur = u.id " +
                "JOIN salle s ON r.id_salle = s.id";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setnom(rs.getString("nom_utilisateur"));
                r.setNomSalle(rs.getString("nom_salle"));
                r.setDate(rs.getDate("date").toLocalDate());
                r.setHeureDebut(rs.getTime("heure_debut").toLocalTime());
                r.setHeureFin(rs.getTime("heure_fin").toLocalTime());
                r.setEtat(rs.getString("etat"));
                liste.add(r);
            }
        }

        return liste;
    }


    public List<Reservation> getReservationsParSalle(String nomSalle) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, u.nom AS nomUtilisateur, s.nom AS nomSalle " +
                "FROM reservation r " +
                "JOIN utilisateur u ON r.id_utilisateur = u.id " +
                "JOIN salle s ON r.id_salle = s.id " +
                "WHERE s.nom = ? ORDER BY r.date, r.heure_debut";

        PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql);
        stmt.setString(1, nomSalle);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Reservation r = new Reservation();
            r.setId(rs.getInt("id"));
            r.setDate(rs.getDate("date").toLocalDate());
             r.setHeureDebut(rs.getTime("heure_debut").toLocalTime());
             r.setHeureFin(rs.getTime("heure_fin").toLocalTime());
            r.setEtat(rs.getString("etat"));
            r.setnom(rs.getString("nomUtilisateur"));
            r.setNomSalle(rs.getString("nomSalle"));
            list.add(r);
        }

        return list;
    }

}
