package fr.stockpieces.piece;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pieces")
public class PieceController {

	private final PieceService service;

	public PieceController(PieceService service) {
		this.service = service;
	}

	@RequestMapping(method = RequestMethod.GET)
	public List<PieceResponse> lister() {
		return service.lister();
	}

	@RequestMapping(value = "/alertes", method = RequestMethod.GET)
	public List<PieceResponse> enAlerte() {
		return service.enAlerte();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public PieceResponse obtenir(@PathVariable("id") Long id) {
		return service.obtenir(id);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public PieceResponse creer(@Valid @RequestBody PieceRequest requete) {
		return service.creer(requete);
	}

	@RequestMapping(value = "/{id}/mouvements", method = RequestMethod.POST)
	public PieceResponse mouvement(@PathVariable("id") Long id, @Valid @RequestBody MouvementRequest requete) {
		return service.mouvement(id, requete);
	}
}