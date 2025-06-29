package model;

public class Salle {
    private int id;
    private String nom;
    private int capacite;
    private String type;

    public Salle(int id, String nom, int capacite, String type) {
        this.id = id;
        this.nom = nom;
        this.capacite = capacite;
        this.type = type;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public int getCapacite() { return capacite; }
    public String getType() { return type; }
}
