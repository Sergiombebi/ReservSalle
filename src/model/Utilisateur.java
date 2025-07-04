package model;

import java.time.LocalDateTime;

public class Utilisateur {
    private int id;
    private String nom;
    private String email;
    private String motDePasse;
    private RoleUtilisateur role;
    private LocalDateTime dateCreation;
    private LocalDateTime dernierAcces;
    private boolean actif;

    // Constructeur principal
    public Utilisateur(int id, String nom, String email, String motDePasse, RoleUtilisateur role) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.dateCreation = LocalDateTime.now();
        this.actif = true;
    }

    // Constructeur pour compatibilité avec les données existantes
    public Utilisateur(int id, String nom, String email, String motDePasse, String role) {
        this(id, nom, email, motDePasse, RoleUtilisateur.fromString(role));
    }

    // Constructeur pour création
    public Utilisateur(String nom, String email, String motDePasse, RoleUtilisateur role) {
        this(0, nom, email, motDePasse, role);
    }

    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getEmail() { return email; }
    public String getMotDePasse() { return motDePasse; }
    public RoleUtilisateur getRole() { return role; }
    public String getRoleString() { return role.getLibelle(); }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDernierAcces() { return dernierAcces; }
    public boolean isActif() { return actif; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setEmail(String email) { this.email = email; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public void setRole(RoleUtilisateur role) { this.role = role; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public void setDernierAcces(LocalDateTime dernierAcces) { this.dernierAcces = dernierAcces; }
    public void setActif(boolean actif) { this.actif = actif; }

    // Méthodes utilitaires
    public boolean peutGererUtilisateurs() {
        return role == RoleUtilisateur.ADMIN;
    }

    public boolean peutValiderReservations() {
        return role == RoleUtilisateur.RESPONSABLE || role == RoleUtilisateur.ADMIN;
    }

    public boolean peutCreerReservations() {
        return role == RoleUtilisateur.DEMANDEUR || role == RoleUtilisateur.ADMIN;
    }

    @Override
    public String toString() {
        return nom + " (" + role.getLibelle() + ")";
    }
}
