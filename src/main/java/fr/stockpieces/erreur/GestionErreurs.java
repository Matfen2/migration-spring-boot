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
		HttpStatus statut;
		String code;
		if (exception instanceof PieceIntrouvableException) {
			statut = HttpStatus.NOT_FOUND;
			code = "PIECE_INTROUVABLE";
		} else if (exception instanceof StockInsuffisantException) {
			statut = HttpStatus.CONFLICT;
			code = "STOCK_INSUFFISANT";
		} else {
			statut = HttpStatus.CONFLICT;
			code = "REFERENCE_DEJA_UTILISEE";
		}
		return ResponseEntity.status(statut).body(new Erreur(code, exception.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Erreur> donneesInvalides(MethodArgumentNotValidException exception) {
		return ResponseEntity.badRequest().body(new Erreur("DONNEES_INVALIDES", "Certains champs sont invalides."));
	}
}
