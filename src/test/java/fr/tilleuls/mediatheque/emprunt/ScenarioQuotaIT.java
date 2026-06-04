package fr.tilleuls.mediatheque.emprunt;

import fr.tilleuls.mediatheque.model.Adherent;
import fr.tilleuls.mediatheque.model.Categorie;
import fr.tilleuls.mediatheque.model.Document;
import fr.tilleuls.mediatheque.model.TypeDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Le service et le dépôt PostgreSQL ensemble : un enfant ne peut pas dépasser son quota.
 */
@EnabledIfEnvironmentVariable(named = "MEDIATHEQUE_DB_URL", matches = ".+")
class ScenarioQuotaIT {

    private ServiceEmprunt service;

    @BeforeEach
    void preparerUneBaseVide() {
        EmpruntRepositoryJdbc repository = new EmpruntRepositoryJdbc(
                System.getenv("MEDIATHEQUE_DB_URL"),
                System.getenv("MEDIATHEQUE_DB_USER"),
                System.getenv("MEDIATHEQUE_DB_PASSWORD"));
        repository.initialiserSchema();
        repository.viderTout();
        service = new ServiceEmprunt(repository);
    }

    @Test
    void un_enfant_est_bloque_au_quatrieme_emprunt() {
        Adherent enfant = new Adherent("E-0007", "Léo Martin", Categorie.ENFANT);
        service.emprunter(enfant, bd("B-0001"));
        service.emprunter(enfant, bd("B-0002"));
        service.emprunter(enfant, bd("B-0003"));

        assertThrows(EmpruntRefuseException.class, () -> service.emprunter(enfant, bd("B-0004")));
    }

    private static Document bd(String code) {
        return new Document(code, "BD " + code, TypeDocument.BD);
    }
}
