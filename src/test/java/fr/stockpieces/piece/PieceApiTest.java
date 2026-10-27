package fr.stockpieces.piece;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * API des pièces, avec la vraie configuration de sécurité et les données de départ (donnees.sql).
 * Chaque test est annulé à la fin : la base retrouve ses 5 pièces de départ.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PieceApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("Les 5 pièces de départ sont chargées et listées par référence")
	void donneesDeDepart() throws Exception {
		mockMvc.perform(get("/api/pieces").with(httpBasic("magasinier", "magasin")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(5))
				.andExpect(jsonPath("$[0].reference").value("COU-A48"));
	}

	@Test
	@DisplayName("Un ancien client appelle l'URL avec une barre oblique finale : elle répond aussi")
	void barreObliqueFinale() throws Exception {
		mockMvc.perform(get("/api/pieces/").with(httpBasic("magasinier", "magasin")))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Les pièces à réapprovisionner sont signalées")
	void alertes() throws Exception {
		mockMvc.perform(get("/api/pieces/alertes").with(httpBasic("magasinier", "magasin")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].reference").value("COU-A48"))
				.andExpect(jsonPath("$[1].reference").value("JOI-T80"));
	}

	@Test
	@DisplayName("Le responsable crée une pièce : 201")
	void creation() throws Exception {
		mockMvc.perform(post("/api/pieces").with(httpBasic("responsable", "responsable"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"reference\":\"VAN-15\",\"libelle\":\"Vanne 1/4 tour DN15\","
						+ "\"quantiteStock\":4,\"seuilAlerte\":2,\"prixUnitaire\":12.40}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.enAlerte").value(false));
	}

	@Test
	@DisplayName("Une pièce sans libellé est refusée : 400")
	void creationInvalide() throws Exception {
		mockMvc.perform(post("/api/pieces").with(httpBasic("responsable", "responsable"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"reference\":\"VAN-16\",\"libelle\":\"\",\"prixUnitaire\":1}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("DONNEES_INVALIDES"));
	}

	@Test
	@DisplayName("Une référence déjà utilisée est refusée : 409")
	void creationDoublon() throws Exception {
		mockMvc.perform(post("/api/pieces").with(httpBasic("responsable", "responsable"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"reference\":\"rou-6205\",\"libelle\":\"Doublon\",\"prixUnitaire\":1}"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("REFERENCE_DEJA_UTILISEE"));
	}

	@Test
	@DisplayName("Une sortie supérieure au stock est refusée : 409")
	void sortieImpossible() throws Exception {
		mockMvc.perform(post("/api/pieces/5/mouvements").with(httpBasic("magasinier", "magasin"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"type\":\"SORTIE\",\"quantite\":3}"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("STOCK_INSUFFISANT"));
	}

	@Test
	@DisplayName("Une pièce inconnue renvoie 404")
	void pieceInconnue() throws Exception {
		mockMvc.perform(get("/api/pieces/999").with(httpBasic("magasinier", "magasin")))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("PIECE_INTROUVABLE"));
	}

	@Test
	@DisplayName("Le rapport de stock est disponible en texte")
	void rapport() throws Exception {
		mockMvc.perform(get("/api/rapport").with(httpBasic("magasinier", "magasin")))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
						.string(org.hamcrest.Matchers.containsString("Pièces référencées : 5")));
	}
}
