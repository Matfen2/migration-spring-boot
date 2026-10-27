# Migration Java 11 / Spring Boot 2 → Java 17 / Spring Boot 3

Projet personnel, conçu pour montrer une compétence recherchée en mission : **faire évoluer une application existante** sans changer son comportement.

Le point de départ est une petite API de **gestion du stock de pièces détachées** d'un service maintenance, écrite comme on l'écrivait en 2021 : Java 11, Spring Boot 2.7, `javax`, `WebSecurityConfigurerAdapter`, Springfox. Elle est migrée étape par étape vers Java 17 et Spring Boot 3, **une étape par commit**, avec une suite de tests qui doit rester verte du début à la fin.

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

## La migration

| Étape | Contenu | Tag |
| --- | --- | --- |
| 0 | État des lieux : Java 11, Spring Boot 2.7, 24 tests verts | `v0-java11-boot2` |
| 1 | Java 17 : compilation, puis records, switch expressions, pattern matching, text blocks | `v1-java17-boot2` |
| 2 | Spring Boot 3.5 : `javax` → `jakarta`, Spring Security 6, Springfox → springdoc, propriétés renommées, changements de comportement détectés par les tests | `v2-java17-boot3` |

Le guide détaillé et le journal des problèmes rencontrés sont dans [`MIGRATION.md`](MIGRATION.md).

## Lancer le projet

Prérequis : un **JDK 17** et Maven (voir [`MIGRATION.md`](MIGRATION.md#prérequis--un-jdk-17)).

```bash
mvn clean verify          # 24 tests
mvn spring-boot:run       # http://localhost:8080/swagger-ui/
```

## Auteur

**Mathieu Fenouil**, développeur full-stack Java / Angular
[LinkedIn](https://www.linkedin.com/in/mathieu-fenouil-développeur-full-stack/) · [GitHub](https://github.com/Matfen2)
