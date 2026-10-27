package fr.stockpieces.piece;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PieceRepository extends JpaRepository<Piece, Long> {

	boolean existsByReferenceIgnoreCase(String reference);

	List<Piece> findAllByOrderByReferenceAsc();

	@Query("select p from Piece p where p.quantiteStock <= p.seuilAlerte order by p.reference")
	List<Piece> findEnAlerte();
}
