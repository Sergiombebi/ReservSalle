-- Ajout des colonnes manquantes dans la table reservation
ALTER TABLE `reservation`
ADD COLUMN `commentaire` text DEFAULT NULL,
ADD COLUMN `date_creation` DATETIME DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN `date_modification` DATETIME ON UPDATE CURRENT_TIMESTAMP,
ADD COLUMN `validee_par` int(11) DEFAULT NULL,
ADD CONSTRAINT `fk_validee_par` FOREIGN KEY (`validee_par`) REFERENCES `utilisateur` (`id`) ON DELETE SET NULL;

-- Ajout des colonnes manquantes dans la table reservation_ressource
ALTER TABLE `reservation_ressource`
ADD COLUMN `date_creation` DATETIME DEFAULT CURRENT_TIMESTAMP;

-- Ajout des colonnes manquantes dans la table ressource
ALTER TABLE `ressource`
MODIFY COLUMN `etat` enum('disponible','en_panne','maintenance','reserve') NOT NULL DEFAULT 'disponible',
ADD COLUMN `date_creation` DATETIME DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN `date_modification` DATETIME ON UPDATE CURRENT_TIMESTAMP;

-- Ajout des colonnes manquantes dans la table salle
ALTER TABLE `salle`
ADD COLUMN `localisation` varchar(150) DEFAULT NULL,
ADD COLUMN `description` text DEFAULT NULL,
ADD COLUMN `active` boolean DEFAULT TRUE,
ADD COLUMN `date_creation` DATETIME DEFAULT CURRENT_TIMESTAMP;

-- Ajout des colonnes manquantes dans la table utilisateur
ALTER TABLE `utilisateur`
MODIFY COLUMN `mot_de_passe` varchar(255) NOT NULL,
ADD COLUMN `date_creation` DATETIME DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN `dernier_acces` DATETIME DEFAULT NULL,
ADD COLUMN `actif` boolean DEFAULT TRUE;

-- Ajout de l'état 'annulee' dans l'enum de la table reservation
ALTER TABLE `reservation`
MODIFY COLUMN `etat` enum('en_attente','validee','rejetee','annulee') DEFAULT 'en_attente';

-- Création de la vue pour les réservations complètes
CREATE OR REPLACE VIEW vue_reservations_completes AS
SELECT 
    r.id,
    r.date,
    r.heure_debut,
    r.heure_fin,
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

-- Ajout d'index pour optimiser les performances
ALTER TABLE `reservation`
ADD INDEX `idx_date` (`date`),
ADD INDEX `idx_etat` (`etat`);

ALTER TABLE `salle`
ADD INDEX `idx_type` (`type`);

ALTER TABLE `ressource`
ADD INDEX `idx_etat` (`etat`);

-- Insertion de données de test supplémentaires pour les salles
INSERT INTO `salle` (`nom`, `capacite`, `type`, `localisation`, `description`) VALUES
('Salle A101', 30, 'COURS', 'Bâtiment A - 1er étage', 'Salle de cours standard'),
('Salle B203', 50, 'AMPHITHEATRE', 'Bâtiment B - 2ème étage', 'Grand amphithéâtre'),
('Salle C105', 20, 'LABORATOIRE', 'Bâtiment C - 1er étage', 'Laboratoire informatique');

-- Insertion de données de test pour les ressources
INSERT INTO `ressource` (`nom`, `description`, `quantite`, `etat`) VALUES
('Vidéoprojecteur', 'Projecteur HD', 5, 'disponible'),
('Ordinateur portable', 'PC portable', 10, 'disponible'),
('Tableau interactif', 'Tableau numérique', 2, 'disponible'); 