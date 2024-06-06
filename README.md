![Version Gatling](https://img.shields.io/badge/Gatling-v3.10.5-orange?style=plastic&logo=gatling) 
![GitHub profile](https://img.shields.io/badge/inesz7-yellow?logo=github)

# Test de performances avec Gatling

![Gatling logo](https://cdn.worldvectorlogo.com/logos/gatling.svg)

## Table des matières

* [Gatling](#Gatling)
* [Contributors](#Contributors)
* [Contact](#Contact)

## Gatling

Gatling est un outil open-source de test de performance conçu pour simuler des charges élevées sur des applications web afin d'évaluer leur comportement sous stress. Il permet de créer et d'exécuter des tests de charge pour identifier les points faibles, améliorer les performances et garantir la fiabilité des systèmes en production. La version utilisé pour réaliser les tests de performances dans ce projet est la version 3.10.5 de Gatling. Pour plus d'informations veuillez consulter le [site officiel de Gatling](https://gatling.io/).

### Installation

Gatling peut être téléchargé en local en suivant [ce lien](https://docs.gatling.io/reference/install/oss/).

1. Téléchargement et extraction :
   - Téléchargez Gatling à partir du lien fourni.
   - Une fois le téléchargement terminé, extrayez le contenu du fichier ZIP à l'emplacement souhaité.

2. Ajout de la simulation :
   - Téléchargez le code de la simulation *gatlingdemostoreapi*.
   - Collez le fichier de simulation dans le dossier `gatling-charts-highcharts-bundle-3.10.5/user-files/simulations`.

3. Exécution de Gatling :
   - Lancez Gatling en exécutant le fichier situé dans le dossier `gatling-charts-highcharts-bundle-3.10.5/bin` :
     - `.bat` pour Windows
     - `.sh` pour Linux

4. Sélection de la simulation :
   - Une console de commande s'ouvrira. Tapez `1` pour sélectionner **run a simulation locally**.
   - Ensuite, tapez le numéro correspondant à la simulation *gatlingdemostoreapi*.

5. Visualisation des résultats :
   - Une fois la simulation terminée, un lien sera fourni pour accéder aux résultats.
   - Un fichier de résultats est également disponible dans le dossier `gatling-charts-highcharts-bundle-3.10.5/results`.

### Simulation

La simulation disponible dans ce projet est un test de performance pour l'application *[demostore](https://demostore.gatling.io/)* testant un scénario basique que vont suivre tout les utilisateurs créés.
