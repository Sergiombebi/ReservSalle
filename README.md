# 🏢 Système de Réservation de Salles - ReservSalle

Un système complet de gestion des réservations de salles et des ressources matérielles, développé en Java avec une interface graphique Swing moderne et professionnelle.

## 📋 Table des Matières

- [Aperçu du Projet](#aperçu-du-projet)
- [Fonctionnalités](#fonctionnalités)
- [Architecture](#architecture)
- [Installation](#installation)
- [Utilisation](#utilisation)
- [Base de Données](#base-de-données)
- [Captures d'Écran](#captures-décran)
- [Développement](#développement)
- [Contribution](#contribution)

## 🎯 Aperçu du Projet

Le système ReservSalle permet de gérer efficacement les réservations de salles et les ressources matérielles dans un environnement universitaire ou d'entreprise. Il offre une interface intuitive pour les différents types d'utilisateurs (administrateurs, demandeurs, responsables) avec des fonctionnalités adaptées à leurs besoins.

### 🌟 Caractéristiques Principales

- **Interface moderne** avec design professionnel
- **Gestion multi-rôles** (Admin, Demandeur, Responsable)
- **Système de validation** des réservations
- **Gestion des conflits** automatique
- **Calendrier visuel** des réservations
- **Gestion des ressources** matérielles
- **Historique complet** des actions

## 🚀 Fonctionnalités

### 👥 Gestion des Utilisateurs
- **Authentification sécurisée** avec gestion des rôles
- **Trois types d'utilisateurs** :
  - **Administrateur** : Gestion complète du système
  - **Demandeur** : Création et suivi des réservations
  - **Responsable** : Validation des demandes

### 🏢 Gestion des Salles
- **Création et modification** des salles
- **Attribution des ressources** par salle
- **Gestion de la capacité** et des types de salles
- **Localisation** et descriptions détaillées

### 📦 Gestion des Ressources
- **Vidéoprojecteurs, PC, systèmes audio**, etc.
- **Gestion des quantités** disponibles
- **États des ressources** (disponible, en panne, maintenance)
- **Vérification automatique** des disponibilités

### 📅 Système de Réservation
- **Réservation intuitive** avec sélection de créneaux
- **Vérification des conflits** en temps réel
- **Validation par les responsables**
- **Cycle de vie complet** : En attente → Validée/Rejetée
- **Historique détaillé** des réservations

### 🗓️ Calendrier Visuel
- **Vue mensuelle** des réservations
- **Filtrage par salle** ou toutes les salles
- **Codes couleurs** pour les différents états
- **Navigation intuitive** par mois/année

## 🏗️ Architecture

Le projet suit une architecture MVC (Model-View-Controller) avec les couches suivantes :

```
src/
├── Main/
│   └── Main.java                    # Point d'entrée de l'application
├── model/                           # Modèles de données
│   ├── Utilisateur.java
│   ├── Salle.java
│   ├── Ressource.java
│   ├── Reservation.java
│   ├── ReservationRessource.java
│   ├── RoleUtilisateur.java         # Enum pour les rôles
│   ├── EtatReservation.java         # Enum pour les états
│   └── EtatRessource.java           # Enum pour les états des ressources
├── dao/                             # Couche d'accès aux données
│   ├── DatabaseManager.java
│   ├── UtilisateurDAO.java
│   ├── SalleDAO.java
│   ├── RessourceDAO.java
│   └── ReservationDAO.java
├── view/                            # Interfaces utilisateur
│   ├── ConnexionView.java           # Écran de connexion
│   ├── AccueilAdminView.java        # Tableau de bord administrateur
│   ├── DemandeurReservationView.java # Interface demandeur
│   ├── ResponsableDashboardView.java # Interface responsable
│   ├── ReservationValidationView.java # Validation des réservations
│   ├── CalendrierView.java          # Calendrier visuel
│   └── SalleGestionView.java        # Gestion des salles
└── controller/
    └── NavigationController.java    # Contrôleur de navigation
```

## 🛠️ Installation

### Prérequis
- **Java 8 ou supérieur**
- **MySQL 5.7 ou supérieur**
- **IDE Java** (IntelliJ IDEA, Eclipse, etc.)

### Étapes d'Installation

1. **Cloner le projet**
   ```bash
   git clone [url-du-projet]
   cd ReservSalle
   ```

2. **Configurer la base de données**
   ```bash
   # Créer la base de données MySQL
   mysql -u root -p < database_schema.sql
   ```

3. **Configurer la connexion**
   Modifier le fichier `src/dao/DatabaseManager.java` :
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/reservsalle";
   private static final String USER = "votre_utilisateur";
   private static final String PASSWORD = "votre_mot_de_passe";
   ```

4. **Ajouter le driver MySQL**
   - Télécharger `mysql-connector-j-9.2.0.jar`
   - L'ajouter au classpath du projet

5. **Compiler et exécuter**
   ```bash
   javac -cp ".:mysql-connector-j-9.2.0.jar" src/Main/Main.java
   java -cp ".:mysql-connector-j-9.2.0.jar:src" Main.Main
   ```

## 🎮 Utilisation

### Connexion au Système

1. **Lancer l'application**
2. **Choisir votre rôle** : Admin, Demandeur, ou Responsable
3. **Saisir vos identifiants**

#### Comptes par Défaut
- **Admin** : `admin` / `admin123`
- **Demandeur** : `jean.dupont@exemple.com` / `password123`
- **Responsable** : `pierre.durand@exemple.com` / `password123`

### Interface Administrateur

#### Gestion des Utilisateurs
- **Ajouter** de nouveaux utilisateurs
- **Modifier** les informations utilisateur
- **Supprimer** des comptes
- **Gérer les rôles** et permissions

#### Gestion des Salles
- **Créer** de nouvelles salles
- **Définir la capacité** et le type
- **Associer des ressources**
- **Gérer la disponibilité**

#### Gestion des Ressources
- **Ajouter** du matériel
- **Suivre les quantités**
- **Gérer les états** (disponible, en panne, maintenance)
- **Historique** des utilisations

### Interface Demandeur

#### Création de Réservations
1. **Sélectionner une salle** dans la liste
2. **Choisir la date** et les créneaux horaires
3. **Sélectionner les ressources** nécessaires
4. **Valider** la demande

#### Suivi des Réservations
- **Consulter** l'état de vos réservations
- **Modifier** les réservations en attente
- **Annuler** si nécessaire
- **Historique** personnel

### Interface Responsable

#### Validation des Demandes
1. **Consulter** les réservations en attente
2. **Examiner** les détails de chaque demande
3. **Valider** ou **rejeter** avec commentaires
4. **Suivre** les statistiques

#### Gestion des Ressources
- **Changer l'état** des ressources
- **Planifier** la maintenance
- **Résoudre** les conflits

## 🗄️ Base de Données

### Structure des Tables

#### `utilisateur`
```sql
CREATE TABLE utilisateur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role ENUM('admin', 'demandeur', 'responsable') NOT NULL,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    dernier_acces DATETIME NULL,
    actif BOOLEAN DEFAULT TRUE
);
```

#### `salle`
```sql
CREATE TABLE salle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    capacite INT NOT NULL CHECK (capacite > 0),
    type VARCHAR(50) NOT NULL,
    localisation VARCHAR(150) NULL,
    description TEXT NULL,
    active BOOLEAN DEFAULT TRUE
);
```

#### `ressource`
```sql
CREATE TABLE ressource (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT NULL,
    quantite INT NOT NULL CHECK (quantite >= 0),
    etat ENUM('disponible', 'en_panne', 'maintenance', 'reserve') DEFAULT 'disponible'
);
```

#### `reservation`
```sql
CREATE TABLE reservation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur INT NOT NULL,
    id_salle INT NOT NULL,
    date DATE NOT NULL,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    etat ENUM('en_attente', 'validee', 'rejetee', 'annulee') DEFAULT 'en_attente',
    commentaire TEXT NULL,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id),
    FOREIGN KEY (id_salle) REFERENCES salle(id)
);
```

### Fonctionnalités Avancées

#### Procédures Stockées
- **VerifierConflitReservation** : Détecte les conflits de créneaux
- **DisponibiliteRessource** : Calcule la disponibilité des ressources

#### Vues Optimisées
- **vue_reservations_completes** : Vue complète des réservations avec jointures

## 🎨 Design et Interface

### Thème Visuel
- **Palette de couleurs** professionnelle
- **Typographie** Segoe UI pour une lisibilité optimale
- **Icônes** intuitives pour une navigation facile
- **Responsive design** adaptatif

### Composants Personnalisés
- **Boutons stylisés** avec effets hover
- **Tableaux** avec tri et filtrage
- **Formulaires** avec validation en temps réel
- **Notifications** contextuelles

## 🔧 Fonctionnalités Techniques

### Gestion des Erreurs
- **Validation** côté client et serveur
- **Messages d'erreur** explicites
- **Logging** des actions utilisateur
- **Rollback** automatique des transactions

### Performance
- **Connexions poolées** à la base de données
- **Requêtes optimisées** avec index
- **Cache** des données fréquemment utilisées
- **Pagination** pour les grandes listes

### Sécurité
- **Authentification** par rôle
- **Validation** des entrées utilisateur
- **Protection** contre les injections SQL
- **Chiffrement** des mots de passe

## 🚀 Améliorations Futures

### Court Terme
- [ ] **Notifications** par email
- [ ] **Export** des données (PDF, Excel)
- [ ] **Statistiques** avancées
- [ ] **Thèmes** personnalisables

### Long Terme
- [ ] **API REST** pour applications mobiles
- [ ] **Synchronisation** avec calendriers externes
- [ ] **Gestion** des récurrences
- [ ] **Reporting** avancé

## 🤝 Contribution

### Comment Contribuer
1. **Fork** le projet
2. **Créer** une branche pour votre fonctionnalité
3. **Commiter** vos changements
4. **Pousser** vers la branche
5. **Ouvrir** une Pull Request

### Standards de Code
- **Java 8+** avec bonnes pratiques
- **Documentation** des méthodes publiques
- **Tests unitaires** pour les nouvelles fonctionnalités
- **Respect** des conventions de nommage

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 📞 Support

Pour toute question ou problème :
- **Issues** GitHub pour les bugs
- **Discussions** pour les questions générales
- **Wiki** pour la documentation détaillée

---

**Développé avec ❤️ pour une gestion efficace des réservations de salles**

*Version 1.0 - Janvier 2025* 