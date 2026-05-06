package fr.tilleuls.mediatheque.relance;

import fr.tilleuls.mediatheque.model.Adherent;
import fr.tilleuls.mediatheque.model.Categorie;
import fr.tilleuls.mediatheque.model.Document;
import fr.tilleuls.mediatheque.model.Emprunt;
import fr.tilleuls.mediatheque.model.TypeDocument;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LettreRelanceTest {

    private final LettreRelance lettre = new LettreRelance();

    @Test
    void la_lettre_reprend_le_nom_le_titre_et_la_date_prevue() {
        Emprunt emprunt = new Emprunt(
                new Adherent("A-0042", "Jeanne Martin", Categorie.ADULTE),
                new Document("L-0001", "Le Petit Prince", TypeDocument.LIVRE),
                LocalDate.of(2026, 9, 22),
                LocalDate.of(2026, 10, 13));

        String texte = lettre.rediger(emprunt);

        assertTrue(texte.startsWith("Bonjour Jeanne Martin,"));
        assertTrue(texte.contains("« Le Petit Prince » devait être rendu le 13/10/2026"));
    }
}
