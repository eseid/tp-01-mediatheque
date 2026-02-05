package fr.tilleuls.mediatheque.penalite;

import fr.tilleuls.mediatheque.model.Emprunt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CalculateurPenalites {

    private final GrilleTarifaire grille;

    public CalculateurPenalites(GrilleTarifaire grille) {
        this.grille = grille;
    }

    public BigDecimal calculer(Emprunt emprunt, LocalDate dateRetour) {
        long joursRetard = ChronoUnit.DAYS.between(emprunt.dateRetourPrevue(), dateRetour);
        long joursFacturables = joursRetard - grille.joursTolerance();
        if (joursFacturables <= 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        BigDecimal montant = grille.tarifParJour(emprunt.document().type())
                .multiply(BigDecimal.valueOf(joursFacturables));
        return montant.min(grille.plafond()).setScale(2, RoundingMode.HALF_UP);
    }
}
