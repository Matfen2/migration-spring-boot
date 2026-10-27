package fr.stockpieces.piece;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Pièce détachée du magasin de maintenance.
 */
@Entity
@Table(name = "piece")
public class Piece {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 30)
	private String reference;

	@Column(nullable = false, length = 100)
	private String libelle;

	@Column(nullable = false)
	private int quantiteStock;

	@Column(nullable = false)
	private int seuilAlerte;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal prixUnitaire;

	protected Piece() {
	}

	public Piece(String reference, String libelle, int quantiteStock, int seuilAlerte, BigDecimal prixUnitaire) {
		this.reference = reference;
		this.libelle = libelle;
		this.quantiteStock = quantiteStock;
		this.seuilAlerte = seuilAlerte;
		this.prixUnitaire = prixUnitaire;
	}

	/**
	 * Applique un mouvement de stock.
	 *
	 * @param type     entrée ou sortie
	 * @param quantite quantité, strictement positive
	 * @throws StockInsuffisantException si une sortie dépasse le stock disponible
	 */
	public void appliquer(TypeMouvement type, int quantite) {
		switch (type) {
			case ENTREE:
				quantiteStock = quantiteStock + quantite;
				break;
			case SORTIE:
				if (quantite > quantiteStock) {
					throw new StockInsuffisantException(reference, quantiteStock, quantite);
				}
				quantiteStock = quantiteStock - quantite;
				break;
			default:
				throw new IllegalArgumentException("Type de mouvement inconnu : " + type);
		}
	}

	public boolean enAlerte() {
		return quantiteStock <= seuilAlerte;
	}

	public BigDecimal valeurStock() {
		return prixUnitaire.multiply(BigDecimal.valueOf(quantiteStock));
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
}
