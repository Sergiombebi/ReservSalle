package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {
    private int id;
    private int idUtilisateur;
    private int idSalle;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String etat; // en_attente, validee, rejetee

    public Reservation() {}

    public Reservation(int id, int idUtilisateur, int idSalle, LocalDate date,
                       LocalTime heureDebut, LocalTime heureFin, String etat) {
        this.id = id;
        this.idUtilisateur = idUtilisateur;
        this.idSalle = idSalle;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.etat = etat;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(int idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public int getIdSalle() { return idSalle; }
    public void setIdSalle(int idSalle) { this.idSalle = idSalle; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
}
