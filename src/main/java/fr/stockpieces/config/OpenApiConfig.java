package fr.stockpieces.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Documentation de l'API avec springdoc-openapi, sur /swagger-ui/index.html.
 */
@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI api() {
		return new OpenAPI().info(new Info()
				.title("Stock de pièces détachées")
				.description("Consultation du stock, mouvements et alertes de réapprovisionnement")
				.version("2.0.0"));
	}
}