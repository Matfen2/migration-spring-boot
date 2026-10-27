package fr.stockpieces.piece;

public class StockInsuffisantException extends RuntimeException {

	public StockInsuffisantException(String reference, int disponible, int demande) {
		super("Stock insuffisant pour " + reference + " : " + disponible + " disponible(s), " + demande + " demandé(s).");
	}
}
