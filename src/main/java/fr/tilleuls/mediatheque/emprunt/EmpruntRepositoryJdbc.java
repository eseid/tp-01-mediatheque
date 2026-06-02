package fr.tilleuls.mediatheque.emprunt;

import fr.tilleuls.mediatheque.model.Emprunt;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Stockage des emprunts dans PostgreSQL.
 */
public class EmpruntRepositoryJdbc implements EmpruntRepository {

    private final String url;
    private final String utilisateur;
    private final String motDePasse;

    public EmpruntRepositoryJdbc(String url, String utilisateur, String motDePasse) {
        this.url = url;
        this.utilisateur = utilisateur;
        this.motDePasse = motDePasse;
    }

    public void initialiserSchema() {
        try (InputStream in = getClass().getResourceAsStream("/schema.sql");
             Connection connexion = connexion();
             Statement statement = connexion.createStatement()) {
            if (in == null) {
                throw new IllegalStateException("schema.sql introuvable dans le classpath");
            }
            statement.execute(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible d'initialiser le schéma", e);
        }
    }

    /**
     * Supprime tous les emprunts. Réservé aux tests.
     */
    public void viderTout() {
        executer("DELETE FROM emprunt");
    }

    @Override
    public void enregistrer(Emprunt emprunt) {
        String sql = """
                INSERT INTO emprunt (numero_adherent, code_document, type_document, date_emprunt, date_retour_prevue)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connexion = connexion();
             PreparedStatement requete = connexion.prepareStatement(sql)) {
            requete.setString(1, emprunt.adherent().numero());
            requete.setString(2, emprunt.document().code());
            requete.setString(3, emprunt.document().type().name());
            requete.setDate(4, Date.valueOf(emprunt.dateEmprunt()));
            requete.setDate(5, Date.valueOf(emprunt.dateRetourPrevue()));
            requete.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible d'enregistrer l'emprunt", e);
        }
    }

    @Override
    public boolean estEmprunte(String codeDocument) {
        return compter("SELECT COUNT(*) FROM emprunt WHERE code_document = ? AND date_retour IS NULL",
                codeDocument) > 0;
    }

    @Override
    public int nombreEmpruntsEnCours(String numeroAdherent) {
        return compter("SELECT COUNT(*) FROM emprunt WHERE numero_adherent = ? AND date_retour IS NULL",
                numeroAdherent);
    }

    private int compter(String sql, String parametre) {
        try (Connection connexion = connexion();
             PreparedStatement requete = connexion.prepareStatement(sql)) {
            requete.setString(1, parametre);
            try (ResultSet resultat = requete.executeQuery()) {
                resultat.next();
                return resultat.getInt(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erreur de lecture des emprunts", e);
        }
    }

    private void executer(String sql) {
        try (Connection connexion = connexion();
             Statement statement = connexion.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Erreur SQL : " + sql, e);
        }
    }

    private Connection connexion() throws SQLException {
        return DriverManager.getConnection(url, utilisateur, motDePasse);
    }
}
