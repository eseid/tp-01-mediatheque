package fr.tilleuls.mediatheque.model;

import java.time.LocalDate;

public record Emprunt(Adherent adherent, Document document, LocalDate dateEmprunt, LocalDate dateRetourPrevue) {
}
