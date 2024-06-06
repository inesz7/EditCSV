![Version Gatling](https://img.shields.io/badge/Gatling-v3.10.5-orange?style=plastic&logo=gatling) 
![GitHub profile](https://img.shields.io/badge/inesz7-yellow?logo=github)

# Test de performances avec Gatling

![Gatling logo](https://cdn.worldvectorlogo.com/logos/gatling.svg)

## Table des matières

* [Gatling](#Gatling)
* [OWASP ZAP](#OWASP-ZAP)
* [Contributor](#Contributor)

## Gatling

Gatling est un outil open-source de test de performance conçu pour simuler des charges élevées sur des applications web afin d'évaluer leur comportement sous stress. Il permet de créer et d'exécuter des tests de charge pour identifier les points faibles, améliorer les performances et garantir la fiabilité des systèmes en production. La version utilisée pour réaliser les tests de performance dans ce projet est la version 3.10.5 de Gatling. Pour plus d'informations, veuillez consulter le [site officiel de Gatling](https://gatling.io/).

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

## OWASP ZAP

OWASP ZAP (Zed Attack Proxy) est un outil open-source de sécurité web conçu pour détecter les vulnérabilités dans les applications web. Il permet de scanner les applications pour identifier les failles de sécurité courantes et offre des fonctionnalités d'analyse dynamique pour tester la robustesse des systèmes en temps réel. Pour plus d'informations, veuillez consulter le [site officiel de ZAP](https://www.zaproxy.org/).

ZAP peut être utilisé en parallèle de Gatling pour scanner automatiquement une application et détecter les vulnérabilités de sécurité potentielles. L'intérêt de combiner ZAP à Gatling est que Gatling peut interagir avec des fonctionnalités avancées de l'application, telles qu'une page de connexion, permettant ainsi de scanner l'ensemble de l'application sans encombre.

### Installation

ZAP peut être téléchargé en local en suivant [ce lien](https://www.zaproxy.org/download/).

1. Téléchargement et installation :
   - Téléchargez ZAP à partir du lien fourni.
   - Suivez les instructions d'installation.

2. Execution de ZAP :
   - Lancez ZAP une fois que l'installation est terminée.
   - Laissez ZAP ouvert, il écoutera par défaut en proxy sur le port 8080.

3. Modification du code Gatling :
   - Modifiez le code de la simulation Gatling afin de décommentariser la ligne suivante dans la configuration des protocoles HTTP :
     ```
     .proxy(ProxyBuilder.proxyBuilder("localhost", 8080));
     ```
   - Exécutez Gatling et lancez le scénario *gatlingdemostoreapi*.
   - Les requêtes HTTP seront automatiquement captées par ZAP qui vérifiera les vulnérabilités de chacune d'entre elles.

4. Résultats :
   - Les résultats de la recherche de vulnérabilités sont disponibles directement sur l'application ZAP.

## Contributor

* [@inesz7](https://github.com/inesz7) 💻
