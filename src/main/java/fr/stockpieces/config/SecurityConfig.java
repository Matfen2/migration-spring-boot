package fr.stockpieces.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Sécurité de l'API : authentification HTTP Basic, deux rôles.
 * <ul>
 * <li>MAGASINIER : consulte le stock et enregistre les mouvements ;</li>
 * <li>RESPONSABLE : peut en plus créer des pièces.</li>
 * </ul>
 * Comptes en mémoire, pour la démonstration uniquement.
 *
 * Spring Security 6 : WebSecurityConfigurerAdapter n'existe plus, la configuration passe par des beans.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	UserDetailsService utilisateurs() {
		return new InMemoryUserDetailsManager(
				User.withUsername("magasinier").password("{noop}magasin").roles("MAGASINIER").build(),
				User.withUsername("responsable").password("{noop}responsable").roles("RESPONSABLE").build());
	}

	@Bean
	SecurityFilterChain securite(HttpSecurity http) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(acces -> acces
						.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/pieces").hasRole("RESPONSABLE")
						.requestMatchers("/api/**").hasAnyRole("MAGASINIER", "RESPONSABLE")
						.anyRequest().authenticated())
				.httpBasic(Customizer.withDefaults())
				.build();
	}
}