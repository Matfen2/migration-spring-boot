package fr.stockpieces.piece;

public class ReferenceDejaUtiliseeException extends RuntimeException {

	public ReferenceDejaUtiliseeException(String reference) {
		super("La référence " + reference + " est déjà utilisée.");
	}
}
