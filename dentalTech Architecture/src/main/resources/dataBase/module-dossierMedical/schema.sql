-- ============================================
-- SCHEMA : MODULE DOSSIER MEDICAL
-- ============================================

-- Table dossier_medical
CREATE TABLE IF NOT EXISTS dossier_medical (
                                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                               patient_id BIGINT NOT NULL,
                                               date_creation DATE NOT NULL DEFAULT (CURRENT_DATE),

    -- BaseEntity
    date_derniere_modification DATETIME NULL,
    cree_par VARCHAR(80) NULL,
    modifie_par VARCHAR(80) NULL,

    FOREIGN KEY (patient_id) REFERENCES Patients(id) ON DELETE CASCADE,
    UNIQUE KEY uk_dossier_patient (patient_id),
    KEY idx_dossier_date_creation (date_creation)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table consultation
CREATE TABLE IF NOT EXISTS consultation (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            date_consultation DATE NOT NULL,
                                            statut ENUM('PLANIFIEE', 'EN_COURS', 'TERMINEE', 'ANNULEE') NOT NULL DEFAULT 'PLANIFIEE',
    observation_medecin TEXT,
    dossier_medical_id BIGINT NOT NULL,
    medecin_id BIGINT NOT NULL,

    -- BaseEntity
    date_creation DATE NOT NULL DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME NULL,
    cree_par VARCHAR(80) NULL,
    modifie_par VARCHAR(80) NULL,

    FOREIGN KEY (dossier_medical_id) REFERENCES dossier_medical(id) ON DELETE CASCADE,
    FOREIGN KEY (medecin_id) REFERENCES medecin(id) ON DELETE CASCADE,
    KEY idx_consultation_date (date_consultation),
    KEY idx_consultation_statut (statut),
    KEY idx_consultation_medecin (medecin_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table acte
CREATE TABLE IF NOT EXISTS acte (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    libelle VARCHAR(150) NOT NULL,
    categorie VARCHAR(50) NOT NULL,
    prix_base DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    -- BaseEntity
    date_creation DATE NOT NULL DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME NULL,
    cree_par VARCHAR(80) NULL,
    modifie_par VARCHAR(80) NULL,

    UNIQUE KEY uk_acte_libelle (libelle),
    KEY idx_acte_categorie (categorie),
    KEY idx_acte_prix (prix_base)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table intervention_medecin
CREATE TABLE IF NOT EXISTS intervention_medecin (
                                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                    prix_patient DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    num_dent INT NULL,
    consultation_id BIGINT NULL,
    acte_id BIGINT NOT NULL,

    -- BaseEntity
    date_creation DATE NOT NULL DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME NULL,
    cree_par VARCHAR(80) NULL,
    modifie_par VARCHAR(80) NULL,

    FOREIGN KEY (consultation_id) REFERENCES consultation(id) ON DELETE SET NULL,
    FOREIGN KEY (acte_id) REFERENCES acte(id) ON DELETE CASCADE,
    KEY idx_intervention_consultation (consultation_id),
    KEY idx_intervention_acte (acte_id),
    KEY idx_intervention_num_dent (num_dent),
    KEY idx_intervention_prix (prix_patient)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table ordonnance
CREATE TABLE IF NOT EXISTS ordonnance (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          date DATE NOT NULL DEFAULT (CURRENT_DATE),
    dossier_medical_id BIGINT NOT NULL,
    medecin_id BIGINT NOT NULL,

    -- BaseEntity
    date_creation DATE NOT NULL DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME NULL,
    cree_par VARCHAR(80) NULL,
    modifie_par VARCHAR(80) NULL,

    FOREIGN KEY (dossier_medical_id) REFERENCES dossier_medical(id) ON DELETE CASCADE,
    FOREIGN KEY (medecin_id) REFERENCES medecin(id) ON DELETE CASCADE,
    KEY idx_ordonnance_date (date),
    KEY idx_ordonnance_dossier (dossier_medical_id),
    KEY idx_ordonnance_medecin (medecin_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table medicament
CREATE TABLE IF NOT EXISTS medicament (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          nom VARCHAR(150) NOT NULL,
    laboratoire VARCHAR(100),
    type VARCHAR(50),
    forme ENUM('COMPRIME', 'SIROP', 'GELULE', 'POUDRE', 'POMMADE', 'INJECTABLE', 'SUPPOSITOIRE', 'COLLYRE') NOT NULL,
    remboursable BOOLEAN NOT NULL DEFAULT FALSE,
    prix_unitaire DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    description TEXT,

    -- BaseEntity
    date_creation DATE NOT NULL DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME NULL,
    cree_par VARCHAR(80) NULL,
    modifie_par VARCHAR(80) NULL,

    UNIQUE KEY uk_medicament_nom (nom),
    KEY idx_medicament_laboratoire (laboratoire),
    KEY idx_medicament_forme (forme),
    KEY idx_medicament_remboursable (remboursable)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table prescription
CREATE TABLE IF NOT EXISTS prescription (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            quantite INT NOT NULL DEFAULT 1,
                                            frequence VARCHAR(100) NOT NULL,
    duree_en_jours INT NOT NULL DEFAULT 7,
    ordonnance_id BIGINT NOT NULL,
    medicament_id BIGINT NOT NULL,

    -- BaseEntity
    date_creation DATE NOT NULL DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME NULL,
    cree_par VARCHAR(80) NULL,
    modifie_par VARCHAR(80) NULL,

    FOREIGN KEY (ordonnance_id) REFERENCES ordonnance(id) ON DELETE CASCADE,
    FOREIGN KEY (medicament_id) REFERENCES medicament(id) ON DELETE CASCADE,
    KEY idx_prescription_ordonnance (ordonnance_id),
    KEY idx_prescription_medicament (medicament_id),
    KEY idx_prescription_quantite (quantite)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table certificat
CREATE TABLE IF NOT EXISTS certificat (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          date_debut DATE NOT NULL,
                                          date_fin DATE NOT NULL,
                                          duree INT NOT NULL,
                                          note_medecin TEXT,
                                          dossier_medical_id BIGINT NOT NULL,
                                          medecin_id BIGINT NOT NULL,

    -- BaseEntity
                                          date_creation DATE NOT NULL DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME NULL,
    cree_par VARCHAR(80) NULL,
    modifie_par VARCHAR(80) NULL,

    FOREIGN KEY (dossier_medical_id) REFERENCES dossier_medical(id) ON DELETE CASCADE,
    FOREIGN KEY (medecin_id) REFERENCES medecin(id) ON DELETE CASCADE,
    KEY idx_certificat_dates (date_debut, date_fin),
    KEY idx_certificat_dossier (dossier_medical_id),
    KEY idx_certificat_medecin (medecin_id),
    CONSTRAINT chk_certificat_dates CHECK (date_debut <= date_fin)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table situation_financiere
CREATE TABLE IF NOT EXISTS situation_financiere (
                                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                    totale_des_actes DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    totale_paye DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    credit DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    statut ENUM('SOLDE', 'EN_RETARD', 'IMPAYE') NOT NULL DEFAULT 'SOLDE',
    en_promo BOOLEAN NOT NULL DEFAULT FALSE,
    dossier_medical_id BIGINT NOT NULL,

    -- BaseEntity
    date_creation DATE NOT NULL DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME NULL,
    cree_par VARCHAR(80) NULL,
    modifie_par VARCHAR(80) NULL,

    FOREIGN KEY (dossier_medical_id) REFERENCES dossier_medical(id) ON DELETE CASCADE,
    UNIQUE KEY uk_situation_dossier (dossier_medical_id),
    KEY idx_situation_statut (statut),
    KEY idx_situation_credit (credit),
    KEY idx_situation_promo (en_promo)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table facture (pour référence - peut être dans un module séparé)
CREATE TABLE IF NOT EXISTS facture (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       totale_facture DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    totale_paye DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    reste DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    statut ENUM('NON_PAYEE', 'PARTIELLEMENT_PAYEE', 'PAYEE', 'ANNULEE') NOT NULL DEFAULT 'NON_PAYEE',
    date_facture DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    situation_financiere_id BIGINT NOT NULL,

    -- BaseEntity
    date_creation DATE NOT NULL DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME NULL,
    cree_par VARCHAR(80) NULL,
    modifie_par VARCHAR(80) NULL,

    FOREIGN KEY (situation_financiere_id) REFERENCES situation_financiere(id) ON DELETE CASCADE,
    KEY idx_facture_date (date_facture),
    KEY idx_facture_statut (statut),
    KEY idx_facture_situation (situation_financiere_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ============================================
-- INDEXES ADDITIONNELS POUR PERFORMANCE
-- ============================================

-- Index composites pour les recherches fréquentes
CREATE INDEX idx_dossier_medical_patient_date ON dossier_medical(patient_id, date_creation);
CREATE INDEX idx_consultation_dossier_date ON consultation(dossier_medical_id, date_consultation);
CREATE INDEX idx_ordonnance_dossier_medecin ON ordonnance(dossier_medical_id, medecin_id);
CREATE INDEX idx_certificat_dossier_periode ON certificat(dossier_medical_id, date_debut, date_fin);

-- ============================================
-- VUES POUR RAPPORTS (OPTIONNEL)
-- ============================================

-- Vue pour les statistiques des consultations
CREATE OR REPLACE VIEW vue_statistiques_consultations AS
SELECT
    DATE_FORMAT(c.date_consultation, '%Y-%m') as mois,
    COUNT(*) as nombre_consultations,
    COUNT(DISTINCT c.dossier_medical_id) as patients_distincts,
    COUNT(DISTINCT c.medecin_id) as medecins_distincts,
    COALESCE(SUM(i.prix_patient), 0) as chiffre_affaires
FROM consultation c
         LEFT JOIN intervention_medecin i ON c.id = i.consultation_id
GROUP BY DATE_FORMAT(c.date_consultation, '%Y-%m');

-- Vue pour la situation financière des patients
CREATE OR REPLACE VIEW vue_situation_financiere_patients AS
SELECT
    p.id as patient_id,
    p.nom,
    p.prenom,
    dm.id as dossier_id,
    sf.totale_des_actes,
    sf.totale_paye,
    sf.credit,
    sf.statut,
    COUNT(DISTINCT c.id) as nombre_consultations,
    COUNT(DISTINCT o.id) as nombre_ordonnances
FROM Patients p
         JOIN dossier_medical dm ON p.id = dm.patient_id
         LEFT JOIN situation_financiere sf ON dm.id = sf.dossier_medical_id
         LEFT JOIN consultation c ON dm.id = c.dossier_medical_id
         LEFT JOIN ordonnance o ON dm.id = o.dossier_medical_id
GROUP BY p.id, dm.id, sf.id;

-- ============================================
-- TRIGGERS POUR L'AUDIT (OPTIONNEL)
-- ============================================

-- Trigger pour mettre à jour date_derniere_modification
DELIMITER $$

CREATE TRIGGER trg_dossier_medical_update
    BEFORE UPDATE ON dossier_medical
    FOR EACH ROW
BEGIN
    SET NEW.date_derniere_modification = NOW();
END$$

    CREATE TRIGGER trg_consultation_update
        BEFORE UPDATE ON consultation
        FOR EACH ROW
    BEGIN
        SET NEW.date_derniere_modification = NOW();
END$$

        CREATE TRIGGER trg_situation_financiere_update
            BEFORE UPDATE ON situation_financiere
            FOR EACH ROW
        BEGIN
            SET NEW.date_derniere_modification = NOW();

    -- Calcul automatique du crédit
    SET NEW.credit = NEW.totale_des_actes - NEW.totale_paye;

    -- Mise à jour automatique du statut
    IF NEW.credit <= 0 THEN
        SET NEW.statut = 'SOLDE';
    ELSEIF NEW.credit > 0 AND NEW.credit <= 1000 THEN
        SET NEW.statut = 'EN_RETARD';
            ELSE
        SET NEW.statut = 'IMPAYE';
        END IF;
        END$$

        DELIMITER ;