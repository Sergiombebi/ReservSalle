# 🚀 Guide de Démarrage Rapide - ReservSalle

## ⚡ Installation Express (5 minutes)

### 1. Base de Données
```bash
# Créer la base de données MySQL
mysql -u root -p
CREATE DATABASE reservsalle;
exit

# Importer le schéma
mysql -u root -p reservsalle < database_schema.sql
```

### 2. Configuration
Modifier `src/dao/DatabaseManager.java` :
```java
private static final String URL = "jdbc:mysql://localhost:3306/reservsalle";
private static final String USER = "root";  // Votre utilisateur MySQL
private static final String PASSWORD = "";  // Votre mot de passe MySQL
```

### 3. Lancement
```bash
# Compiler et exécuter
javac -cp ".:mysql-connector-j-9.2.0.jar" src/Main/Main.java
java -cp ".:mysql-connector-j-9.2.0.jar:src" Main.Main
```

## 👥 Comptes de Test

| Rôle | Email/Nom | Mot de passe | Fonctionnalités |
|------|-----------|--------------|-----------------|
| **Admin** | `admin` | `admin123` | Gestion complète du système |
| **Demandeur** | `jean.dupont@exemple.com` | `password123` | Créer et suivre des réservations |
| **Responsable** | `pierre.durand@exemple.com` | `password123` | Valider les réservations |

## 🎯 Première Utilisation

### En tant qu'Administrateur
1. **Connexion** → Sélectionner "admin" → Saisir identifiants
2. **Ajouter des utilisateurs** → Onglet "Utilisateurs" → Bouton "Ajouter"
3. **Créer des salles** → Onglet "Salles" → Définir capacité et type
4. **Gérer les ressources** → Onglet "Ressources" → Ajouter matériel

### En tant que Demandeur
1. **Connexion** → Sélectionner "Demandeur" → Identifiants
2. **Nouvelle réservation** → Choisir salle + date + horaires
3. **Sélectionner ressources** → Vidéoprojecteur, PC, etc.
4. **Valider** → Attendre validation du responsable

### En tant que Responsable
1. **Connexion** → Sélectionner "Responsable" → Identifiants
2. **Voir les demandes** → Tableau des réservations en attente
3. **Valider/Rejeter** → Sélectionner une ligne → Boutons d'action
4. **Gérer les ressources** → Changer états (maintenance, panne)

## 📅 Fonctionnalités Principales

### ✅ Réservations
- **Création intuitive** avec calendrier visuel
- **Vérification automatique** des conflits
- **Gestion des ressources** matérielles
- **Suivi en temps réel** des états

### 🔧 Administration
- **Gestion des utilisateurs** (CRUD complet)
- **Configuration des salles** avec localisation
- **Inventaire des ressources** avec quantités
- **Calendrier global** par salle

### 📊 Validation
- **Interface dédiée** pour responsables
- **Détails complets** de chaque demande
- **Historique** des décisions
- **Statistiques** en temps réel

## 🎨 Interface Moderne

### Codes Couleurs
- 🟢 **Vert** : Réservations validées
- 🟡 **Jaune** : En attente de validation
- 🔴 **Rouge** : Rejetées ou en conflit
- ⚪ **Blanc** : Créneaux libres

### Navigation
- **Onglets** pour différentes fonctions
- **Boutons colorés** avec icônes
- **Tableaux interactifs** avec tri
- **Formulaires** avec validation

## 🔧 Résolution de Problèmes

### Erreur de Connexion MySQL
```
Erreur: "SQLException: Access denied"
→ Vérifier USER et PASSWORD dans DatabaseManager.java
→ S'assurer que MySQL est démarré
→ Vérifier que la base 'reservsalle' existe
```

### Conflit de Réservation
```
Message: "Créneau non disponible"
→ Vérifier qu'aucune autre réservation n'existe
→ Choisir un autre horaire
→ Contacter l'administrateur si problème persiste
```

### Interface ne s'affiche pas
```
→ Vérifier que Java 8+ est installé
→ S'assurer que mysql-connector-j-9.2.0.jar est dans le classpath
→ Relancer l'application
```

## 🎯 Cas d'Usage Typiques

### Scénario 1 : Réservation de Cours
1. **Enseignant** se connecte comme Demandeur
2. Sélectionne **Salle A101** pour jeudi 9h-11h
3. Ajoute **vidéoprojecteur** + **système audio**
4. **Responsable** valide la demande
5. Réservation confirmée ✅

### Scénario 2 : Gestion Administrative
1. **Admin** ajoute nouvelle salle "Labo B205"
2. Définit capacité 25, type "Laboratoire"
3. Associe 20 PC + 1 vidéoprojecteur
4. Salle disponible pour réservations ✅

### Scénario 3 : Maintenance
1. **Responsable** remarque panne vidéoprojecteur
2. Change état ressource → "En panne"
3. Ressource non disponible pour nouvelles réservations
4. Répare → Change état → "Disponible" ✅

## 📞 Support

### Problèmes Techniques
- Vérifier le fichier `AMELIORATIONS.md` pour détails techniques
- Consulter le `README.md` pour architecture complète
- Examiner `database_schema.sql` pour structure BDD

### Fonctionnalités Manquantes
- Toutes les exigences du TP308 sont implémentées ✅
- Fonctionnalités bonus disponibles (calendrier, stats)
- Interface moderne et professionnelle ✅

## 🏆 Récapitulatif des Améliorations

### ✨ Nouvelles Fonctionnalités
- **Enums type-safe** pour rôles et états
- **Calendrier visuel** mensuel avec filtres
- **Interface de validation** dédiée aux responsables
- **Gestion avancée** des ressources avec états
- **Vérification automatique** des conflits

### 🎨 Interface Professionnelle
- **Design cohérent** avec palette de couleurs
- **Composants personnalisés** avec effets visuels
- **Navigation intuitive** par rôles
- **Tableaux interactifs** avec tri et filtrage

### 🔧 Optimisations Techniques
- **Base de données robuste** avec contraintes
- **Procédures stockées** pour performance
- **Gestion des erreurs** complète
- **Architecture extensible** pour futures améliorations

---

**🚀 Votre système de réservation est maintenant prêt à utiliser !**

*Pour plus de détails techniques, consultez `AMELIORATIONS.md`* 