package fr.stockpieces.rapport;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import fr.stockpieces.piece.Piece;

/**
 * Rapport texte de l'état du stock, envoyé chaque semaine au responsable maintenance.
 */
public final class RapportStock {

	private static final String ENTETE = """
			RAPPORT DE STOCK
			================
			Pièces référencées : %d
			Valeur du stock : %s €
			Pièces à réapprovisionner : %d
			""";

	private static final String LIGNE_ALERTE = "  - %s : %d en stock (seuil %d)\n";

	private RapportStock() {
	}

	public static String generer(List<Piece> pieces) {
		BigDecimal valeurTotale = pieces.stream()
				.map(Piece::valeurStock)
				.reduce(BigDecimal.ZERO, BigDecimal::add)
				.setScale(2, RoundingMode.HALF_UP);
		List<Piece> enAlerte = pieces.stream().filter(Piece::enAlerte).toList();

		StringBuilder rapport = new StringBuilder(
				ENTETE.formatted(pieces.size(), valeurTotale.toPlainString(), enAlerte.size()));
		for (Piece piece : enAlerte) {
			rapport.append(LIGNE_ALERTE.formatted(piece.getReference(), piece.getQuantiteStock(), piece.getSeuilAlerte()));
		}
		return rapport.toString();
	}
}