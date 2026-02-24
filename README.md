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
