package model;

public class Ressource {
    private int id;
    private String nom;
    private String description;
    private int quantite;
    private String etat;

    public Ressource(int id, String nom, String description, int quantite, String etat) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.quantite = quantite;
        this.etat = etat;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
}