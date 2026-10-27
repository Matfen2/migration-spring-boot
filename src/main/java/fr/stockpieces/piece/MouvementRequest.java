package fr.stockpieces.piece;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Corps de la requête d'un mouvement de stock.
 */
public class MouvementRequest {

	@NotNull
	private TypeMouvement type;

	@Min(1)
	private int quantite;

	public TypeMouvement getType() {
		return type;
	}

	public void setType(TypeMouvement type) {
		this.type = type;
	}

	public int getQuantite() {
		return quantite;
	}

	public void setQuantite(int quantite) {
		this.quantite = quantite;
	}
}
