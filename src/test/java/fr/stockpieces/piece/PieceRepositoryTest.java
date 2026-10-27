package fr.stockpieces.piece;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PieceRepositoryTest {

	@Autowired
	private PieceRepository repository;

	@Test
	@DisplayName("Les pièces en alerte sont celles dont le stock atteint le seuil")
	void piecesEnAlerte() {
		repository.save(new Piece("TST-OK", "Pièce bien stockée", 50, 10, BigDecimal.ONE));
		repository.save(new Piece("TST-SEUIL", "Pièce au seuil", 10, 10, BigDecimal.ONE));
		repository.save(new Piece("TST-BAS", "Pièce sous le seuil", 2, 10, BigDecimal.ONE));

		assertThat(repository.findEnAlerte())
				.extracting(Piece::getReference)
				.contains("TST-SEUIL", "TST-BAS")
				.doesNotContain("TST-OK");
	}

	@Test
	@DisplayName("La recherche de référence ne tient pas compte de la casse")
	void referenceSansCasse() {
		repository.save(new Piece("TST-CASSE", "Pièce", 1, 0, BigDecimal.ONE));

		assertThat(repository.existsByReferenceIgnoreCase("tst-casse")).isTrue();
	}
}
