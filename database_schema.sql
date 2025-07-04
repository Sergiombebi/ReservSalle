-- Script de création de la base de données ReservSalle

-- Création de la base de données
CREATE DATABASE IF NOT EXISTS reservsalle;
USE reservsalle;

-- Suppression des tables existantes (ordre important à cause des clés étrangères)
DROP TABLE IF EXISTS reservation_ressource;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS ressource;
DROP TABLE IF EXISTS salle;
DROP TABLE IF EXISTS utilisateur;

-- Table des utilisateurs (basée sur Utilisateur.java)
CREATE TABLE utilisateur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'DEMANDEUR', 'RESPONSABLE') NOT NULL,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    dernier_acces DATETIME NULL,
    actif BOOLEAN DEFAULT TRUE,
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- Table des salles (basée sur Salle.java)
CREATE TABLE salle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    capacite INT NOT NULL CHECK (capacite > 0),
    type VARCHAR(50) NOT NULL,
    localisation VARCHAR(150) NULL,
    description TEXT NULL,
    active BOOLEAN DEFAULT TRUE,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nom (nom),
    INDEX idx_type (type)
);

-- Table des ressources (basée sur Ressource.java)
CREATE TABLE ressource (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT NULL,
    quantite INT NOT NULL CHECK (quantite >= 0),
    etat ENUM('DISPONIBLE', 'EN_PANNE', 'MAINTENANCE', 'RESERVE') DEFAULT 'DISPONIBLE',
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_nom (nom),
    INDEX idx_etat (etat)
);

-- Table des réservations (basée sur Reservation.java)
CREATE TABLE reservation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur INT NOT NULL,
    id_salle INT NOT NULL,
    date_debut DATETIME NOT NULL,
    date_fin DATETIME NOT NULL,
    etat ENUM('EN_ATTENTE', 'VALIDEE', 'REJETEE', 'ANNULEE') DEFAULT 'EN_ATTENTE',
    commentaire TEXT NULL,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME ON UPDATE CURRENT_TIMESTAMP,
    validee_par INT NULL,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (id_salle) REFERENCES salle(id) ON DELETE CASCADE,
    FOREIGN KEY (validee_par) REFERENCES utilisateur(id) ON DELETE SET NULL,
    INDEX idx_date_debut (date_debut),
    INDEX idx_etat (etat),
    CONSTRAINT chk_dates CHECK (date_debut < date_fin)
);

-- Table de liaison entre réservations et ressources (basée sur ReservationRessource.java)
CREATE TABLE reservation_ressource (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_reservation INT NOT NULL,
    id_ressource INT NOT NULL,
    quantite INT NOT NULL CHECK (quantite > 0),
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_reservation) REFERENCES reservation(id) ON DELETE CASCADE,
    FOREIGN KEY (id_ressource) REFERENCES ressource(id) ON DELETE CASCADE,
    UNIQUE KEY unique_reservation_ressource (id_reservation, id_ressource)
);

-- Insertion des données de test
INSERT INTO utilisateur (nom, email, mot_de_passe, role) VALUES
('Admin', 'admin@exemple.com', 'admin123', 'ADMIN'),
('Jean Dupont', 'jean.dupont@exemple.com', 'password123', 'DEMANDEUR'),
('Marie Martin', 'marie.martin@exemple.com', 'password123', 'RESPONSABLE');

INSERT INTO salle (nom, capacite, type, localisation, description) VALUES
('Salle A101', 30, 'COURS', 'Bâtiment A - 1er étage', 'Salle de cours standard'),
('Salle B203', 50, 'AMPHITHEATRE', 'Bâtiment B - 2ème étage', 'Grand amphithéâtre'),
('Salle C105', 20, 'LABORATOIRE', 'Bâtiment C - 1er étage', 'Laboratoire informatique');

INSERT INTO ressource (nom, description, quantite, etat) VALUES
('Vidéoprojecteur', 'Projecteur HD', 5, 'DISPONIBLE'),
('Ordinateur portable', 'PC portable', 10, 'DISPONIBLE'),
('Tableau interactif', 'Tableau numérique', 2, 'DISPONIBLE');

-- Création des vues pour faciliter les requêtes courantes
CREATE VIEW vue_reservations_completes AS
SELECT 
    r.id,
    r.date_debut,
    r.date_fin,
    r.etat,
    r.commentaire,
    u.nom AS nom_utilisateur,
    u.email AS email_utilisateur,
    s.nom AS nom_salle,
    s.type AS type_salle,
    s.capacite AS capacite_salle,
    GROUP_CONCAT(CONCAT(res.nom, ' (', rr.quantite, ')') SEPARATOR ', ') AS ressources_reservees
FROM reservation r
JOIN utilisateur u ON r.id_utilisateur = u.id
JOIN salle s ON r.id_salle = s.id
LEFT JOIN reservation_ressource rr ON r.id = rr.id_reservation
LEFT JOIN ressource res ON rr.id_ressource = res.id
GROUP BY r.id; 