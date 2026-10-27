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
public class PieceRequest {

	@NotBlank
	@Size(max = 30)
	private String reference;

	@NotBlank
	@Size(max = 100)
	private String libelle;

	@Min(0)
	private int quantiteStock;

	@Min(0)
	private int seuilAlerte;

	@NotNull
	@DecimalMin("0.00")
	private BigDecimal prixUnitaire;

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public int getQuantiteStock() {
		return quantiteStock;
	}

	public void setQuantiteStock(int quantiteStock) {
		this.quantiteStock = quantiteStock;
	}

	public int getSeuilAlerte() {
		return seuilAlerte;
	}

	public void setSeuilAlerte(int seuilAlerte) {
		this.seuilAlerte = seuilAlerte;
	}

	public BigDecimal getPrixUnitaire() {
		return prixUnitaire;
	}

	public void setPrixUnitaire(BigDecimal prixUnitaire) {
		this.prixUnitaire = prixUnitaire;
	}
}
