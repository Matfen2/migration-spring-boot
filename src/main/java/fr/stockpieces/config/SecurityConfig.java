package fr.stockpieces.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Sécurité de l'API : authentification HTTP Basic, deux rôles.
 * <ul>
 * <li>MAGASINIER : consulte le stock et enregistre les mouvements ;</li>
 * <li>RESPONSABLE : peut en plus créer des pièces.</li>
 * </ul>
 * Comptes en mémoire, pour la démonstration uniquement.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
				.withUser("magasinier").password("{noop}magasin").roles("MAGASINIER")
				.and()
				.withUser("responsable").password("{noop}responsable").roles("RESPONSABLE");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authorizeRequests()
				.antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
				.antMatchers(HttpMethod.POST, "/api/pieces").hasRole("RESPONSABLE")
				.antMatchers("/api/**").hasAnyRole("MAGASINIER", "RESPONSABLE")
				.anyRequest().authenticated()
				.and()
				.httpBasic();
	}
}
