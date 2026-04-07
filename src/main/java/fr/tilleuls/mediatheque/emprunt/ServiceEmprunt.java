package fr.tilleuls.mediatheque.emprunt;

import fr.tilleuls.mediatheque.model.Adherent;
import fr.tilleuls.mediatheque.model.Document;
import fr.tilleuls.mediatheque.model.Emprunt;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class ServiceEmprunt {

    private final EmpruntRepository repository;

    public ServiceEmprunt(EmpruntRepository repository) {
        this.repository = repository;
    }

    public Emprunt emprunter(Adherent adherent, Document document) {
        if (repository.estEmprunte(document.code())) {
            throw new EmpruntRefuseException("Le document " + document.code() + " est déjà emprunté");
        }
        int quota = adherent.categorie().quota();
        if (repository.nombreEmpruntsEnCours(adherent.numero()) >= quota) {
            throw new EmpruntRefuseException(
                    "Quota atteint pour l'adhérent " + adherent.numero() + " (" + quota + " emprunts maximum)");
        }

        LocalDate aujourdhui = LocalDate.now();
        LocalDate retourPrevu = premierJourDOuverture(aujourdhui.plusDays(document.type().dureeEmpruntJours()));
        Emprunt emprunt = new Emprunt(adherent, document, aujourdhui, retourPrevu);
        repository.enregistrer(emprunt);
        return emprunt;
    }

    /**
     * La médiathèque est fermée le dimanche et le lundi : un retour prévu
     * ces jours-là est reporté au mardi.
     */
    static LocalDate premierJourDOuverture(LocalDate date) {
        LocalDate jour = date;
        while (jour.getDayOfWeek() == DayOfWeek.SUNDAY || jour.getDayOfWeek() == DayOfWeek.MONDAY) {
            jour = jour.plusDays(1);
        }
        return jour;
    }
}
