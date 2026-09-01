package fr.tilleuls.mediatheque.emprunt;

import fr.tilleuls.mediatheque.model.Emprunt;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation en mémoire, utilisée par les tests et la démo.
 */
public class EmpruntRepositoryEnMemoire implements EmpruntRepository {

    private final List<Emprunt> emprunts = new ArrayList<>();

    @Override
    public void enregistrer(Emprunt emprunt) {
        emprunts.add(emprunt);
    }

    @Override
    public boolean estEmprunte(String codeDocument) {
        return emprunts.stream()
                .anyMatch(emprunt -> emprunt.document().code().equals(codeDocument));
    }

    @Override
    public int nombreEmpruntsEnCours(String numeroAdherent) {
        return (int) emprunts.stream()
                .filter(emprunt -> emprunt.adherent().numero().equals(numeroAdherent))
                .count();
    }
}
