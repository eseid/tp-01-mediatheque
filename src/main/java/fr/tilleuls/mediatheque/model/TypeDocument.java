package fr.tilleuls.mediatheque.model;

public enum TypeDocument {
    LIVRE(21),
    DVD(7);

    private final int dureeEmpruntJours;

    TypeDocument(int dureeEmpruntJours) {
        this.dureeEmpruntJours = dureeEmpruntJours;
    }

    public int dureeEmpruntJours() {
        return dureeEmpruntJours;
    }
}
