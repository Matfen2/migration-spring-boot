package fr.stockpieces.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.UrlHandlerFilter;

/**
 * Compatibilité temporaire avec les anciens clients qui appellent les URL avec une barre oblique finale
 * (/api/pieces/).
 *
 * Spring Boot 2 acceptait ces URL ; Spring Framework 6 ne les accepte plus par défaut, pour éviter
 * que deux URL différentes mènent au même code (une règle de sécurité pourrait n'en couvrir qu'une).
 * Ce filtre retire la barre oblique finale avant tout traitement, Spring Security compris.
 *
 * À supprimer quand tous les clients appelleront les URL sans barre oblique finale.
 */
@Configuration
public class CompatibiliteUrlConfig {

	@Bean
	FilterRegistrationBean<UrlHandlerFilter> barreObliqueFinale() {
		UrlHandlerFilter filtre = UrlHandlerFilter.trailingSlashHandler("/api/**").wrapRequest().build();
		FilterRegistrationBean<UrlHandlerFilter> enregistrement = new FilterRegistrationBean<>(filtre);
		// Avant Spring Security : les règles d'accès voient l'URL déjà nettoyée
		enregistrement.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return enregistrement;
	}
}