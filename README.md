# TP 1 - Premier jour de mission à la médiathèque

**Durée** : un après-midi (environ 3 h 30)
**Socle obligatoire** : tickets MED-101 à MED-105 · **Bonus** : MED-106

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
> touché au code. Le lendemain, tout était vert.
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

## Avant de commencer (15 min)

Choisissez **une** des trois options.

| | Option A - Windows | Option B - Linux (VM ou machine perso) | Option C - rien à installer |
|---|---|---|---|
| Java | JDK 21 [Temurin](https://adoptium.net/temurin/releases/?version=21) (`.msi`, cochez *Set JAVA_HOME*) | JDK 21 via votre gestionnaire de paquets ou [SDKMAN](https://sdkman.io) | GitHub Codespaces : bouton **Code → Codespaces → Create codespace** sur votre fork |
| Git | [Git for Windows](https://git-scm.com/download/win) (fournit **Git Bash**) | déjà présent | déjà présent |
| Terminal | PowerShell (ou Git Bash) | votre shell | le terminal de VS Code dans le navigateur |

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

## MED-101 - Faire tourner le projet sur votre poste · 15 min

**Objectif** : lancer les tests sur votre poste et comprendre ce qui est testé.

**Windows (PowerShell)**

```powershell
.\mvnw.cmd test
.\mvnw.cmd verify
```

**Linux, macOS, Git Bash, Codespaces**

```bash
./mvnw test
./mvnw verify
```

Le premier lancement télécharge Maven et les dépendances : comptez une à deux minutes.

> **Ce que vous devez obtenir**
> - `test` lance les **tests unitaires** : 17 tests, `BUILD SUCCESS`.
> - `verify` relance les tests unitaires, construit le jar, puis lance les **tests
>   d'intégration** : `Tests run: 3, Failures: 0, Errors: 0, Skipped: 3`, et quand même
>   `BUILD SUCCESS`.
>
> Ces 3 tests *skipped* (ignorés) sont attendus sur votre poste. Ne passez pas à la
> suite sans savoir **pourquoi** : c'est le deuxième critère ci-dessous, et vous en
> aurez besoin dans MED-104.

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

## MED-102 - Une CI à chaque push et à chaque Pull Request · 30 min

**Objectif** : créer un workflow GitHub Actions qui lance les tests unitaires à chaque
push sur `main` et à chaque Pull Request vers `main`.

**Critères d'acceptation**
- Le fichier s'appelle `.github/workflows/ci.yml` et le workflow s'appelle `CI`.
- Un job `tests-unitaires` tourne sur `ubuntu-latest`, avec Java 21 (Temurin).
- Il lance la même commande que vous en local.
- La CI est **verte**, **sans désactiver, supprimer ni modifier l'intention d'aucun test**.

C'est votre premier workflow, alors ce ticket est guidé. Les suivants ne le seront plus :
vous réutiliserez ce que vous construisez ici.

### Partie 1 - Construire le workflow

**Le vocabulaire du matin, appliqué au projet**

| Notion | Dans ce ticket |
|---|---|
| Workflow | le fichier `.github/workflows/ci.yml` |
| Événement (`on`) | un push sur `main`, une Pull Request vers `main` |
| Job | `tests-unitaires` : une suite d'étapes exécutées sur la même machine |
| Runner | la machine qui exécute le job : une VM Ubuntu neuve, prêtée par GitHub et détruite à la fin |
| Step | une étape du job : une commande (`run`) ou une action (`uses`) |
| Action | une brique réutilisable, publiée par quelqu'un d'autre (`actions/checkout`, `actions/setup-java`…) |

**Étape 1 : créer une branche pour le ticket**

```
git switch -c med-102-ci
```

**Étape 2 : créer le fichier `.github/workflows/ci.yml` et compléter les `???`**

```yaml
name: CI

on:
  push:
    branches: [ ??? ]
  pull_request:
    branches: [ ??? ]

jobs:
  tests-unitaires:
    runs-on: ???
    steps:
      # Le runner démarre vide : il faut d'abord y récupérer le code du dépôt
      - uses: actions/checkout@v7

      # Puis installer le JDK
      - uses: actions/setup-java@v6
        with:
          distribution: ???
          java-version: ???

      # Enfin, lancer les tests unitaires
      - name: Tests unitaires
        run: ???
```

Points d'attention :
- En YAML, **l'indentation fait partie de la syntaxe**. Utilisez des espaces, jamais de
  tabulations.
- Le dossier s'appelle `.github/workflows`, avec un **s** et un point au début.
- Le runner est une machine **Linux**. Choisissez la bonne variante du Maven Wrapper.
- Ajoutez l'option `-B` (*batch mode*) à la commande Maven. Sans elle, les logs sont
  pollués par les barres de progression des téléchargements.

**Étape 3 : pousser et ouvrir la Pull Request**

```
git add .github/workflows/ci.yml
git commit -m "MED-102 : CI sur push et PR"
git push -u origin med-102-ci
```

Ouvrez ensuite une Pull Request de `med-102-ci` vers le `main` **de votre fork**.

**Étape 4 : regarder la CI tourner**

En bas de la Pull Request, la section *Checks* affiche le workflow `CI`. Vous le retrouvez
aussi dans l'onglet **Actions**. Cliquez sur le job `tests-unitaires`, puis dépliez
chaque step pour lire ses logs.

✅ **Point d'étape** : un run `CI` apparaît sur votre Pull Request et ses steps
s'exécutent, dans l'ordre.

<details>
<summary>Aucun run n'apparaît ?</summary>

- Les workflows sont-ils activés dans l'onglet **Actions** de votre fork ?
- Le chemin est-il exactement `.github/workflows/ci.yml` ?
- La PR vise-t-elle bien le `main` de **votre fork**, et pas le dépôt d'origine ?
- Si le YAML est invalide, l'onglet **Actions** affiche l'erreur en haut du run, avec le
  numéro de ligne.
</details>

### Partie 2 - Faire passer la CI au vert

La première exécution sera probablement rouge. C'est le but du ticket. Le code n'a pas
changé depuis tout à l'heure, et les tests étaient verts sur votre poste. Qu'est-ce qui
est différent sur le runner ?

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

<details>
<summary>Indice 4 - vous avez renommé un fichier, mais <code>git status</code> ne voit rien ?</summary>

Windows et macOS ne distinguent pas les majuscules dans les noms de fichiers. Quand
seule la casse change, Git ne voit souvent aucune différence. Renommez en deux fois,
en passant par un nom temporaire :

```
git mv src/main/resources/Ancien.properties src/main/resources/temp.properties
git mv src/main/resources/temp.properties src/main/resources/nouveau.properties
```
</details>

**Question pour le débrief** : ce problème existe depuis juin. Pourquoi personne ne l'a vu ?

## MED-103 - « Les tests étaient rouges lundi » · 30 min

> **Message de Yanis sur le canal de l'équipe** : « Lundi matin, trois tests de
> `ServiceEmpruntTest` étaient rouges. Mardi, je les ai relancés sans rien changer et
> ils sont passés. Quelqu'un comprend ? »

Un test qui passe ou échoue selon le moment où on le lance, sans que le code ait changé,
s'appelle un **test flaky** (instable). Dans une pipeline, c'est un poison : on finit
par relancer la CI « pour voir », et on ne fait plus confiance au rouge.

**Objectif** : comprendre pourquoi ces tests échouent certains jours, puis les rendre
**déterministes** : même code, même résultat, quel que soit le jour.

**Critères d'acceptation**
- Vous savez dire **quels jours** ces tests échouent, et pourquoi.
- Les tests donnent le même résultat quel que soit le jour où on les lance.
- Un test vérifie que **`emprunter()`**, appelé un lundi, reporte le retour au mardi.
  Aujourd'hui, cette règle n'est testée que sur la petite méthode
  `premierJourDOuverture`, jamais sur le vrai parcours d'un emprunt.

### Étape 1 - Comprendre, sans attendre lundi

Vous faites sans doute ce TP un autre jour : vos tests sont verts. Vous allez donc
trouver le bug **en lisant le code**, pas en le voyant échouer.

Ouvrez `ServiceEmprunt.java` et `ServiceEmpruntTest.java`, puis complétez ce tableau
pour un **livre** (durée d'emprunt : 21 jours) :

| L'emprunt a lieu le… | Date de retour calculée par le service | Date attendue par le test | Vert ou rouge ? |
|---|---|---|---|
| jeudi 26 novembre 2026 | | | |
| lundi 23 novembre 2026 | | | |

Que remarquez-vous sur 21, 14 et 7 jours ?

### Étape 2 - Deux outils Java à connaître

**`LocalDate.now()`** renvoie la date du jour, lue sur l'horloge de la machine. C'est
pratique, mais un code qui l'appelle donne un résultat différent chaque jour. Le test
ne peut pas savoir à l'avance quel jour on sera.

```java
LocalDate aujourdhui = LocalDate.now();   // 26/11 aujourd'hui, 27/11 demain…
```

**`java.time.Clock`** est un objet qui représente « l'horloge » à laquelle on demande
l'heure. Au lieu de laisser le code lire l'horloge de la machine, on **lui passe une
horloge** :

```java
// En production : l'horloge réelle de la machine
Clock horloge = Clock.systemDefaultZone();

// Dans un test : une horloge arrêtée, qui indique toujours le lundi 23 novembre 2026
ZoneId paris = ZoneId.of("Europe/Paris");
Clock horloge = Clock.fixed(LocalDate.of(2026, 11, 23).atStartOfDay(paris).toInstant(), paris);

// Partout où le code a besoin de la date du jour :
LocalDate aujourdhui = LocalDate.now(horloge);
```

`LocalDate.now(horloge)` renvoie la date **de l'horloge qu'on lui donne**. Avec
l'horloge réelle, c'est aujourd'hui. Avec l'horloge figée, c'est toujours le 23 novembre,
quel que soit le jour où le test tourne.

### Étape 3 - Corriger et prouver

1. Modifiez `ServiceEmprunt` pour qu'il **reçoive** une `Clock` (par son constructeur)
   au lieu d'appeler `LocalDate.now()` tout seul. Le reste de l'application doit
   continuer à fonctionner avec l'horloge réelle.
2. Dans `ServiceEmpruntTest`, créez le service avec une horloge figée sur une date que
   vous choisissez, et attendez une date **écrite en dur** : plus de `LocalDate.now()`
   dans les tests.
3. Ajoutez le test du troisième critère : horloge figée un lundi, emprunt d'un livre,
   retour attendu le mardi.

<details>
<summary>Indice 1</summary>

Gardez deux constructeurs dans `ServiceEmprunt` : celui qui existe déjà, qui utilise
l'horloge réelle, et un nouveau qui reçoit la `Clock`. Le code existant n'a ainsi rien
à changer.
</details>

<details>
<summary>Indice 2</summary>

Lundi 23 novembre 2026 + 21 jours = lundi 14 décembre 2026. La médiathèque est fermée
le lundi : quelle date le service doit-il renvoyer ?
</details>

**Question pour le débrief** : quelles autres sources de « hasard » peuvent rendre un
test flaky ? (Pensez au fuseau horaire, à l'ordre d'exécution des tests, au réseau…)

## MED-104 - Les tests d'intégration en CI, contre un vrai PostgreSQL · 40 min

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

## MED-105 - Quality gate sécurité et protection de `main` · 35 min

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

## MED-106 (bonus) - Une CI rapide, qui tourne aussi sous Windows · 25 min

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

## Ce que vous rendez

- Le lien vers votre fork, avec le workflow final sur `main`.
- Un fichier `NOTES.md` à la racine : pour chaque ticket traité, 3 ou 4 lignes.
  Ce que vous avez trouvé, ce que vous avez changé, pourquoi, et ce qui vous a
  surpris.
