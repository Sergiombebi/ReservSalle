package model;

public enum EtatReservation {
    EN_ATTENTE("en_attente", "En attente"),
    VALIDEE("validee", "Validée"),
    REJETEE("rejetee", "Rejetée"),
    ANNULEE("annulee", "Annulée");

    private final String code;
    private final String libelle;

    EtatReservation(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }

    public String getCode() {
        return code;
    }

    public String getLibelle() {
        return libelle;
    }

    public static EtatReservation fromCode(String code) {
        for (EtatReservation e : EtatReservation.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("État non reconnu: " + code);
    }
} 