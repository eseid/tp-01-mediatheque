# Médiathèque Les Tilleuls — gestion des emprunts

Back-office de gestion des emprunts de la médiathèque : quotas par adhérent,
dates de retour, pénalités de retard.

## Installation

Prérequis : un JDK 21 (Temurin conseillé). Maven n'a pas besoin d'être installé,
le projet embarque le Maven Wrapper.

```bash
# Linux / macOS / Git Bash
./mvnw test

# Windows (PowerShell)
.\mvnw.cmd test
```

Le premier lancement télécharge Maven et les dépendances : c'est normal qu'il soit long.

## Règles métier

### Durée des emprunts

| Type  | Durée    |
|-------|----------|
| Livre | 21 jours |
| BD    | 14 jours |
| DVD   | 7 jours  |

La médiathèque est fermée le **dimanche et le lundi** : si la date de retour tombe
un de ces jours, elle est reportée au mardi suivant.

### Quotas (emprunts simultanés)

| Catégorie | Quota |
|-----------|-------|
| Adulte    | 5     |
| Étudiant  | 8     |
| Enfant    | 3     |

### Pénalités de retard

- **Tolérance de 3 jours** : un document rendu avec 3 jours de retard ou moins
  n'entraîne aucune pénalité.
- Au-delà, seuls les jours **au-delà de la tolérance** sont facturés.
  Exemple : un livre rendu avec 5 jours de retard → 2 jours facturés → 0,40 €.
- Le montant est plafonné à **5,00 € par emprunt**.
- Tarifs par jour : voir `src/main/resources/penalites.properties`.
