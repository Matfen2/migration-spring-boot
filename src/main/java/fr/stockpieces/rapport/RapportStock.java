package fr.stockpieces.rapport;

import java.math.BigDecimal;
import java.util.List;

import fr.stockpieces.piece.Piece;

/**
 * Rapport texte de l'état du stock, envoyé chaque semaine au responsable maintenance.
 */
public final class RapportStock {

	private RapportStock() {
	}

	public static String generer(List<Piece> pieces) {
		BigDecimal valeurTotale = BigDecimal.ZERO;
		int enAlerte = 0;
		StringBuilder lignes = new StringBuilder();
		for (Piece piece : pieces) {
			valeurTotale = valeurTotale.add(piece.valeurStock());
			if (piece.enAlerte()) {
				enAlerte++;
				lignes.append("  - ").append(piece.getReference()).append(" : ")
						.append(piece.getQuantiteStock()).append(" en stock (seuil ")
						.append(piece.getSeuilAlerte()).append(")\n");
			}
		}

		String rapport = "RAPPORT DE STOCK\n"
				+ "================\n"
				+ "Pièces référencées : " + pieces.size() + "\n"
				+ "Valeur du stock : " + valeurTotale.setScale(2, BigDecimal.ROUND_HALF_UP) + " €\n"
				+ "Pièces à réapprovisionner : " + enAlerte + "\n";
		if (enAlerte > 0) {
			rapport = rapport + lignes;
		}
		return rapport;
	}
}
