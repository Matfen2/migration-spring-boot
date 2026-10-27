package fr.stockpieces.piece;

import java.math.BigDecimal;

/**
 * Pièce exposée par l'API.
 */
public record PieceResponse(
		Long id,
		String reference,
		String libelle,
		int quantiteStock,
		int seuilAlerte,
		BigDecimal prixUnitaire,
		boolean enAlerte) {

	public static PieceResponse de(Piece piece) {
		return new PieceResponse(piece.getId(), piece.getReference(), piece.getLibelle(), piece.getQuantiteStock(),
				piece.getSeuilAlerte(), piece.getPrixUnitaire(), piece.enAlerte());
	}
}