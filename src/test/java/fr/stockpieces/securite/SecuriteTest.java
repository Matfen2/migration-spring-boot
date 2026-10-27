package fr.stockpieces.securite;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Règles d'accès : elles doivent rester identiques avant et après la migration de Spring Security.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecuriteTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("Sans authentification, l'API répond 401")
	void sansAuthentification() throws Exception {
		mockMvc.perform(get("/api/pieces")).andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("Un mauvais mot de passe est refusé : 401")
	void mauvaisMotDePasse() throws Exception {
		mockMvc.perform(get("/api/pieces").with(httpBasic("magasinier", "faux")))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("Le magasinier ne peut pas créer de pièce : 403")
	void magasinierNePeutPasCreer() throws Exception {
		mockMvc.perform(post("/api/pieces").with(httpBasic("magasinier", "magasin"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"reference\":\"X-1\",\"libelle\":\"X\",\"prixUnitaire\":1}"))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("La documentation de l'API reste accessible sans authentification")
	void documentationPublique() throws Exception {
		mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
	}
}
