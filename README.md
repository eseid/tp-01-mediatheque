# TP 1 — Premier jour de mission à la médiathèque

**Durée** : un après-midi (environ 3 h 30)
**Socle obligatoire** : tickets MED-101 à MED-105 · **Bonus** : MED-106 à MED-108

## Le contexte

Vous êtes consultant·e DevOps. La ville vous envoie en mission à la médiathèque
**Les Tilleuls**. Depuis février, trois développeurs (Camille, Yanis et Inès)
maintiennent un petit back-office Java qui gère les emprunts : quotas par adhérent,
dates de retour, pénalités de retard. Les règles métier sont décrites dans
[PROJET.md](PROJET.md).

Il n'y a aucune CI. Chacun lance les tests « quand il y pense », sur son poste Windows.

Le chef de projet vous a laissé ce message :

> Les tests passent chez tout le monde, et pourtant on a des bugs en production.
> Lundi dernier, Yanis jure que les tests étaient rouges alors que personne n'avait
> touché au code. Le lendemain, tout était vert. Et depuis la 1.3.0, une adhérente
> se plaint d'avoir payé une pénalité qu'elle ne devait pas.
>
> À partir de maintenant, plus rien n'arrive sur `main` sans avoir été vérifié.

Votre mission : mettre en place la CI qui rendra `main` digne de confiance. Les
tickets ci-dessous sont classés dans l'ordre de priorité du chef de projet.

### Comment lire un ticket

Chaque ticket donne un **objectif** et des **critères d'acceptation** : c'est ce qui
sera vérifié au débrief. Il ne donne pas les commandes à taper. Si vous bloquez plus
de 10 minutes, ouvrez les indices, un par un.

L'IA est autorisée. En revanche, au débrief, vous devrez expliquer **la cause** de
chaque problème avec vos propres mots, pas seulement la correction.

---

## Avant de commencer (15 min)

Choisissez **une** des trois options.

