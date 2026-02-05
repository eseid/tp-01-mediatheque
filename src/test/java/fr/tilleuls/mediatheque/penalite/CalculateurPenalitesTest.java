package fr.tilleuls.mediatheque.penalite;

import fr.tilleuls.mediatheque.model.Adherent;
import fr.tilleuls.mediatheque.model.Categorie;
import fr.tilleuls.mediatheque.model.Document;
import fr.tilleuls.mediatheque.model.Emprunt;
import fr.tilleuls.mediatheque.model.TypeDocument;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalculateurPenalitesTest {

    private static final LocalDate RETOUR_PREVU = LocalDate.of(2026, 3, 10);

    private final GrilleTarifaire grille = new GrilleTarifaire(
            Map.of(TypeDocument.LIVRE, new BigDecimal("0.20"),
                   TypeDocument.DVD, new BigDecimal("0.30")),
            new BigDecimal("5.00"),
            3);

    private final CalculateurPenalites calculateur = new CalculateurPenalites(grille);

    private final Adherent adulte = new Adherent("A-0042", "Jeanne Martin", Categorie.ADULTE);

    private Emprunt empruntDe(TypeDocument type) {
        Document document = new Document("D-0001", "Un document", type);
        return new Emprunt(adulte, document, RETOUR_PREVU.minusDays(type.dureeEmpruntJours()), RETOUR_PREVU);
    }

    @Test
    void pas_de_penalite_si_le_document_est_rendu_a_temps() {
        assertEquals(new BigDecimal("0.00"), calculateur.calculer(empruntDe(TypeDocument.LIVRE), RETOUR_PREVU));
    }

    @Test
    void pas_de_penalite_si_le_document_est_rendu_en_avance() {
        assertEquals(new BigDecimal("0.00"),
                calculateur.calculer(empruntDe(TypeDocument.LIVRE), RETOUR_PREVU.minusDays(4)));
    }

    @Test
    void un_retard_au_dela_de_la_tolerance_est_facture() {
        BigDecimal montant = calculateur.calculer(empruntDe(TypeDocument.LIVRE), RETOUR_PREVU.plusDays(6));

        assertTrue(montant.signum() > 0);
    }

    @Test
    void la_penalite_est_plafonnee() {
        assertEquals(new BigDecimal("5.00"),
                calculateur.calculer(empruntDe(TypeDocument.DVD), RETOUR_PREVU.plusDays(60)));
    }
}
