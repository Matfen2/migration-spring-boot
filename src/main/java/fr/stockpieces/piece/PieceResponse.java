package fr.stockpieces.piece;

import java.math.BigDecimal;

/**
 * Pièce exposée par l'API.
 */
public class PieceResponse {

	private final Long id;
	private final String reference;
	private final String libelle;
	private final int quantiteStock;
	private final int seuilAlerte;
	private final BigDecimal prixUnitaire;
	private final boolean enAlerte;

	public PieceResponse(Long id, String reference, String libelle, int quantiteStock, int seuilAlerte,
			BigDecimal prixUnitaire, boolean enAlerte) {
		this.id = id;
		this.reference = reference;
		this.libelle = libelle;
		this.quantiteStock = quantiteStock;
		this.seuilAlerte = seuilAlerte;
		this.prixUnitaire = prixUnitaire;
		this.enAlerte = enAlerte;
	}

	public static PieceResponse de(Piece piece) {
		return new PieceResponse(piece.getId(), piece.getReference(), piece.getLibelle(), piece.getQuantiteStock(),
				piece.getSeuilAlerte(), piece.getPrixUnitaire(), piece.enAlerte());
	}

	public Long getId() {
		return id;
	}

	public String getReference() {
		return reference;
	}

	public String getLibelle() {
		return libelle;
	}

	public int getQuantiteStock() {
		return quantiteStock;
	}

	public int getSeuilAlerte() {
		return seuilAlerte;
	}

	public BigDecimal getPrixUnitaire() {
		return prixUnitaire;
	}

	public boolean isEnAlerte() {
		return enAlerte;
	}
}
