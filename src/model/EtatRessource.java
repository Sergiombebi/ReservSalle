package model;

public enum EtatRessource {
    DISPONIBLE("disponible", "Disponible"),
    EN_PANNE("en_panne", "En panne"),
    MAINTENANCE("maintenance", "En maintenance"),
    RESERVE("reserve", "Réservé");

    private final String code;
    private final String libelle;

    EtatRessource(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }

    public String getCode() {
        return code;
    }

    public String getLibelle() {
        return libelle;
    }

    public static EtatRessource fromCode(String code) {
        for (EtatRessource e : EtatRessource.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("État non reconnu: " + code);
    }
} 