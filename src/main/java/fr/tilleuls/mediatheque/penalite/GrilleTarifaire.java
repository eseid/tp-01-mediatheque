package fr.tilleuls.mediatheque.penalite;

import fr.tilleuls.mediatheque.model.TypeDocument;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Tarifs des pénalités de retard, par type de document.
 */
public final class GrilleTarifaire {

    private static final String FICHIER = "/penalites.properties";

    private final Map<TypeDocument, BigDecimal> tarifsParJour;
    private final BigDecimal plafond;
    private final int joursTolerance;

    public GrilleTarifaire(Map<TypeDocument, BigDecimal> tarifsParJour, BigDecimal plafond, int joursTolerance) {
        this.tarifsParJour = Map.copyOf(tarifsParJour);
        this.plafond = plafond;
        this.joursTolerance = joursTolerance;
    }

    /**
     * Charge la grille officielle depuis le classpath.
     */
    public static GrilleTarifaire charger() {
        try (InputStream in = GrilleTarifaire.class.getResourceAsStream(FICHIER)) {
            if (in == null) {
                throw new IllegalStateException("Configuration introuvable dans le classpath : " + FICHIER);
            }
            Properties props = new Properties();
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));

            Map<TypeDocument, BigDecimal> tarifs = new EnumMap<>(TypeDocument.class);
            for (TypeDocument type : TypeDocument.values()) {
                String cle = "tarif." + type.name().toLowerCase(Locale.ROOT);
                tarifs.put(type, new BigDecimal(obligatoire(props, cle)));
            }
            return new GrilleTarifaire(
                    tarifs,
                    new BigDecimal(obligatoire(props, "plafond")),
                    Integer.parseInt(obligatoire(props, "tolerance.jours")));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String obligatoire(Properties props, String cle) {
        String valeur = props.getProperty(cle);
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalStateException("Clé manquante dans " + FICHIER + " : " + cle);
        }
        return valeur.trim();
    }

    public BigDecimal tarifParJour(TypeDocument type) {
        BigDecimal tarif = tarifsParJour.get(type);
        if (tarif == null) {
            throw new IllegalArgumentException("Aucun tarif défini pour " + type);
        }
        return tarif;
    }

    public BigDecimal plafond() {
        return plafond;
    }

    public int joursTolerance() {
        return joursTolerance;
    }
}
