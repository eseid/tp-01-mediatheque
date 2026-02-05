package fr.tilleuls.mediatheque.penalite;

import fr.tilleuls.mediatheque.model.TypeDocument;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GrilleTarifaireTest {

    @Test
    void charge_la_grille_officielle_depuis_le_fichier_de_configuration() {
        GrilleTarifaire grille = GrilleTarifaire.charger();

        assertEquals(new BigDecimal("0.20"), grille.tarifParJour(TypeDocument.LIVRE));
        assertEquals(new BigDecimal("5.00"), grille.plafond());
        assertEquals(3, grille.joursTolerance());
    }

    @Test
    void refuse_de_calculer_un_tarif_non_defini() {
        GrilleTarifaire grille = new GrilleTarifaire(
                Map.of(TypeDocument.LIVRE, new BigDecimal("0.20")), new BigDecimal("5.00"), 3);

        assertThrows(IllegalArgumentException.class, () -> grille.tarifParJour(TypeDocument.DVD));
    }
}
