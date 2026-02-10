package fr.tilleuls.mediatheque.emprunt;

public class EmpruntRefuseException extends RuntimeException {

    public EmpruntRefuseException(String message) {
        super(message);
    }
}
