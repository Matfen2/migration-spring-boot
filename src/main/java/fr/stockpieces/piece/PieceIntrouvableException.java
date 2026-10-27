package fr.stockpieces.piece;

public class PieceIntrouvableException extends RuntimeException {

	public PieceIntrouvableException(Long id) {
		super("La pièce " + id + " n'existe pas.");
	}
}
