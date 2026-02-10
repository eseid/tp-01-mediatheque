package fr.tilleuls.mediatheque.emprunt;

import fr.tilleuls.mediatheque.model.Emprunt;

public interface EmpruntRepository {

    void enregistrer(Emprunt emprunt);

    boolean estEmprunte(String codeDocument);

    int nombreEmpruntsEnCours(String numeroAdherent);
}
