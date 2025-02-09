# Contrôle du debugger en ligne de commande

> **Auteurs** : Gatien BERTIN, Mathurin HAUVILLE  
> **Date** : 5 février 2025
>

## Lancer le debugger

1. Lancer le programme [JDISimpleDebugger.java](src/main/java/com/ubo/debug/JDISimpleDebugger.java)
2. Copier le fichier [breakpoints.txt](src/main/resources/breakpoints.txt) dans le répertoire `target/classes`
3. Configurer pour exécuter le programme depuis le répertoire `target/classes`
4. Exécuter le programme avec la configuration créée à l'étape précédente

> [!NOTE]
> Par défaut un breakpoint est placé à la ligne 8 de la classe `JDISimpleDebuggee`.
> Vous pouvez le modifier en éditant le fichier `breakpoints.txt`

## Fonctionnement

Le debugger utilise le pattern commande pour permettre à l'utilisateur de contrôler le debugger.

### JDISimpleDebugger

Le programme se lance depuis la classe [JDISimpleDebugger](src/main/java/com/ubo/debug/JDISimpleDebugger.java). Elle
crée une nouvelle instance de [ScriptableDebugger](src/main/java/com/ubo/debug/ScriptableDebugger.java) et attache la
classe [JDISimpleDebuggee](src/main/java/com/ubo/debug/JDISimpleDebuggee.java) qui va être utilisé pour tester le
debugger

### ScriptableDebugger

La classe [ScriptableDebugger](src/main/java/com/ubo/debug/ScriptableDebugger.java) garde la même structure que celle
initiale. Dans la méthode `startDebugger()`, lorsque l'événement est du type `ClassPrepareEvent`, on charge le fichier
de breakpoints. Si un évenement est de type `BreakpointEvent` ou bien `StepEvent`, alors on
lance
l'interpréteur de commande qui permet à l'utilisateur de contrôler le debugger.

### CommandInterpreter

La classe [CommandInterpreter](src/main/java/com/ubo/debug/CommandInterpreter.java) permet à l'utilisateur de contrôler
le debugger avec les commandes disponibles. Elle assigne l'ensemble des commandes à un mot clé qui devra être saisi par
l'utilisateur pour déclencher la commande en question.

### BreakpointManager

La classe [BreakpointManager](src/main/java/com/ubo/debug/BreakpointManager.java) permet de gérer les breakpoints. Elle
contient l'ensemble des méthodes relatives à la gestion des breakpoints.

## Commandes

### step-back

- Demande à l'utilisateur un nombre de pas à revenir en arrière.
- Sauvegarde la valeur du PC actuel.
- Redémarre la vm.
- Enregistre le PC avec la valeur sauvegardée moins le nombre de pas à revenir en arrière.
- La méthode `initStepWithPC()` de la classe [ScriptableDebugger](src/main/java/com/ubo/debug/ScriptableDebugger.java)
  exécute la commande `step` pour revenir à la position souhaitée.

### step-back-one

Commande `step-back` avec un pas en arrière.

### pc

Affiche la valeur du PC actuel.

### break

Ajoute un breakpoint à la ligne demandée et ajoute ce breakpoint dans le fichier breakpoints.txt pour retrouver les
breakpoints à chaque lancement.

### break-once

Ajoute un breakpoint à la ligne demandée sans l'ajouter dans le fichier breakpoints.txt.

### continue

Continue l'exécution du programme jusqu'au prochain breakpoint. Utilise la commande `step` pour avancer jusqu'au prochain breakpoint.

### step

### step-over

### frame

### temporaries

### stack

### receiver

### sender

### receiver-variables

### method

### arguments

### print-var

### breakpoints

### break-before-method-call

### break-on-count

N'est pas implémentée.

