# Projet données réparties

- [Projet données réparties](#projet-données-réparties)
  - [Difficultés potentielles identifiées](#difficultés-potentielles-identifiées)
  - [Tests envisagés](#tests-envisagés)
    - [Tests unitaires sur la classe Tuple](#tests-unitaires-sur-la-classe-tuple)
    - [Tests unitaires plus généraux sur Linda](#tests-unitaires-plus-généraux-sur-linda)
  - [Répartition du travail pour le projet de données réparties](#répartition-du-travail-pour-le-projet-de-données-réparties)
    - [Présentation](#présentation)
    - [Rôles](#rôles)
    - [À Faire](#à-faire)
      - [1.1.1 Implantation basique de mémoire partagée](#111-implantation-basique-de-mémoire-partagée)
      - [1.1.2 Implantation multitâche](#112-implantation-multitâche)
      - [1.2 Implantation serveur](#12-implantation-serveur)
      - [2.1 Applications](#21-applications)

## Difficultés potentielles identifiées

- mise en place du multithreading
- réutilisabilité du code de la première version pour la deuxième implantation

## Tests envisagés

### Tests unitaires sur la classe Tuple

- tests de la méthode `matches` pour divers Tuples motifs, contenant divers types communément utilisés
- tests de la méthode `contains`
- test de `deepclone`, en effectuant des modifications sur la copie, et en s'assurant qu'il n'y a pas d'effets de bords
- test de `toString`
- tests de `valueOf` pour des entrées valides et invalides

### Tests unitaires plus généraux sur Linda

Ces tests devront être exécutés sur les deux versions de Linda produites (*c'est-à-dire* en mémoire partagée et clients-serveur).

- Tests de lecture bloquante et non bloquante, supprimant l'élément du `tuplespace` ou non (méthode `read`, `take`, `tryRead`, `tryTake`)
- test d'écriture (méthode `write`)
- tests d'écriture / lecture concurrente par deux clients au même moment (méthodes précédentes appliquées à deux clients synchronisés)
- test d'enregistrement de `callbacks` (méthode `eventRegister`)
- tests d'écriture / lecture par un client pendant l'exécution d'un `callback` (méthodes précédentes appliquées à un client synchronisé avec le serveur)

## Répartition du travail pour le projet de données réparties

### Présentation

Nous avons vu la répartition du temps de travail de la manière suivante :

- 2 personnes codent "le dur" (les implémentations)
- 1 personne est chargée de faire le lien entre les deux autres, faire les tests, le rapport, la présentation... et peut aider les deux autres

La répartition des tâches est faite de manière que tout le monde puisse travailler en parallèle, même si cela n'est pas possible tout le temps (car certaines tâches sont du type `finish to start` : une tâche doit finir pour que l'autre commence).

### Rôles

- 👤 *rôle 1* : **Ying LIU** ([yingliu126](https://github.com/yingliu126))
- 👤 *rôle 2* : **Philippe NEGREL-JERZY** ([l3alr0g](https://github.com/l3alr0g))
- 👤 *rôle 3* : **Sébastien PONT** ([seba1204](https://github.com/seba1204))

### À Faire

#### 1.1.1 Implantation basique de mémoire partagée

- *1.1.1.a*
  - ✏️ implantation des 9 fonctions (`read`, `write`, ...)
  - 👤 n°2
- *1.1.1.b*
  - ✏️ écriture de tests pour Linda `shm`
  - 👤 n°3
- *1.1.1.c* ✏️ commencer le rapport
  - 👤 n°3

#### 1.1.2 Implantation multitâche

- *1.1.2.a*
  - ✏️ mise à jour de l'implantation basique pour le multitâche
  - 👤 n°2
- *1.1.2.b*
  - ✏️ faire des tests de performances pour mettre en évidence et compléter le rapport
  - 👤 n°3

#### 1.2 Implantation serveur

- *1.2.a*
  - ✏️ implémenter la version serveur de Linda
  - 👤 n°2, 3
- *1.2.b*
  - ✏️ faire des tests unitaires pour cette version
  - 👤 n°3, 1
- *1.2.c*
  - ✏️ compléter le rapport et la documentation
  - 👤 n°1

#### 2.1 Applications

- *2.1.a*
  - ✏️ implémenter l'exemple des nombres premiers
  - 👤 n°3, 1
- *2.1.b*
  - ✏️ implémenter l'exemple de recherche
  - 👤 n°2, 1
- *2.1.c*
  - ✏️ faire le rapport et la présentation
  - 👤 n°1
