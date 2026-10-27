package fr.stockpieces.erreur;

/**
 * Corps des réponses d'erreur de l'API.
 *
 * @param code    code stable, exploitable par les clients
 * @param message message lisible
 */
public record Erreur(String code, String message) {
}