package fr.stockpieces.piece;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Corps de la requête de création d'une pièce.
 */
public record PieceRequest(
		@NotBlank @Size(max = 30) String reference,
		@NotBlank @Size(max = 100) String libelle,
		@Min(0) int quantiteStock,
		@Min(0) int seuilAlerte,
		@NotNull @DecimalMin("0.00") BigDecimal prixUnitaire) {
}