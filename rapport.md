## Difficultés potentielles identifiées
- mise en place du multithreading
- réutilisabilité du code de la première version pour la deuxième implantation

## Tests envisagés

### Tests unitaire sur la classe Tuple
- tests de la méthode `matches` pour divers Tuples motifs, contenant divers types communément utilisés
- tests de la méthode `contains`
- test de `deepclone`, en effectuant des modifications sur la copie, et en s'assurant qu'il n'y a pas d'effets de bords
- test de `toString` 
- tests de `valueOf` pour des entrées valides et invalides

### Tests unitaires plus généraux sur Linda

Ces tests devront être exécutés sur les deux versions de Linda produites (*ie* en mémoire partagée et clients-serveur).

- tests de lecture bloquante et non bloquante, supprimant l'élément du tuplespace ou non (méthode `read`, `take`, `tryRead`, `tryTake`)
- test d'écriture (méthode `write`)
- tests d'écriture / lecture concurrente par deux clients au même moment (méthodes précédentes appliquées à deux clients synchronisés)
- test d'enregistrement de callbacks (méthode `eventRegister`)
- tests d'écriture / lecture par un client pendant l'exécution d'un callback (méthodes précédentes appliquées à un client synchonisé avec le serveur)
