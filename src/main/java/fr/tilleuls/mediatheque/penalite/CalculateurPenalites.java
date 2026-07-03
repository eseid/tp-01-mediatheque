package fr.tilleuls.mediatheque.penalite;

import fr.tilleuls.mediatheque.model.Emprunt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Calcule la pénalité due pour un emprunt rendu en retard.
 */
public class CalculateurPenalites {

    private static final BigDecimal AUCUNE_PENALITE = BigDecimal.ZERO.setScale(2);

    private final GrilleTarifaire grille;

    public CalculateurPenalites(GrilleTarifaire grille) {
        this.grille = grille;
    }

    public BigDecimal calculer(Emprunt emprunt, LocalDate dateRetour) {
        long joursFacturables = joursDeRetard(emprunt, dateRetour) - grille.joursTolerance();
        if (joursFacturables <= 0) {
            return AUCUNE_PENALITE;
        }
        BigDecimal tarif = grille.tarifParJour(emprunt.document().type());
        return plafonner(tarif.multiply(BigDecimal.valueOf(joursFacturables)));
    }

    /**
     * Nombre de jours de retard, jour du retour inclus.
     */
    static long joursDeRetard(Emprunt emprunt, LocalDate dateRetour) {
        return ChronoUnit.DAYS.between(emprunt.dateRetourPrevue(), dateRetour) + 1;
    }

    private BigDecimal plafonner(BigDecimal montant) {
        return montant.min(grille.plafond()).setScale(2, RoundingMode.HALF_UP);
    }
}
