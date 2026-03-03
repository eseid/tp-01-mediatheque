package fr.tilleuls.mediatheque.emprunt;

import fr.tilleuls.mediatheque.model.Adherent;
import fr.tilleuls.mediatheque.model.Categorie;
import fr.tilleuls.mediatheque.model.Document;
import fr.tilleuls.mediatheque.model.Emprunt;
import fr.tilleuls.mediatheque.model.TypeDocument;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceEmpruntTest {

    private final EmpruntRepository repository = new EmpruntRepositoryEnMemoire();
    private final ServiceEmprunt service = new ServiceEmprunt(repository);

    private final Adherent adulte = new Adherent("A-0042", "Jeanne Martin", Categorie.ADULTE);

    @Test
    void un_livre_est_emprunte_pour_trois_semaines() {
        Emprunt emprunt = service.emprunter(adulte, livre("L-0001"));

        assertEquals(LocalDate.now().plusDays(21), emprunt.dateRetourPrevue());
    }

    @Test
    void une_bd_est_empruntee_pour_deux_semaines() {
        Emprunt emprunt = service.emprunter(adulte, new Document("B-0001", "Astérix chez les Bretons", TypeDocument.BD));

        assertEquals(LocalDate.now().plusDays(14), emprunt.dateRetourPrevue());
    }

    @Test
    void un_dvd_est_emprunte_pour_une_semaine() {
        Emprunt emprunt = service.emprunter(adulte, new Document("V-0001", "Le Voyage de Chihiro", TypeDocument.DVD));

        assertEquals(LocalDate.now().plusDays(7), emprunt.dateRetourPrevue());
    }

    @Test
    void un_document_deja_emprunte_est_refuse() {
        Adherent autre = new Adherent("A-0051", "Paul Durand", Categorie.ADULTE);
        service.emprunter(adulte, livre("L-0001"));

        assertThrows(EmpruntRefuseException.class, () -> service.emprunter(autre, livre("L-0001")));
    }

    @Test
    void un_adulte_ne_peut_pas_depasser_cinq_emprunts() {
        for (int i = 1; i <= 5; i++) {
            service.emprunter(adulte, livre("L-000" + i));
        }

        assertThrows(EmpruntRefuseException.class, () -> service.emprunter(adulte, livre("L-0006")));
    }

    private static Document livre(String code) {
        return new Document(code, "Livre " + code, TypeDocument.LIVRE);
    }
}
