package fr.stockpieces.erreur;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fr.stockpieces.piece.PieceIntrouvableException;
import fr.stockpieces.piece.ReferenceDejaUtiliseeException;
import fr.stockpieces.piece.StockInsuffisantException;

@RestControllerAdvice
public class GestionErreurs {

	@ExceptionHandler({ PieceIntrouvableException.class, StockInsuffisantException.class,
			ReferenceDejaUtiliseeException.class })
	public ResponseEntity<Erreur> erreurMetier(RuntimeException exception) {
		if (exception instanceof PieceIntrouvableException introuvable) {
			return reponse(HttpStatus.NOT_FOUND, "PIECE_INTROUVABLE", introuvable);
		}
		if (exception instanceof StockInsuffisantException insuffisant) {
			return reponse(HttpStatus.CONFLICT, "STOCK_INSUFFISANT", insuffisant);
		}
		return reponse(HttpStatus.CONFLICT, "REFERENCE_DEJA_UTILISEE", exception);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Erreur> donneesInvalides(MethodArgumentNotValidException exception) {
		return ResponseEntity.badRequest().body(new Erreur("DONNEES_INVALIDES", "Certains champs sont invalides."));
	}

	private static ResponseEntity<Erreur> reponse(HttpStatus statut, String code, RuntimeException exception) {
		return ResponseEntity.status(statut).body(new Erreur(code, exception.getMessage()));
	}
}