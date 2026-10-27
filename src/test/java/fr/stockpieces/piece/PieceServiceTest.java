package fr.stockpieces.piece;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PieceServiceTest {

	@Mock
	private PieceRepository repository;

	@InjectMocks
	private PieceService service;

	@Test
	@DisplayName("Une référence déjà utilisée est refusée, sans enregistrement")
	void referenceDejaUtilisee() {
		PieceRequest requete = new PieceRequest();
		requete.setReference(" ROU-6205 ");
		requete.setLibelle("Roulement");
		requete.setPrixUnitaire(BigDecimal.ONE);
		when(repository.existsByReferenceIgnoreCase("ROU-6205")).thenReturn(true);

		assertThatThrownBy(() -> service.creer(requete)).isInstanceOf(ReferenceDejaUtiliseeException.class);
		verify(repository, never()).save(any());
	}

	@Test
	@DisplayName("Un mouvement sur une pièce inconnue est refusé")
	void pieceInconnue() {
		when(repository.findById(99L)).thenReturn(Optional.empty());
		MouvementRequest requete = new MouvementRequest();
		requete.setType(TypeMouvement.ENTREE);
		requete.setQuantite(1);

		assertThatThrownBy(() -> service.mouvement(99L, requete)).isInstanceOf(PieceIntrouvableException.class);
	}
}
