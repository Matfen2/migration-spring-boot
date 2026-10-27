package fr.stockpieces.rapport;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stockpieces.piece.Piece;

/**
 * Le texte exact du rapport : il doit rester identique au caractère près après la modernisation du code.
 */
class RapportStockTest {

	@Test
	@DisplayName("Le rapport liste la valeur du stock et les pièces à réapprovisionner")
	void rapportAvecAlertes() {
		String rapport = RapportStock.generer(Arrays.asList(
				new Piece("COU-A48", "Courroie", 6, 8, new BigDecimal("14.50")),
				new Piece("ROU-6205", "Roulement", 42, 10, new BigDecimal("8.90"))));

		assertThat(rapport).isEqualTo("RAPPORT DE STOCK\n"
				+ "================\n"
				+ "Pièces référencées : 2\n"
				+ "Valeur du stock : 460.80 €\n"
				+ "Pièces à réapprovisionner : 1\n"
				+ "  - COU-A48 : 6 en stock (seuil 8)\n");
	}

	@Test
	@DisplayName("Sans pièce en alerte, le rapport n'a pas de liste")
	void rapportSansAlerte() {
		String rapport = RapportStock.generer(Collections.singletonList(
				new Piece("ROU-6205", "Roulement", 42, 10, new BigDecimal("8.90"))));

		assertThat(rapport).isEqualTo("RAPPORT DE STOCK\n"
				+ "================\n"
				+ "Pièces référencées : 1\n"
				+ "Valeur du stock : 373.80 €\n"
				+ "Pièces à réapprovisionner : 0\n");
	}
}
