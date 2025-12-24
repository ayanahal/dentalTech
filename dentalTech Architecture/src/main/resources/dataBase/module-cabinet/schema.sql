-- ==========================================================
--  Module CabinetMedical / Charges / Revenues / Statistiques
-- ==========================================================

-- Table Cabinet_Medical (CabinetMedical)
CREATE TABLE IF NOT EXISTS cabinet_medical (
                                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                               nom VARCHAR(150) NOT NULL,
    email VARCHAR(150),
    logo VARCHAR(255),
    adresse VARCHAR(255),
    cin VARCHAR(30),
    tel1 VARCHAR(30),
    tel2 VARCHAR(30),
    site_web VARCHAR(255),
    instagram VARCHAR(255),
    facebook VARCHAR(255),
    description TEXT,

    -- Champs BaseEntity (attention: votre code Java utilise createdAt/updatedAt)
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_cabinet_nom (nom),
    UNIQUE KEY uk_cabinet_email (email),
    KEY idx_cabinet_nom (nom)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================
-- Table Charges (charges du cabinet)
-- =====================================
CREATE TABLE IF NOT EXISTS charges (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       libelle VARCHAR(150) NOT NULL,          -- Votre entité a getLibelle()
    montant DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    date_charge DATETIME NOT NULL,          -- Votre entité a getDateCharge()
    categorie VARCHAR(100),                 -- Votre entité a getCategorie()
    description TEXT,
    mode_paiement VARCHAR(50),              -- Votre entité a getModePaiement()

    cabinet_id BIGINT,

    -- Champs BaseEntity
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_charges_cabinet
    FOREIGN KEY (cabinet_id) REFERENCES cabinet_medical(id)
                                                  ON DELETE CASCADE
                                                  ON UPDATE CASCADE,

    KEY idx_charges_date (date_charge),
    KEY idx_charges_cabinet (cabinet_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================
-- Table Revenues (revenus du cabinet)
-- =====================================
CREATE TABLE IF NOT EXISTS revenues (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        titre VARCHAR(150) NOT NULL,            -- Votre entité a getTitre()
    description TEXT,
    montant DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    date_revenue DATETIME NOT NULL,         -- Votre entité a getDate()

    cabinet_id BIGINT,

    -- Champs BaseEntity
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_revenues_cabinet
    FOREIGN KEY (cabinet_id) REFERENCES cabinet_medical(id)
                                                  ON DELETE CASCADE
                                                  ON UPDATE CASCADE,

    KEY idx_revenues_date (date_revenue),
    KEY idx_revenues_cabinet (cabinet_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ==========================================
-- Table Statistiques (Statistiques du cabinet)
-- ==========================================
CREATE TABLE IF NOT EXISTS statistiques (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            nom VARCHAR(150) NOT NULL,
    categorie VARCHAR(50) NOT NULL,         -- CategorieStatistique enum
    chiffre DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    date_calcul DATE NOT NULL,              -- LocalDate dans votre entité

    cabinet_id BIGINT,

    -- Champs BaseEntity
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_statistiques_cabinet
    FOREIGN KEY (cabinet_id) REFERENCES cabinet_medical(id)
                                                  ON DELETE CASCADE
                                                  ON UPDATE CASCADE,

    KEY idx_statistiques_date (date_calcul),
    KEY idx_statistiques_cabinet (cabinet_id),
    KEY idx_statistiques_categorie (categorie)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ===========================================================
-- Table d'association cabinet_staff (Many-to-Many)
-- ===========================================================
CREATE TABLE IF NOT EXISTS cabinet_staff (
                                             cabinet_id BIGINT NOT NULL,
                                             staff_id BIGINT NOT NULL,

                                             PRIMARY KEY (cabinet_id, staff_id),

    CONSTRAINT fk_cabinetstaff_cabinet
    FOREIGN KEY (cabinet_id) REFERENCES cabinet_medical(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

    -- Note: Vérifiez que la table staff existe dans module-users
    CONSTRAINT fk_cabinetstaff_staff
    FOREIGN KEY (staff_id) REFERENCES staff(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;