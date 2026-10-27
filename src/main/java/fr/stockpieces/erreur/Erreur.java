package fr.stockpieces.erreur;

/**
 * Corps des réponses d'erreur de l'API.
 */
public class Erreur {

	private final String code;
	private final String message;

	public Erreur(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
