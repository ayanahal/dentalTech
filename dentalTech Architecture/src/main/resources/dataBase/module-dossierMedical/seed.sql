-- ============================================
-- SEED DATA : MODULE DOSSIER MEDICAL
-- ============================================

-- Suppression des données existantes (dans l'ordre inverse des dépendances)
DELETE FROM facture;
DELETE FROM situation_financiere;
DELETE FROM certificat;
DELETE FROM prescription;
DELETE FROM ordonnance;
DELETE FROM intervention_medecin;
DELETE FROM consultation;
DELETE FROM medicament;
DELETE FROM acte;
DELETE FROM dossier_medical;

-- Réinitialisation des auto-incréments
ALTER TABLE dossier_medical AUTO_INCREMENT = 1;
ALTER TABLE consultation AUTO_INCREMENT = 1;
ALTER TABLE acte AUTO_INCREMENT = 1;
ALTER TABLE intervention_medecin AUTO_INCREMENT = 1;
ALTER TABLE ordonnance AUTO_INCREMENT = 1;
ALTER TABLE medicament AUTO_INCREMENT = 1;
ALTER TABLE prescription AUTO_INCREMENT = 1;
ALTER TABLE certificat AUTO_INCREMENT = 1;
ALTER TABLE situation_financiere AUTO_INCREMENT = 1;
ALTER TABLE facture AUTO_INCREMENT = 1;

-- ============================================
-- 1. CRÉATION DES DOSSIERS MÉDICAUX
-- ============================================

INSERT INTO dossier_medical (patient_id, date_creation, cree_par) VALUES
                                                                      (1, '2024-01-15', 'admin'),   -- Amal
                                                                      (2, '2024-02-20', 'admin'),   -- Omar
                                                                      (3, '2024-03-10', 'admin'),   -- Nour
                                                                      (4, '2024-01-25', 'admin'),   -- Youssef
                                                                      (5, '2024-04-05', 'admin'),   -- Hiba
                                                                      (6, '2024-02-28', 'admin');   -- Mahdi

-- ============================================
-- 2. ACTES MÉDICAUX DENTAIRES
-- ============================================

INSERT INTO acte (libelle, categorie, prix_base, cree_par) VALUES
-- Soins conservateurs
('Détartrage complet', 'SOINS_PREVENTIFS', 300.00, 'admin'),
('Scellement de sillons', 'SOINS_PREVENTIFS', 250.00, 'admin'),
('Fluoruration', 'SOINS_PREVENTIFS', 150.00, 'admin'),

-- Soins curatifs
('Obturation composite (petite)', 'ODONTOLOGIE_CONSERVATRICE', 400.00, 'admin'),
('Obturation composite (moyenne)', 'ODONTOLOGIE_CONSERVATRICE', 600.00, 'admin'),
('Obturation composite (grande)', 'ODONTOLOGIE_CONSERVATRICE', 800.00, 'admin'),
('Dévitalisation incisive/canines', 'ENDODONTIE', 1000.00, 'admin'),
('Dévitalisation prémolaire', 'ENDODONTIE', 1200.00, 'admin'),
('Dévitalisation molaire', 'ENDODONTIE', 1500.00, 'admin'),

-- Chirurgie
('Extraction dent simple', 'CHIRURGIE', 500.00, 'admin'),
('Extraction dent incluse', 'CHIRURGIE', 1200.00, 'admin'),
('Greffe gingivale', 'PARODONTOLOGIE', 2000.00, 'admin'),

-- Prothèse
('Couronne céramo-métallique', 'PROTHESE', 2500.00, 'admin'),
('Bridge 3 éléments', 'PROTHESE', 6000.00, 'admin'),
('Prothèse amovible partielle', 'PROTHESE', 3500.00, 'admin'),
('Prothèse amovible complète', 'PROTHESE', 5000.00, 'admin'),

-- Implantologie
('Implant dentaire', 'IMPLANTOLOGIE', 8000.00, 'admin'),
('Pose pilier implant', 'IMPLANTOLOGIE', 1500.00, 'admin'),

-- Orthodontie
('Consultation orthodontique', 'ORTHODONTIE', 200.00, 'admin'),
('Traitement orthodontique (début)', 'ORTHODONTIE', 15000.00, 'admin');

-- ============================================
-- 3. MÉDICAMENTS COURANTS
-- ============================================

INSERT INTO medicament (nom, laboratoire, type, forme, remboursable, prix_unitaire, description, cree_par) VALUES
-- Antalgiques
('Doliprane 1000mg', 'SANOFI', 'ANTALGIQUE', 'COMPRIME', TRUE, 15.50, 'Paracétamol 1000mg - Antalgique et antipyrétique', 'admin'),
('Ibuprofene 400mg', 'BIOCODEX', 'ANTI-INFLAMMATOIRE', 'COMPRIME', TRUE, 12.80, 'Anti-inflammatoire non stéroïdien', 'admin'),
('Aspégic 1000mg', 'SANOFI', 'ANTALGIQUE', 'POUDRE', TRUE, 18.20, 'Aspirine effervescente', 'admin'),

-- Antibiotiques
('Amoxicilline 1g', 'GSK', 'ANTIBIOTIQUE', 'COMPRIME', TRUE, 25.00, 'Antibiotique à large spectre', 'admin'),
('Clamoxyl 500mg', 'GSK', 'ANTIBIOTIQUE', 'COMPRIME', TRUE, 28.50, 'Amoxicilline trihydratée', 'admin'),
('Ciflox 500mg', 'BAYER', 'ANTIBIOTIQUE', 'COMPRIME', TRUE, 35.00, 'Ciprofloxacine - Antibiotique fluoroquinolone', 'admin'),

-- Anesthésiques locaux
('Xylocaine', 'ASTRAZENECA', 'ANESTHESIQUE', 'INJECTABLE', TRUE, 45.00, 'Lidocaïne - Anesthésique local', 'admin'),
('Scandonest', 'SEPTODONT', 'ANESTHESIQUE', 'INJECTABLE', TRUE, 52.00, 'Articaine - Anesthésique local dentaire', 'admin'),

-- Antiseptiques
('Bain de bouche Parodontal', 'PIERRE FABRE', 'ANTISEPTIQUE', 'SOLUTION', FALSE, 32.00, 'Solution pour bain de bouche', 'admin'),
('Chlorhexidine 0.12%', 'COLGATE', 'ANTISEPTIQUE', 'GEL', TRUE, 28.50, 'Gel buccal antiseptique', 'admin'),

-- Soins post-opératoires
('Solupred 20mg', 'SANOFI', 'CORTICOIDE', 'COMPRIME', TRUE, 42.00, 'Prednisolone - Anti-inflammatoire stéroïdien', 'admin'),
('Spasfon', 'BOIRON', 'ANTISPASMODIQUE', 'COMPRIME', TRUE, 16.80, 'Phloroglucinol - Antispasmodique', 'admin');

-- ============================================
-- 4. CONSULTATIONS
-- ============================================

INSERT INTO consultation (date_consultation, statut, observation_medecin, dossier_medical_id, medecin_id, cree_par) VALUES
-- Patient 1 : Amal (3 consultations)
('2024-01-20', 'TERMINEE', 'Patient présente des caries sur les molaires 36 et 46. Détartrage effectué.', 1, 1, 'admin'),
('2024-03-15', 'TERMINEE', 'Contrôle post-traitement. Situation stable.', 1, 1, 'admin'),
('2024-05-10', 'PLANIFIEE', 'Prochaine visite de contrôle', 1, 1, 'admin'),

-- Patient 2 : Omar (2 consultations)
('2024-02-25', 'TERMINEE', 'Douleur sur dent 16. Carie profonde nécessitant dévitalisation.', 2, 2, 'admin'),
('2024-04-20', 'EN_COURS', 'Traitement endodontique en cours sur dent 16.', 2, 2, 'admin'),

-- Patient 3 : Nour (1 consultation)
('2024-03-12', 'TERMINEE', 'Consultation pré-natale. Soins légers uniquement.', 3, 1, 'admin'),

-- Patient 4 : Youssef (3 consultations)
('2024-01-30', 'TERMINEE', 'Extraction dent de sagesse 48 incluse.', 4, 2, 'admin'),
('2024-02-28', 'TERMINEE', 'Suture enlevée. Cicatrisation normale.', 4, 2, 'admin'),
('2024-04-15', 'TERMINEE', 'Contrôle post-opératoire. Tout est normal.', 4, 2, 'admin'),

-- Patient 5 : Hiba (1 consultation)
('2024-04-10', 'TERMINEE', 'Première consultation. Détartrage et fluoruration.', 5, 1, 'admin'),

-- Patient 6 : Mahdi (2 consultations)
('2024-03-05', 'TERMINEE', 'Consultation pour pose implant dent 36.', 6, 2, 'admin'),
('2024-04-25', 'EN_COURS', 'Traitement implantologique en cours.', 6, 2, 'admin');

-- ============================================
-- 5. INTERVENTIONS MÉDICALES
-- ============================================

INSERT INTO intervention_medecin (prix_patient, num_dent, consultation_id, acte_id, cree_par) VALUES
-- Consultation 1 : Amal (détartrage + obturations)
(300.00, NULL, 1, 1, 'admin'),  -- Détartrage
(400.00, 36, 1, 4, 'admin'),    -- Obturation petite dent 36
(400.00, 46, 1, 4, 'admin'),    -- Obturation petite dent 46

-- Consultation 2 : Omar (dévitalisation)
(1500.00, 16, 2, 9, 'admin'),   -- Dévitalisation molaire 16
(600.00, 16, 2, 6, 'admin'),    -- Obturation moyenne dent 16

-- Consultation 4 : Youssef (extraction)
(1200.00, 48, 4, 11, 'admin'),  -- Extraction dent incluse 48

-- Consultation 7 : Hiba (soins préventifs)
(300.00, NULL, 7, 1, 'admin'),  -- Détartrage
(150.00, NULL, 7, 3, 'admin'),  -- Fluoruration

-- Consultation 8 : Mahdi (implant)
(8000.00, 36, 8, 16, 'admin');  -- Implant dentaire 36

-- ============================================
-- 6. ORDONNANCES
-- ============================================

INSERT INTO ordonnance (date, dossier_medical_id, medecin_id, cree_par) VALUES
-- Ordonnances pour Amal
('2024-01-20', 1, 1, 'admin'),
('2024-03-15', 1, 1, 'admin'),

-- Ordonnance pour Omar
('2024-02-25', 2, 2, 'admin'),

-- Ordonnance pour Youssef
('2024-01-30', 4, 2, 'admin'),

-- Ordonnance pour Mahdi
('2024-03-05', 6, 2, 'admin');

-- ============================================
-- 7. PRESCRIPTIONS
-- ============================================

INSERT INTO prescription (quantite, frequence, duree_en_jours, ordonnance_id, medicament_id, cree_par) VALUES
-- Ordonnance 1 : Amal (après soins)
(20, '1 comprimé 3 fois par jour', 7, 1, 1, 'admin'),  -- Doliprane
(12, '1 comprimé 2 fois par jour', 6, 1, 2, 'admin'),  -- Ibuprofene

-- Ordonnance 2 : Amal (antibiotique)
(14, '1 comprimé 2 fois par jour', 7, 2, 4, 'admin'),  -- Amoxicilline

-- Ordonnance 3 : Omar (après dévitalisation)
(20, '1 comprimé 3 fois par jour', 7, 3, 1, 'admin'),  -- Doliprane
(14, '1 comprimé 2 fois par jour', 7, 3, 4, 'admin'),  -- Amoxicilline
(1, '1 flacon par jour en bain de bouche', 5, 3, 9, 'admin'),  -- Bain de bouche

-- Ordonnance 4 : Youssef (post-extraction)
(20, '1 comprimé 3 fois par jour', 5, 4, 1, 'admin'),  -- Doliprane
(10, '1 comprimé 2 fois par jour', 5, 4, 2, 'admin'),  -- Ibuprofene
(7, '1 comprimé 2 fois par jour', 7, 4, 4, 'admin'),   -- Amoxicilline

-- Ordonnance 5 : Mahdi (post-implant)
(30, '1 comprimé 3 fois par jour', 10, 5, 1, 'admin'), -- Doliprane
(20, '1 comprimé 2 fois par jour', 10, 5, 11, 'admin'),-- Solupred
(14, '1 comprimé 2 fois par jour', 7, 5, 4, 'admin');  -- Amoxicilline

-- ============================================
-- 8. CERTIFICATS MÉDICAUX
-- ============================================

INSERT INTO certificat (date_debut, date_fin, duree, note_medecin, dossier_medical_id, medecin_id, cree_par) VALUES
-- Certificat pour Youssef (après extraction)
('2024-01-30', '2024-02-02', 3, 'Repos recommandé après extraction dent de sagesse. Alimentation molle conseillée.', 4, 2, 'admin'),

-- Certificat pour Mahdi (après pose implant)
('2024-03-05', '2024-03-10', 5, 'Arrêt de travail pour soins dentaires lourds. Éviter efforts physiques.', 6, 2, 'admin'),

-- Certificat pour Amal (soins dentaires)
('2024-01-20', '2024-01-21', 1, 'Soins dentaires nécessitant repos.', 1, 1, 'admin');

-- ============================================
-- 9. SITUATIONS FINANCIÈRES
-- ============================================

INSERT INTO situation_financiere (totale_des_actes, totale_paye, credit, statut, en_promo, dossier_medical_id, cree_par) VALUES
-- Amal : 300 + 400 + 400 = 1100 DH
(1100.00, 1100.00, 0.00, 'SOLDE', FALSE, 1, 'admin'),

-- Omar : 1500 + 600 = 2100 DH (a payé 1500)
(2100.00, 1500.00, 600.00, 'EN_RETARD', FALSE, 2, 'admin'),

-- Nour : 0 DH (consultation seulement)
(0.00, 0.00, 0.00, 'SOLDE', FALSE, 3, 'admin'),

-- Youssef : 1200 DH (a payé 600)
(1200.00, 600.00, 600.00, 'EN_RETARD', FALSE, 4, 'admin'),

-- Hiba : 300 + 150 = 450 DH
(450.00, 450.00, 0.00, 'SOLDE', FALSE, 5, 'admin'),

-- Mahdi : 8000 DH (traitement en cours, a payé 4000)
(8000.00, 4000.00, 4000.00, 'IMPAYE', FALSE, 6, 'admin');

-- ============================================
-- 10. FACTURES (EXEMPLE)
-- ============================================

INSERT INTO facture (totale_facture, totale_paye, reste, statut, date_facture, situation_financiere_id, cree_par) VALUES
-- Facture pour Amal
(1100.00, 1100.00, 0.00, 'PAYEE', '2024-01-20 14:30:00', 1, 'admin'),

-- Facture pour Omar
(2100.00, 1500.00, 600.00, 'PARTIELLEMENT_PAYEE', '2024-02-25 16:45:00', 2, 'admin'),

-- Facture pour Youssef
(1200.00, 600.00, 600.00, 'PARTIELLEMENT_PAYEE', '2024-01-30 11:20:00', 4, 'admin'),

-- Facture pour Hiba
(450.00, 450.00, 0.00, 'PAYEE', '2024-04-10 10:15:00', 5, 'admin'),

-- Facture pour Mahdi (premier versement)
(4000.00, 4000.00, 0.00, 'PAYEE', '2024-03-05 09:30:00', 6, 'admin'),

-- Facture pour Mahdi (reste à payer)
(4000.00, 0.00, 4000.00, 'NON_PAYEE', '2024-04-25 15:00:00', 6, 'admin');

-- ============================================
-- STATISTIQUES DE VÉRIFICATION
-- ============================================

SELECT '=== STATISTIQUES DOSSIER MEDICAL ===' as '';
SELECT COUNT(*) as total_dossiers FROM dossier_medical;
SELECT COUNT(*) as total_consultations FROM consultation;
SELECT COUNT(*) as total_interventions FROM intervention_medecin;
SELECT COUNT(*) as total_ordonnances FROM ordonnance;
SELECT COUNT(*) as total_prescriptions FROM prescription;
SELECT COUNT(*) as total_certificats FROM certificat;

SELECT '=== SITUATION FINANCIÈRE TOTALE ===' as '';
SELECT
    SUM(totale_des_actes) as total_actes,
    SUM(totale_paye) as total_paye,
    SUM(credit) as total_credit,
    COUNT(CASE WHEN statut = 'SOLDE' THEN 1 END) as patients_soldes,
    COUNT(CASE WHEN statut = 'EN_RETARD' THEN 1 END) as patients_en_retard,
    COUNT(CASE WHEN statut = 'IMPAYE' THEN 1 END) as patients_impayes
FROM situation_financiere;

SELECT '=== TOP 3 ACTES LES PLUS PRATIQUÉS ===' as '';
SELECT a.libelle, COUNT(i.id) as nombre_interventions, SUM(i.prix_patient) as revenu_total
FROM acte a
         LEFT JOIN intervention_medecin i ON a.id = i.acte_id
GROUP BY a.id
ORDER BY nombre_interventions DESC
    LIMIT 3;

SELECT '=== MÉDICAMENTS LES PLUS PRESCRITS ===' as '';
SELECT m.nom, COUNT(p.id) as nombre_prescriptions, SUM(p.quantite) as total_quantite
FROM medicament m
         LEFT JOIN prescription p ON m.id = p.medicament_id
GROUP BY m.id
ORDER BY nombre_prescriptions DESC
    LIMIT 5;