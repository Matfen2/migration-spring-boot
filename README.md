# Migration Java 11 / Spring Boot 2 → Java 17 / Spring Boot 3

Projet personnel, conçu pour montrer une compétence recherchée en mission : **faire évoluer une application existante sans changer son comportement**.

Le point de départ est une petite API de **gestion du stock de pièces détachées** d'un service maintenance, écrite comme on l'écrivait en 2021 : Java 11, Spring Boot 2.7, `javax`, `WebSecurityConfigurerAdapter`, Springfox. Elle a été migrée étape par étape vers Java 17 et Spring Boot 3.5, **une étape par commit**, avec une suite de **24 tests restée verte du début à la fin**.

## Avant / après

| | Avant (`v0-java11-boot2`) | Après (`v2-java17-boot3`) |
| --- | --- | --- |
| Java | 11 | 17 |
| Spring Boot | 2.7.18 | 3.5.16 |
| Jakarta EE | `javax.persistence`, `javax.validation` | `jakarta.persistence`, `jakarta.validation` |
| Spring Security | 5.7, `WebSecurityConfigurerAdapter` | 6.5, beans `SecurityFilterChain` et `UserDetailsService` |
| Documentation de l'API | Springfox 3 (abandonné) | springdoc-openapi 2.8, OpenAPI 3.1 |
| Hibernate | 5.6 | 6.6 |
| DTO | classes avec getters et setters | records |
| Code | `switch` avec `break`, casts après `instanceof`, chaînes concaténées | switch expressions, pattern matching, text blocks |

## Les étapes

| Étape | Contenu | Tag |
| --- | --- | --- |
| 0 | État des lieux : Java 11, Spring Boot 2.7, 24 tests verts | `v0-java11-boot2` |
| 1 | Java 17 : compilation, puis records, switch expressions, pattern matching, text blocks, `toList()` | `v1-java17-boot2` |
| 2 | Spring Boot 3.5 : `javax` → `jakarta`, Spring Security 6, Springfox → springdoc, propriétés renommées, changement de comportement détecté par les tests | `v2-java17-boot3` |

Chaque tag se compare au précédent sur GitHub (*Compare*), pour voir exactement ce que chaque étape a changé.

## Ce que les tests ont révélé

La migration compilait, mais **deux changements de comportement** n'auraient été visibles qu'en production sans les tests :

- **Les URL avec une barre oblique finale renvoyaient 404.** Spring Framework 6 n'accepte plus `/api/pieces/` comme `/api/pieces` par défaut, parce que deux URL menant au même code peuvent échapper à une règle de sécurité. Un test simulant un ancien client l'a détecté. Pour ne pas casser les clients existants, un `UrlHandlerFilter` (Spring 6.2) retire la barre oblique finale **avant** Spring Security ; il est documenté comme temporaire, à retirer quand les clients seront migrés.
- **Une propriété avait été renommée** (`server.max-http-header-size` → `server.max-http-request-header-size`). Sans correction, elle aurait été ignorée en silence. L'outil officiel `spring-boot-properties-migrator`, ajouté le temps de la migration, l'a signalée au démarrage.

Le détail de chaque problème, sa cause et la solution retenue sont dans le [journal de migration](MIGRATION.md#journal-de-migration).

## Choix de méthode

- **Une seule chose à la fois** : Java d'abord, Spring Boot ensuite. Spring Boot 2.7 supportant Java 17, chaque étape reste petite et vérifiable.
- **Les tests avant le code** : la suite couvre le domaine, le service, le repository, l'API, les règles de sécurité et le texte exact du rapport. Elle n'a pas été modifiée pendant la migration, sauf pour suivre le passage aux records.
- **Migration manuelle, guidée par le guide officiel** de Spring Boot 3.0. Sur une grosse base de code, j'utiliserais OpenRewrite pour automatiser la partie mécanique (`javax` → `jakarta`, Spring Security), puis la même méthode de vérification par les tests.

## L'application

| Route | Rôle | Description |
| --- | --- | --- |
| `GET /api/pieces` | magasinier, responsable | Liste des pièces, par référence |
| `GET /api/pieces/{id}` | magasinier, responsable | Détail d'une pièce |
| `GET /api/pieces/alertes` | magasinier, responsable | Pièces à réapprovisionner (stock au seuil ou en dessous) |
| `POST /api/pieces` | responsable | Création d'une pièce |
| `POST /api/pieces/{id}/mouvements` | magasinier, responsable | Entrée ou sortie de stock |
| `GET /api/rapport` | magasinier, responsable | Rapport texte de l'état du stock |

Authentification HTTP Basic, comptes de démonstration : `magasinier` / `magasin` et `responsable` / `responsable`. Base H2 en mémoire, chargée avec 5 pièces au démarrage.

## Lancer le projet

Prérequis : un **JDK 17** et Maven.

```bash
mvn clean verify          # 24 tests
mvn spring-boot:run       # http://localhost:8080/swagger-ui/index.html
```

Pour rejouer le point de départ : `git checkout v0-java11-boot2`.

## Auteur

**Mathieu Fenouil**, développeur full-stack Java / Angular
[LinkedIn](https://www.linkedin.com/in/mathieu-fenouil-développeur-full-stack/) · [GitHub](https://github.com/Matfen2)