package fr.stockpieces.piece;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PieceService {

	private final PieceRepository repository;

	@Autowired
	public PieceService(PieceRepository repository) {
		this.repository = repository;
	}

	@Transactional(readOnly = true)
	public List<PieceResponse> lister() {
		return repository.findAllByOrderByReferenceAsc().stream()
				.map(PieceResponse::de)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public PieceResponse obtenir(Long id) {
		return PieceResponse.de(charger(id));
	}

	@Transactional(readOnly = true)
	public List<PieceResponse> enAlerte() {
		return repository.findEnAlerte().stream()
				.map(PieceResponse::de)
				.collect(Collectors.toList());
	}

	public PieceResponse creer(PieceRequest requete) {
		String reference = requete.getReference().trim();
		if (repository.existsByReferenceIgnoreCase(reference)) {
			throw new ReferenceDejaUtiliseeException(reference);
		}
		Piece piece = new Piece(reference, requete.getLibelle().trim(), requete.getQuantiteStock(),
				requete.getSeuilAlerte(), requete.getPrixUnitaire());
		return PieceResponse.de(repository.save(piece));
	}

	public PieceResponse mouvement(Long id, MouvementRequest requete) {
		Piece piece = charger(id);
		piece.appliquer(requete.getType(), requete.getQuantite());
		return PieceResponse.de(piece);
	}

	private Piece charger(Long id) {
		return repository.findById(id).orElseThrow(() -> new PieceIntrouvableException(id));
	}
}
