package fr.stockpieces.rapport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.stockpieces.piece.PieceRepository;

@RestController
public class RapportController {

	private final PieceRepository repository;

	@Autowired
	public RapportController(PieceRepository repository) {
		this.repository = repository;
	}

	@GetMapping(value = "/api/rapport", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
	public String rapport() {
		return RapportStock.generer(repository.findAllByOrderByReferenceAsc());
	}
}
