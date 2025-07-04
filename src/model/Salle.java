package model;

import java.util.ArrayList;
import java.util.List;

public class Salle {
    private int id;
    private String nom;
    private int capacite;
    private String type;
    private String localisation;
    private String description;
    private List<Ressource> ressourcesDisponibles;
    private boolean active;

    // Constructeur principal
    public Salle(int id, String nom, int capacite, String type) {
        this.id = id;
        this.nom = nom;
        this.capacite = capacite;
        this.type = type;
        this.ressourcesDisponibles = new ArrayList<>();
        this.active = true;
    }

    // Constructeur complet
    public Salle(int id, String nom, int capacite, String type, String localisation, String description) {
        this(id, nom, capacite, type);
        this.localisation = localisation;
        this.description = description;
    }

    // Constructeur pour création
    public Salle(String nom, int capacite, String type) {
        this(0, nom, capacite, type);
    }

    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public int getCapacite() { return capacite; }
    public String getType() { return type; }
    public String getLocalisation() { return localisation; }
    public String getDescription() { return description; }
    public List<Ressource> getRessourcesDisponibles() { return new ArrayList<>(ressourcesDisponibles); }
    public boolean isActive() { return active; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public void setType(String type) { this.type = type; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public void setDescription(String description) { this.description = description; }
    public void setActive(boolean active) { this.active = active; }

    // Méthodes pour gérer les ressources
    public void ajouterRessource(Ressource ressource) {
        if (!ressourcesDisponibles.contains(ressource)) {
            ressourcesDisponibles.add(ressource);
        }
    }

    public void retirerRessource(Ressource ressource) {
        ressourcesDisponibles.remove(ressource);
    }

    public boolean aRessource(int idRessource) {
        return ressourcesDisponibles.stream()
                .anyMatch(r -> r.getId() == idRessource);
    }

    public int getNombreRessourcesDisponibles() {
        return ressourcesDisponibles.size();
    }

    // Méthodes utilitaires
    public boolean peutAccueillir(int nombrePersonnes) {
        return capacite >= nombrePersonnes;
    }

    public String getInfoComplete() {
        return String.format("%s (Cap: %d, Type: %s, Localisation: %s)", 
                           nom, capacite, type, localisation != null ? localisation : "Non spécifiée");
    }

    @Override
    public String toString() {
        return nom + " (Capacité: " + capacite + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Salle salle = (Salle) obj;
        return id == salle.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
