package model;

public class ReservationRessource {
    private int id;
    private int idReservation;
    private int idRessource;
    private int quantite;

    public ReservationRessource() {}

    public ReservationRessource(int id, int idReservation, int idRessource, int quantite) {
        this.id = id;
        this.idReservation = idReservation;
        this.idRessource = idRessource;
        this.quantite = quantite;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdReservation() { return idReservation; }
    public void setIdReservation(int idReservation) { this.idReservation = idReservation; }

    public int getIdRessource() { return idRessource; }
    public void setIdRessource(int idRessource) { this.idRessource = idRessource; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
}