| | Option A — Windows | Option B — Linux (VM ou machine perso) | Option C — rien à installer |
|---|---|---|---|
| Java | JDK 21 [Temurin](https://adoptium.net/temurin/releases/?version=21) (`.msi`, cochez *Set JAVA_HOME*) | JDK 21 via votre gestionnaire de paquets ou [SDKMAN](https://sdkman.io) | GitHub Codespaces : bouton **Code → Codespaces → Create codespace** sur votre fork |
| Git | [Git for Windows](https://git-scm.com/download/win) (fournit **Git Bash**) | déjà présent | déjà présent |
| Terminal | PowerShell, ou Git Bash pour MED-108 | votre shell | le terminal de VS Code dans le navigateur |

Maven n'a pas besoin d'être installé : le projet embarque le **Maven Wrapper**
(`mvnw` / `mvnw.cmd`). Docker n'est pas nécessaire non plus : tout ce qui demande des
conteneurs tourne dans la CI.

Ensuite :

1. **Forkez** ce dépôt sur votre compte GitHub, puis clonez **votre fork**.
2. Dans votre fork, onglet **Actions** : cliquez sur *I understand my workflows, go
   ahead and enable them*. Sur un fork, les workflows sont désactivés par défaut.
3. Travaillez sur **une branche par ticket** et ouvrez une Pull Request vers le `main`
   **de votre fork**.

> ⚠️ Quand vous créez une Pull Request depuis un fork, GitHub propose par défaut de
> l'ouvrir vers le dépôt d'origine. Changez *base repository* pour choisir **votre fork**.

---

## MED-101 — Faire tourner le projet sur votre poste · 15 min

**Objectif** : lancer les tests sur votre poste et comprendre ce qui est testé.

```powershell
# Windows (PowerShell)
.\mvnw.cmd test
.\mvnw.cmd verify
```

```bash
# Linux, Git Bash, Codespaces
./mvnw test
./mvnw verify
```

**Critères d'acceptation**
- Vous savez combien de tests unitaires sont lancés, et dans quelles classes.
- Vous avez trouvé les tests d'intégration, et vous savez pourquoi ils ne
  s'exécutent pas sur votre poste.
- Vous avez noté le résultat **exact** (vert ou rouge, et quels tests).

Si un test est rouge chez vous et vert chez votre voisin, notez lequel, et
notez votre système et le jour de la semaine. C'est un indice pour la suite.

<details>
<summary>Indice</summary>

Regardez la différence entre la sortie de `test` et celle de `verify`, et
comparez les noms des classes de test (`*Test.java`, `*IT.java`). Ouvrez une
classe `*IT.java` : quelle annotation se trouve au-dessus de la classe ?
</details>

---

## MED-102 — Une CI à chaque push et à chaque Pull Request · 30 min

**Objectif** : créer un workflow GitHub Actions qui lance les tests unitaires à chaque
push sur `main` et à chaque Pull Request vers `main`.

**Critères d'acceptation**
- Le fichier s'appelle `.github/workflows/ci.yml` et le workflow s'appelle `CI`.
- Un job `tests-unitaires` tourne sur `ubuntu-latest`, avec Java 21 (Temurin).
- Il lance la même commande que vous en local.
- La CI est **verte**, **sans désactiver, supprimer ni modifier l'intention d'aucun test**.

Spoiler : la première exécution sera probablement rouge. C'est le but du ticket.

<details>
<summary>Indice 1</summary>

Lisez le message d'erreur **en entier** dans les logs du job. Quel fichier le code
cherche-t-il ? Quel fichier existe réellement dans le dépôt ?
</details>

<details>
<summary>Indice 2</summary>

Le runner GitHub est une machine Ubuntu. Qu'est-ce qui distingue le système de
fichiers de Linux de celui de Windows (ou de macOS) ?
</details>

<details>
<summary>Indice 3</summary>

`git log --follow --stat -- src/main/resources/` : qui a touché à ce fichier, quand, et pourquoi ?
</details>

**Question pour le débrief** : ce problème existe depuis juin. Pourquoi personne ne l'a vu ?

---

## MED-103 — « Les tests étaient rouges lundi » · 30 min

**Objectif** : trouver les tests qui ne donnent pas toujours le même résultat, et les
rendre **déterministes**.

Si vous faites ce TP un dimanche ou un lundi, vous avez peut-être déjà croisé ce
problème dès MED-101.

**Critères d'acceptation**
- Vous savez dire **quels jours** ces tests échouent, et pourquoi.
- Les tests donnent le même résultat quel que soit le jour où on les lance.
- La règle « retour reporté au mardi quand la médiathèque est fermée » est toujours
  testée, **via le service** (pas seulement via la méthode utilitaire).

<details>
<summary>Indice 1</summary>

Dans `ServiceEmpruntTest`, d'où vient la date attendue ? Et dans `ServiceEmprunt`,
d'où vient la date du jour ?
</details>

<details>
<summary>Indice 2</summary>

21, 14, 7 : qu'ont ces durées en commun ? Si on emprunte un mardi, quel jour tombe le retour ?
</details>

<details>
<summary>Indice 3</summary>

`java.time.Clock`. Le service ne doit pas aller chercher l'heure lui-même : on la
lui donne. En production, l'horloge système ; dans les tests, `Clock.fixed(...)`.
</details>

---

## MED-104 — Les tests d'intégration en CI, contre un vrai PostgreSQL · 40 min

**Objectif** : exécuter les tests d'intégration (`*IT.java`) dans la CI, contre une
vraie base PostgreSQL, sans rien installer sur votre poste.

**Critères d'acceptation**
- Un job `tests-integration` ne démarre **que si** `tests-unitaires` est vert.
- Une base PostgreSQL 17 démarre dans la CI, le temps du job.
- Les tests d'intégration sont **réellement exécutés**. Vérifiez dans les logs :
  `Tests run: 3, Failures: 0, Errors: 0, Skipped: 0`. Un job vert avec des tests
  ignorés ne compte pas.
- Ce job ne relance pas une deuxième fois les tests unitaires.

<details>
<summary>Indice 1</summary>

Dans la documentation GitHub Actions, cherchez « service containers » et « PostgreSQL ».
Pensez au *health check* : le job ne doit pas démarrer avant que la base soit prête.
</details>

<details>
<summary>Indice 2</summary>

Les variables attendues par les tests sont décrites dans [PROJET.md](PROJET.md).
Le `pom.xml` contient une propriété qui permet de ne lancer que les tests d'intégration.
</details>

**Questions pour le débrief** : pourquoi un vrai PostgreSQL plutôt qu'une base en
mémoire ? Que se passe-t-il si quelqu'un supprime la variable `MEDIATHEQUE_DB_URL`
du workflow ? La CI le détecte-t-elle ?

---

## MED-105 — Quality gate sécurité et protection de `main` · 35 min

**Objectif** : aucune dépendance avec une vulnérabilité connue **HIGH** ou
**CRITICAL** ne doit atteindre `main`, et personne ne peut merger une Pull Request
dont la CI est rouge.

**Critères d'acceptation**
- Un job `securite` analyse les dépendances avec **Trivy** et échoue en cas de
  vulnérabilité HIGH ou CRITICAL.
- Le job est rouge : vous identifiez la CVE et vous répondez à la question
  « est-ce que notre code est réellement exposé ? ». Puis vous corrigez proprement,
  par une montée de version, sans ignorer la CVE.
- Une règle de protection (*ruleset*) sur `main` impose une Pull Request et des
  checks verts.
- Preuve : une Pull Request avec un test volontairement cassé **ne peut pas** être
  mergée (capture d'écran dans votre `NOTES.md`).

<details>
<summary>Indice 1</summary>

L'action officielle est `aquasecurity/trivy-action`, avec le type de scan `fs`
(système de fichiers). C'est une action tierce qui s'exécute avec accès à votre
code : épinglez-la par **SHA de commit** plutôt que par tag.
</details>

<details>
<summary>Indice 2</summary>

*Settings → Rules → Rulesets → New branch ruleset*. Cherchez *Require status checks to pass*.
</details>

---

## MED-106 (bonus) — Une CI rapide, qui tourne aussi sous Windows · 25 min

**Objectif** : accélérer la CI et vérifier que le projet fonctionne sur les deux
systèmes utilisés par l'équipe.

**Critères d'acceptation**
- Vous notez la durée de la pipeline **avant** et **après** vos changements.
- Les dépendances Maven sont mises en cache entre deux exécutions.
- Les tests unitaires tournent sur `ubuntu-latest` **et** `windows-latest`.
- Un échec sur un système n'annule pas l'exécution sur l'autre.

<details>
<summary>Indice</summary>

`setup-java` sait gérer un cache Maven. Pour la matrice : `strategy.matrix`
et `fail-fast`. Sur un runner Windows, le shell par défaut est PowerShell.
</details>

**Question pour le débrief** : si la CI n'avait tourné que sous Windows, le bug
de MED-102 aurait-il été détecté ?

---

## MED-107 (bonus) — Des résultats de tests lisibles dans la Pull Request · 20 min

**Objectif** : publier le rapport des tests dans la Pull Request, pour ne plus avoir
à fouiller dans les logs.

**Critères d'acceptation**
- Sur une Pull Request, un rapport liste les tests par classe, avec leur statut.
- Les tests unitaires **et** les tests d'intégration y apparaissent.
- Un test en échec est visible en un clic.

<details>
<summary>Indice</summary>

Surefire et Failsafe produisent des rapports JUnit XML dans `target/`. L'action
`dorny/test-reporter` sait les lire (`java-junit`). Attention aux permissions du
`GITHUB_TOKEN`.
</details>

---

## MED-108 (défi) — L'enquête : la pénalité de Mme Lefèvre · 30 min

Ticket ouvert par l'accueil de la médiathèque :

> Mme Martine Lefèvre (adhérente adulte, carte A-1187) a emprunté *Le Petit Prince*
> (livre). Retour prévu le **mardi 13/10/2026**, rendu le **vendredi 16/10/2026** :
> 3 jours de retard, donc dans la tolérance. Elle a été facturée **0,20 €**.
> Personne ne sait depuis quand. La 1.0.0 avait été validée à la main par le chef de
> projet.

**Objectif** : reproduire le bug par un test, retrouver le commit qui l'a introduit,
puis corriger.

**Critères d'acceptation**
- Un test reproduit exactement le cas de Mme Lefèvre. Il est rouge avant la
  correction et vert après.
- Vous avez trouvé le commit fautif avec **`git bisect run`**, en une seule commande,
  sans tester les commits à la main.
- Le message de commit du correctif cite le commit fautif.
- Vous savez expliquer pourquoi les tests existants ne l'ont pas détecté.

<details>
<summary>Indice 1</summary>

Pendant le bisect, git va revenir sur d'anciens commits : votre test doit compiler
sur la version 1.0.0. N'utilisez que des classes qui existaient déjà, et construisez
la `GrilleTarifaire` vous-même (sans `charger()`).
</details>

<details>
<summary>Indice 2</summary>

Ne committez pas votre test avant le bisect : un fichier non suivi par git reste en
place quand git change de commit.
</details>

<details>
<summary>Indice 3</summary>

Dans Git Bash, Linux ou Codespaces :

```bash
git bisect start <mauvais> <bon>
git bisect run ./mvnw -q test -Dtest=<NomDeVotreTest>
git bisect reset
```
</details>

<details>
<summary>Indice 4</summary>

Relisez `CalculateurPenalitesTest`. Que vérifie **vraiment**
`un_retard_au_dela_de_la_tolerance_est_facture` ?
</details>

---

## Ce que vous rendez

- Le lien vers votre fork, avec le workflow final sur `main`.
- Un fichier `NOTES.md` à la racine : pour chaque ticket traité, 3 ou 4 lignes.
  Ce que vous avez trouvé, ce que vous avez changé, pourquoi, et ce qui vous a
  surpris.
