#  TechRent - Gestion de Location de Matériel IT

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-blue?style=for-the-badge)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

**TechRent Pro** est une application de bureau complète conçue pour gérer le cycle de vie de la location de matériel informatique (PC, projecteurs, serveurs, etc.). Elle permet de gérer les stocks, les clients, les contrats de location et le suivi de maintenance via une interface moderne et intuitive.

---

##  Aperçu

| Tableau de Bord | Planning des Locations |
|:---:|:---:|
| ![Dashboard](https://via.placeholder.com/400x250.png?text=Dashboard+TechRent) | ![Planning](https://via.placeholder.com/400x250.png?text=Calendrier+Visuel) |
| *Statistiques en temps réel et alertes* | *Vue calendrier interactive (CalendarFX)* |

---

##  Fonctionnalités Clés

### 1. Gestion du Matériel 
* Inventaire complet avec photos des produits.
* Suivi des états en temps réel : `DISPONIBLE`, `EN LOCATION`, `EN PANNE`, `EN MAINTENANCE`.
* Module de maintenance dédié pour gérer les réparations et mises au rebut.

### 2. Gestion des Locations 
* **Système Anti-Conflit :** Vérification automatique des chevauchements de dates pour éviter les doubles réservations.
* **Calendrier Visuel :** Vue globale des locations passées, en cours et futures.
* **Facturation :** Calcul automatique du coût total et gestion des pénalités de retard (Tarif x 2).

### 3. Gestion des Clients 
* Base de données clients complète.
* Historique des locations par client.

### 4. Rapports & Exports 
* Tableau de bord avec KPI (Revenu estimé, Retards, Taux d'occupation).
* Génération de PDF pour les contrats ou les listes d'inventaire.

---

##  Architecture Technique

Le projet respecte scrupuleusement l'architecture en couches **MVC / Service / DAO** pour garantir une maintenance facile et une séparation des responsabilités.



[Image of Layered Software Architecture]


* **Vue (Presentation) :** FXML + JavaFX Controller. Interface moderne avec le thème *AtlantaFX (Cupertino Dark)*.
* **Service (Métier) :** Contient toute la logique (calculs de prix, vérification de disponibilité, règles de gestion).
* **DAO (Data Access) :** Gestion des transactions avec la base de données via **Hibernate** (ORM).
* **Modèle :** Entités JPA mappées à la base de données.

---

##  Installation & Démarrage

### Prérequis
* **Java JDK 21** (ou supérieur).
* **MySQL** ou **SQL Server** (WAMP/XAMPP recommandé pour MySQL).
* **Maven** (pour la gestion des dépendances).

### Étapes d'installation

1.  **Cloner le projet :**
    

2.  **Configuration de la Base de Données :**
    * Créez une base de données vide nommée `TechRentDB`.
    * Si vous avez le script SQL fourni (`database_setup.sql`), exécutez-le. Sinon, Hibernate créera les tables au premier lancement.
    * Modifiez le fichier `src/main/resources/hibernate.cfg.xml` avec vos identifiants :
        ```xml
        <property name="connection.username">root</property>
        <property name="connection.password">votre_mot_de_passe</property>
        ```

3.  **Lancer l'application :**
    * Via IntelliJ / Eclipse : Exécutez la classe `com.techrent.App`.
    * **Login par défaut :**
        * Utilisateur : `admin`
        * Mot de passe : `admin123`

---

##  Bibliothèques Utilisées

* **JavaFX :** Framework GUI.
* **Hibernate ORM :** Gestion de la persistance des données.
* **CalendarFX :** Composant de calendrier professionnel.
* **AtlantaFX :** Thèmes modernes pour JavaFX.
* **iText / PDFBox :** Génération de rapports PDF.
* **Lombok :** Réduction du code boilerplate (Getters/Setters).

---
