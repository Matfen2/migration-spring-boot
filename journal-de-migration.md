## Journal de migration

| Étape | Problème rencontré | Cause | Solution retenue |
| --- | --- | --- | --- |
| 0 | Spring Boot 2.7 ne démarre pas sur Java 25 | Hibernate 5 et Mockito ne savent pas générer leurs classes sur une JVM aussi récente | JDK 17 installé à part, sans droits administrateur, activé par terminal (`JAVA_HOME`) |
| 1a | Aucun | Spring Boot 2.7 supporte déjà Java 17 | Changement de `java.version` seul, 24 tests verts |
| 1b | Le test du service ne compilait plus | Les records n'ont pas de setters | Construction directe : `new PieceRequest(…)` |
| 1b | Risque de rapport différent sous Windows | `%n` produit `\r\n` sous Windows | `\n` explicite dans le format du rapport |
| 1b | Risque d'afficher `460,80` au lieu de `460.80` | `%.2f` dépend de la langue du système | `toPlainString()` sur un `BigDecimal` arrondi avec `RoundingMode.HALF_UP` |
| 2 | Springfox ne démarre pas avec Spring Boot 3 | Projet abandonné depuis 2020, incompatible avec Spring 6 | Remplacé par springdoc-openapi 2.8 ; propriété `ant_path_matcher` retirée |
| 2 | `WebSecurityConfigurerAdapter` n'existe plus | Supprimé dans Spring Security 6 | Beans `SecurityFilterChain` et `UserDetailsService`, DSL en lambdas, `requestMatchers` |
| 2 | `GET /api/pieces/` renvoie 404 au lieu de 200 | Spring 6 n'accepte plus la barre oblique finale par défaut (risque de sécurité) | `UrlHandlerFilter` avant Spring Security, compatibilité temporaire à retirer quand les clients seront migrés |
| 2 | `server.max-http-header-size` signalée au démarrage | Propriété renommée dans Spring Boot 3 | Renommée en `server.max-http-request-header-size`, détectée par `spring-boot-properties-migrator`, retiré ensuite |