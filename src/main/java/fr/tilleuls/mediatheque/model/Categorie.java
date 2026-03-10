package fr.tilleuls.mediatheque.model;

/**
 * Catégorie d'adhérent. Le quota est le nombre maximum d'emprunts simultanés.
 */
public enum Categorie {
    ADULTE(5),
    ETUDIANT(8),
    ENFANT(3);

    private final int quota;

    Categorie(int quota) {
        this.quota = quota;
    }

    public int quota() {
        return quota;
    }
}
