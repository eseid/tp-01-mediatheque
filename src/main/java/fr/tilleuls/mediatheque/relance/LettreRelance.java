package fr.tilleuls.mediatheque.relance;

import fr.tilleuls.mediatheque.model.Emprunt;
import org.apache.commons.text.StringSubstitutor;

import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Rédige la lettre de relance envoyée aux adhérents en retard.
 */
public class LettreRelance {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String MODELE = """
            Bonjour ${nom},

            Le document "${titre}" devait etre rendu le ${dateRetourPrevue}.
            Merci de le rapporter des que possible a la mediatheque Les Tilleuls.

            Pour rappel, la mediatheque est ouverte du mardi au samedi.
            """;

    public String rediger(Emprunt emprunt) {
        Map<String, String> valeurs = Map.of(
                "nom", emprunt.adherent().nom(),
                "titre", emprunt.document().titre(),
                "dateRetourPrevue", emprunt.dateRetourPrevue().format(FORMAT_DATE));
        return new StringSubstitutor(valeurs).replace(MODELE);
    }
}
