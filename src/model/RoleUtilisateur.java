package model;

public enum RoleUtilisateur {
    ADMIN("admin"),
    DEMANDEUR("demandeur"),
    RESPONSABLE("responsable");

    private final String libelle;

    RoleUtilisateur(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    public static RoleUtilisateur fromString(String text) {
        for (RoleUtilisateur r : RoleUtilisateur.values()) {
            if (r.libelle.equalsIgnoreCase(text)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Rôle non reconnu: " + text);
    }
} 