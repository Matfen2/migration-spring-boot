package fr.stockpieces.piece;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PieceService {

	private final PieceRepository repository;

	public PieceService(PieceRepository repository) {
		this.repository = repository;
	}

	@Transactional(readOnly = true)
	public List<PieceResponse> lister() {
		return repository.findAllByOrderByReferenceAsc().stream()
				.map(PieceResponse::de)
				.toList();
	}

	@Transactional(readOnly = true)
	public PieceResponse obtenir(Long id) {
		return PieceResponse.de(charger(id));
	}

	@Transactional(readOnly = true)
	public List<PieceResponse> enAlerte() {
		return repository.findEnAlerte().stream()
				.map(PieceResponse::de)
				.toList();
	}

	public PieceResponse creer(PieceRequest requete) {
		String reference = requete.reference().trim();
		if (repository.existsByReferenceIgnoreCase(reference)) {
			throw new ReferenceDejaUtiliseeException(reference);
		}
		Piece piece = new Piece(reference, requete.libelle().trim(), requete.quantiteStock(),
				requete.seuilAlerte(), requete.prixUnitaire());
		return PieceResponse.de(repository.save(piece));
	}

	public PieceResponse mouvement(Long id, MouvementRequest requete) {
		Piece piece = charger(id);
		piece.appliquer(requete.type(), requete.quantite());
		return PieceResponse.de(piece);
	}

	private Piece charger(Long id) {
		return repository.findById(id).orElseThrow(() -> new PieceIntrouvableException(id));
	}
}