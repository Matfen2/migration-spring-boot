package fr.stockpieces.piece;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Corps de la requête d'un mouvement de stock.
 *
 * @param type     entrée ou sortie
 * @param quantite quantité, strictement positive
 */
public record MouvementRequest(
		@NotNull TypeMouvement type,
		@Min(1) int quantite) {
}