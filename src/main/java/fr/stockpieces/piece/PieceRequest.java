package fr.stockpieces.piece;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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