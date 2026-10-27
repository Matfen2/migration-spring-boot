# Guide de migration : Java 11 / Spring Boot 2.7 → Java 17 / Spring Boot 3.5

**Statut : migration terminée.** Les 24 tests sont verts à la fin de chaque étape (tags `v0-java11-boot2`, `v1-java17-boot2`, `v2-java17-boot3`).

Ce guide se suit dans l'ordre. **Une étape = un commit**, et les tests doivent être verts avant de passer à la suivante : ce sont eux qui prouvent que la migration ne change pas le comportement.

À chaque problème rencontré, note-le dans le [journal de migration](#journal-de-migration) en bas de ce fichier : c'est la matière de ton entretien.

## Prérequis : un JDK 17

Spring Boot 2.7 ne fonctionne pas sur Java 25 (Hibernate 5 et Mockito ne savent pas générer leurs classes sur une JVM aussi récente). Il faut un **JDK 17**, qui sert à la fois au point de départ et à l'arrivée.

1. Télécharge l'archive ZIP de **Temurin 17** pour Windows x64 sur [adoptium.net](https://adoptium.net/temurin/releases/?version=17&os=windows&package=jdk).
2. Décompresse-la dans `C:\Users\EyesEagle\jdks\` (pas besoin d'être administrateur).
3. Dans chaque terminal PowerShell où tu travailles sur ce projet :

   ```powershell
   $env:JAVA_HOME = "$HOME\jdks\jdk-17.0.x+y"   # le nom exact du dossier décompressé
   $env:Path = "$env:JAVA_HOME\bin;$env:Path"
   java -version                                # doit afficher 17
   ```

Ce réglage ne vaut que pour le terminal ouvert : ton Java 25 reste celui de MécaDétect.

---

## Étape 0 : état des lieux

**Objectif** : prouver que le point de départ fonctionne, avant de toucher à quoi que ce soit.

```powershell
mvn clean verify
```

Résultat attendu : **24 tests verts**. Lance aussi l'application (`mvn spring-boot:run`) et ouvre http://localhost:8080/swagger-ui/ : la documentation Springfox s'affiche.

```powershell
git tag v0-java11-boot2
```

---

## Étape 1 : Java 11 → Java 17

Spring Boot 2.7 supporte Java 17 : on change **une seule chose à la fois**, d'abord Java, Spring Boot ensuite.

### 1a. Compiler en Java 17

Dans `pom.xml`, passe `java.version` de 11 à 17, puis `mvn clean verify`. Les tests doivent rester verts sans rien changer d'autre.

Commit : `build: compilation en Java 17`

### 1b. Moderniser le code

Les tests sont ton filet de sécurité : après chaque changement, `mvn verify`.

| Où | Avant (Java 11) | Après (Java 17) |
| --- | --- | --- |
| `PieceRequest`, `MouvementRequest`, `PieceResponse`, `Erreur` | classes avec getters et setters | **records** |
| `Piece.appliquer` | `switch` avec `break` | **switch expression** (`case ENTREE -> …`) |
| `GestionErreurs` | `instanceof` suivi d'un cast | **pattern matching** (`instanceof X x`) |
| `RapportStock` | chaîne concaténée avec `\n` | **text block** (`"""`) et `formatted(…)` |
| `RapportStock` | `BigDecimal.ROUND_HALF_UP`, déprécié | `RoundingMode.HALF_UP` |
| `PieceService` | `.collect(Collectors.toList())` | `.toList()` |

Points d'attention :

- Avec un record, `PieceResponse.isEnAlerte()` devient `enAlerte()`. Le JSON garde le nom `enAlerte` : `PieceApiTest` le vérifie.
- Les annotations de validation (`@NotBlank`…) se placent directement sur les composants du record.
- `RapportStockTest` compare le texte **au caractère près** : un espace en trop dans le text block, et il échoue. C'est voulu.

<details>
<summary>Solution : le record <code>PieceRequest</code> et le switch de <code>Piece</code></summary>

```java
public record PieceRequest(
        @NotBlank @Size(max = 30) String reference,
        @NotBlank @Size(max = 100) String libelle,
        @Min(0) int quantiteStock,
        @Min(0) int seuilAlerte,
        @NotNull @DecimalMin("0.00") BigDecimal prixUnitaire) {
}
```

```java
public void appliquer(TypeMouvement type, int quantite) {
    quantiteStock = switch (type) {
        case ENTREE -> quantiteStock + quantite;
        case SORTIE -> {
            if (quantite > quantiteStock) {
                throw new StockInsuffisantException(reference, quantiteStock, quantite);
            }
            yield quantiteStock - quantite;
        }
    };
}
```

Le `default` disparaît : le compilateur vérifie que tous les cas de l'enum sont traités.

</details>

Commit : `refactor: code modernisé en Java 17 (records, switch, text blocks)`

```powershell
git tag v1-java17-boot2
```

---

## Étape 2 : Spring Boot 2.7 → Spring Boot 3.5

C'est l'étape la plus lourde. Référence officielle : le [guide de migration Spring Boot 3.0](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide).

### 2a. Monter la version et ajouter le détecteur de propriétés

Dans `pom.xml` :

- passe le parent à la dernière version **3.5.x** : **3.5.16**, sortie en juin 2026 ;
- ajoute temporairement l'outil officiel qui signale les propriétés renommées ou supprimées :

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-properties-migrator</artifactId>
      <scope>runtime</scope>
  </dependency>
  ```

Ne compile pas encore : il reste du travail.

### 2b. `javax` → `jakarta`

Spring Boot 3 passe à Jakarta EE 10 : les packages `javax.persistence` et `javax.validation` deviennent `jakarta.persistence` et `jakarta.validation`.

Dans VS Code : recherche globale (`Ctrl+Shift+H`) de `javax.persistence` et `javax.validation`, remplacement par `jakarta.persistence` et `jakarta.validation`. **Ne remplace pas** tout `javax.` : certains packages du JDK (`javax.sql`, `javax.crypto`) ne changent pas.

### 2c. Spring Security 6

`WebSecurityConfigurerAdapter` n'existe plus. La configuration devient une ou plusieurs méthodes `@Bean` :

- les utilisateurs : un bean `UserDetailsService` (`InMemoryUserDetailsManager`) ;
- les règles : un bean `SecurityFilterChain`, écrit avec le DSL en lambdas ;
- `authorizeRequests()` → `authorizeHttpRequests(…)` ; `antMatchers(…)` → `requestMatchers(…)` ;
- `.and()` disparaît.

`SecuriteTest` vérifie que les règles d'accès n'ont pas changé : 401 sans authentification, 403 pour le magasinier qui crée une pièce.

<details>
<summary>Solution : <code>SecurityConfig</code> en Spring Security 6</summary>

```java
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
```

</details>

### 2d. Springfox → springdoc-openapi

Springfox est abandonné depuis 2020 et ne démarre pas avec Spring Boot 3.

1. Retire la dépendance `springfox-boot-starter` et la propriété `springfox.version`.
2. Ajoute `org.springdoc:springdoc-openapi-starter-webmvc-ui`, en version **2.8.9** (compatible avec Spring Boot 3.5).
3. Supprime `SwaggerConfig` ; remplace-le par un bean `OpenAPI` qui reprend le titre, la description et la version.
4. Retire `spring.mvc.pathmatch.matching-strategy=ant_path_matcher` : il ne servait qu'à Springfox.

La documentation reste sur http://localhost:8080/swagger-ui/index.html, et `/v3/api-docs` reste public (`SecuriteTest`).

### 2e. Lancer les tests et lire les avertissements

```powershell
mvn clean verify
```

Attends-toi à **deux surprises**, c'est le cœur de l'exercice :

1. **Une propriété renommée** : au démarrage, le `spring-boot-properties-migrator` affiche un avertissement sur une propriété de `application.properties`. Renomme-la comme il l'indique.
2. **Un test qui échoue** sans que le code ait changé. Lis bien le message, cherche dans le guide de migration Spring Boot 3.0 (section *Spring MVC and WebFlux URL Matching Changes*), puis **décide** : corriger le client, ou rétablir l'ancien comportement ? Il n'y a pas une seule bonne réponse, mais il faut pouvoir justifier la tienne.

Quand tout est vert, **retire `spring-boot-properties-migrator`** : il ne sert que pendant la migration.

<details>
<summary>Solution retenue : rétablir la compatibilité avec <code>UrlHandlerFilter</code></summary>

Spring Framework 6 n'accepte plus `/api/pieces/` comme `/api/pieces`. Pour ne pas casser les anciens clients le jour de la mise à jour, un filtre de Spring 6.2 retire la barre oblique finale **avant** Spring Security, qui voit ainsi l'URL déjà nettoyée. Il est documenté comme temporaire.

```java
@Configuration
public class CompatibiliteUrlConfig {

    @Bean
    FilterRegistrationBean<UrlHandlerFilter> barreObliqueFinale() {
        UrlHandlerFilter filtre = UrlHandlerFilter.trailingSlashHandler("/api/**").wrapRequest().build();
        FilterRegistrationBean<UrlHandlerFilter> enregistrement = new FilterRegistrationBean<>(filtre);
        enregistrement.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return enregistrement;
    }
}
```

L'autre option, plus stricte : accepter le 404, mettre à jour le test et prévenir les clients.

</details>

Commit : `build: migration vers Spring Boot 3.5 (Jakarta EE, Spring Security 6, springdoc)`

```powershell
git tag v2-java17-boot3
```

---

## Pour aller plus loin (facultatif)

- **Java 21** : monte `java.version` à 21 (avec un JDK 21), puis active les threads virtuels avec `spring.threads.virtual.enabled=true`.
- **Spring Boot 4** : la suite logique, puisque le support gratuit de la branche 3.5 s'est terminé le 30 juin 2026. C'est la version de MécaDétect.

---

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