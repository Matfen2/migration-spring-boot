package fr.stockpieces.piece;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PieceTest {

	private Piece roulement() {
		return new Piece("ROU-6205", "Roulement à billes 6205", 10, 4, new BigDecimal("8.90"));
	}

	@Test
	@DisplayName("Une entrée augmente le stock")
	void entree() {
		Piece piece = roulement();
		piece.appliquer(TypeMouvement.ENTREE, 5);
		assertThat(piece.getQuantiteStock()).isEqualTo(15);
	}

	@Test
	@DisplayName("Une sortie diminue le stock")
	void sortie() {
		Piece piece = roulement();
		piece.appliquer(TypeMouvement.SORTIE, 3);
		assertThat(piece.getQuantiteStock()).isEqualTo(7);
	}

	@Test
	@DisplayName("Une sortie supérieure au stock est refusée, et le stock reste inchangé")
	void sortieImpossible() {
		Piece piece = roulement();
		assertThatThrownBy(() -> piece.appliquer(TypeMouvement.SORTIE, 11))
				.isInstanceOf(StockInsuffisantException.class)
				.hasMessage("Stock insuffisant pour ROU-6205 : 10 disponible(s), 11 demandé(s).");
		assertThat(piece.getQuantiteStock()).isEqualTo(10);
	}

	@Test
	@DisplayName("La pièce est en alerte quand le stock atteint le seuil")
	void alerte() {
		Piece piece = roulement();
		assertThat(piece.enAlerte()).isFalse();
		piece.appliquer(TypeMouvement.SORTIE, 6);
		assertThat(piece.enAlerte()).isTrue();
	}

	@Test
	@DisplayName("La valeur du stock est le prix unitaire multiplié par la quantité")
	void valeur() {
		assertThat(roulement().valeurStock()).isEqualByComparingTo("89.00");
	}
}
