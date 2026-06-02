package fr.tilleuls.mediatheque.emprunt;

import fr.tilleuls.mediatheque.model.Adherent;
import fr.tilleuls.mediatheque.model.Categorie;
import fr.tilleuls.mediatheque.model.Document;
import fr.tilleuls.mediatheque.model.Emprunt;
import fr.tilleuls.mediatheque.model.TypeDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Nécessite une base PostgreSQL. Variables d'environnement :
 * MEDIATHEQUE_DB_URL, MEDIATHEQUE_DB_USER, MEDIATHEQUE_DB_PASSWORD.
 */
@EnabledIfEnvironmentVariable(named = "MEDIATHEQUE_DB_URL", matches = ".+")
class EmpruntRepositoryJdbcIT {

    private EmpruntRepositoryJdbc repository;

    private final Adherent adulte = new Adherent("A-0042", "Jeanne Martin", Categorie.ADULTE);

    @BeforeEach
    void preparerUneBaseVide() {
        repository = new EmpruntRepositoryJdbc(
                System.getenv("MEDIATHEQUE_DB_URL"),
                System.getenv("MEDIATHEQUE_DB_USER"),
                System.getenv("MEDIATHEQUE_DB_PASSWORD"));
        repository.initialiserSchema();
        repository.viderTout();
    }

    @Test
    void un_document_enregistre_devient_indisponible() {
        assertFalse(repository.estEmprunte("L-0001"));

        repository.enregistrer(empruntDe("L-0001"));

        assertTrue(repository.estEmprunte("L-0001"));
    }

    @Test
    void compte_les_emprunts_en_cours_d_un_adherent() {
        repository.enregistrer(empruntDe("L-0001"));
        repository.enregistrer(empruntDe("L-0002"));

        assertEquals(2, repository.nombreEmpruntsEnCours("A-0042"));
        assertEquals(0, repository.nombreEmpruntsEnCours("A-9999"));
    }

    private Emprunt empruntDe(String code) {
        Document livre = new Document(code, "Livre " + code, TypeDocument.LIVRE);
        return new Emprunt(adulte, livre, LocalDate.of(2026, 6, 2), LocalDate.of(2026, 6, 23));
    }
}
