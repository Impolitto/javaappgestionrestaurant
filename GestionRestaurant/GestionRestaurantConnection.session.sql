-- Script de création de la base de données GestionRestaurant
-- MySQL

CREATE DATABASE IF NOT EXISTS GestionRestaurant;
USE GestionRestaurant;

-- Table des catégories de plats
CREATE TABLE IF NOT EXISTS CATEGORIE_PLAT (
    id_categorie INT PRIMARY KEY AUTO_INCREMENT,
    nom_categorie VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    actif BOOLEAN DEFAULT true
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des plats
CREATE TABLE IF NOT EXISTS PLAT (
    id_plat INT PRIMARY KEY AUTO_INCREMENT,
    nom_plat VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    prix DECIMAL(10, 2) NOT NULL,
    id_categorie INT,
    disponible BOOLEAN DEFAULT true,
    temps_preparation INT DEFAULT 0,
    FOREIGN KEY (id_categorie) REFERENCES CATEGORIE_PLAT(id_categorie) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des clients
CREATE TABLE IF NOT EXISTS CLIENT (
    id_client INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    telephone VARCHAR(20),
    email VARCHAR(100),
    nb_visites INT DEFAULT 0,
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des tables du restaurant
CREATE TABLE IF NOT EXISTS TABLE_RESTAURANT (
    id_table INT PRIMARY KEY AUTO_INCREMENT,
    numero_table VARCHAR(50) NOT NULL UNIQUE,
    capacite INT NOT NULL,
    emplacement VARCHAR(100),
    statut VARCHAR(50) DEFAULT 'LIBRE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des commandes
CREATE TABLE IF NOT EXISTS COMMANDE (
    id_commande INT PRIMARY KEY AUTO_INCREMENT,
    id_table INT NOT NULL,
    id_client INT,
    date_commande DATE NOT NULL,
    heure_commande TIME NOT NULL,
    statut_commande VARCHAR(50) DEFAULT 'EN_COURS',
    montant_total DECIMAL(10, 2) DEFAULT 0,
    FOREIGN KEY (id_table) REFERENCES TABLE_RESTAURANT(id_table) ON DELETE CASCADE,
    FOREIGN KEY (id_client) REFERENCES CLIENT(id_client) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des détails de commande
CREATE TABLE IF NOT EXISTS DETAIL_COMMANDE (
    id_detail INT PRIMARY KEY AUTO_INCREMENT,
    id_commande INT NOT NULL,
    id_plat INT NOT NULL,
    quantite INT NOT NULL,
    prix_unitaire DECIMAL(10, 2) NOT NULL,
    sous_total DECIMAL(10, 2) NOT NULL,
    notes_speciales VARCHAR(255),
    FOREIGN KEY (id_commande) REFERENCES COMMANDE(id_commande) ON DELETE CASCADE,
    FOREIGN KEY (id_plat) REFERENCES PLAT(id_plat) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des factures
CREATE TABLE IF NOT EXISTS FACTURE (
    id_facture INT PRIMARY KEY AUTO_INCREMENT,
    id_commande INT NOT NULL,
    date_facture DATE NOT NULL,
    montant_ht DECIMAL(10, 2) NOT NULL,
    taux_tva DECIMAL(5, 2) DEFAULT 20,
    montant_tva DECIMAL(10, 2),
    montant_ttc DECIMAL(10, 2),
    statut VARCHAR(50) DEFAULT 'EMISE',
    notes VARCHAR(255),
    FOREIGN KEY (id_commande) REFERENCES COMMANDE(id_commande) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insérer quelques catégories de test
INSERT IGNORE INTO CATEGORIE_PLAT (id_categorie, nom_categorie, description) VALUES
(1, 'Entrées', 'Plats pour commencer'),
(2, 'Plats principaux', 'Plats principaux'),
(3, 'Desserts', 'Desserts variés'),
(4, 'Boissons', 'Boissons diverses');

-- Insérer quelques plats de test
INSERT IGNORE INTO PLAT (id_plat, nom_plat, description, prix, id_categorie, disponible, temps_preparation) VALUES
(1, 'Soupe à l\'oignon', 'Soupe gratinée à l\'oignon', 8.50, 1, true, 15),
(2, 'Salade César', 'Salade fraîche avec croutons', 9.00, 1, true, 10),
(3, 'Steak Frites', 'Steak grillé avec frites maison', 18.50, 2, true, 20),
(4, 'Pâtes Carbonara', 'Pâtes avec sauce carbonara', 14.00, 2, true, 15),
(5, 'Tiramisu', 'Dessert italien traditionnel', 7.50, 3, true, 5),
(6, 'Crème brûlée', 'Crème brûlée maison', 8.00, 3, true, 5),
(7, 'Eau minérale', 'Bouteille 50cl', 2.50, 4, true, 1),
(8, 'Vin rouge', 'Verre de vin rouge', 5.00, 4, true, 1);

-- Insérer quelques tables de test
INSERT IGNORE INTO TABLE_RESTAURANT (id_table, numero_table, capacite, emplacement, statut) VALUES
(1, 'T01', 2, 'Près de la fenêtre', 'LIBRE'),
(2, 'T02', 4, 'Centre salle', 'LIBRE'),
(3, 'T03', 6, 'Coin tranquille', 'LIBRE'),
(4, 'T04', 2, 'Comptoir', 'LIBRE'),
(5, 'T05', 4, 'Terrasse', 'LIBRE');

-- Insérer quelques clients de test
INSERT IGNORE INTO CLIENT (id_client, nom, prenom, telephone, email, nb_visites) VALUES
(1, 'Dupont', 'Jean', '0612345678', 'jean.dupont@email.com', 5),
(2, 'Martin', 'Marie', '0687654321', 'marie.martin@email.com', 3),
(3, 'Bernard', 'Pierre', '0698765432', 'pierre.bernard@email.com', 1);
