package ma.dentalTech.repository.common;

import ma.dentalTech.entities.agenda.*;
import ma.dentalTech.entities.dossierMedical.*;
import ma.dentalTech.entities.patient.*;
import ma.dentalTech.entities.enums.*;
import ma.dentalTech.entities.cabinet.*;
import ma.dentalTech.entities.users.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public final class RowMappers {

    private RowMappers(){}

    // ==================== PATIENT MODULE ====================

    public static Patient mapPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setId(rs.getLong("id"));
        p.setNom(rs.getString("nom"));
        p.setPrenom(rs.getString("prenom"));
        p.setAdresse(rs.getString("adresse"));
        p.setTelephone(rs.getString("telephone"));
        p.setEmail(rs.getString("email"));

        Date dn = rs.getDate("dateNaissance");
        if (dn != null) p.setDateNaissance(dn.toLocalDate());

        Date dc = rs.getDate("dateCreation");
        if (dc != null) p.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) p.setDateDerniereModification(dm.toLocalDateTime());

        p.setCreePar(rs.getString("creePar"));
        p.setModifiePar(rs.getString("modifiePar"));

        String sexeStr = rs.getString("sexe");
        if (sexeStr != null) p.setSexe(Sexe.valueOf(sexeStr));

        String assuranceStr = rs.getString("assurance");
        if (assuranceStr != null) p.setAssurance(Assurance.valueOf(assuranceStr));

        return p;
    }

    public static Antecedent mapAntecedent(ResultSet rs) throws SQLException {
        Antecedent a = new Antecedent();
        a.setId(rs.getLong("id"));
        a.setNom(rs.getString("nom"));

        String categorieStr = rs.getString("categorie");
        if (categorieStr != null) a.setCategorie(CategorieAntecedent.valueOf(categorieStr));

        String risqueStr = rs.getString("niveauRisque");
        if (risqueStr != null) a.setNiveauRisque(NiveauRisque.valueOf(risqueStr));

        Date dc = rs.getDate("dateCreation");
        if (dc != null) a.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) a.setDateDerniereModification(dm.toLocalDateTime());

        a.setCreePar(rs.getString("creePar"));
        a.setModifiePar(rs.getString("modifiePar"));

        return a;
    }

    // ==================== DOSSIER MEDICAL MODULE ====================

    public static DossierMedical mapDossierMedical(ResultSet rs) throws SQLException {
        DossierMedical d = new DossierMedical();
        d.setId(rs.getLong("id"));

        Patient p = new Patient();
        p.setId(rs.getLong("patient_id"));
        d.setPatient(p);

        Date dc = rs.getDate("date_creation");
        if (dc != null) d.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("date_derniere_modification");
        if (dm != null) d.setDateDerniereModification(dm.toLocalDateTime());

        d.setCreePar(rs.getString("cree_par"));
        d.setModifiePar(rs.getString("modifie_par"));

        return d;
    }

    public static Consultation mapConsultation(ResultSet rs) throws SQLException {
        Consultation c = new Consultation() {
            @Override
            public void setPatientId(long patientId) {
                // Implémentation vide
            }

            @Override
            public String getLibelle() {
                return "";
            }

            @Override
            public LocalDateTime getDateCharge() {
                return null;
            }

            @Override
            public String getCategorie() {
                return "";
            }

            @Override
            public String getModePaiement() {
                return "";
            }

            @Override
            public CabinetMedical getCabinet() {
                return null;
            }

            @Override
            public void setCreatedAt(LocalDateTime now) {

            }

            @Override
            public void setUpdatedAt(LocalDateTime now) {

            }

            @Override
            public void setPrenom(String test) {

            }

            @Override
            public void setNom(String nom) {

            }

            @Override
            public void setAdresse(String adresse) {

            }

            @Override
            public void setTel1(String tel1) {

            }

            @Override
            public void setEmail(String email) {

            }

            @Override
            public void setSiteWeb(String siteWeb) {

            }

            @Override
            public void setLogo(String logo) {

            }

            @Override
            public Object getNom() {
                return null;
            }

            @Override
            public Object getEmail() {
                return null;
            }

            @Override
            public Object getAdresse() {
                return null;
            }

            @Override
            public void setTelephone(String telephone) {

            }

            @Override
            public void setPoste(String poste) {

            }

            @Override
            public void setDateEmbauche(LocalDate localDate) {

            }

            @Override
            public void setRole(RoleType roleType) {

            }

            @Override
            public void setMontantRestant(double montantRestant) {

            }

            @Override
            public void setNiveauAcces(int niveauAcces) {

            }

            @Override
            public void setNumeroOrdre(String numeroOrdre) {

            }

            @Override
            public void setDepartement(String departement) {

            }

            @Override
            public void setDescription(String description) {

            }

            @Override
            public void setPermissions(List<String> list) {

            }

            @Override
            public void setNumero(String numero) {

            }

            @Override
            public void setMontantTotal(double montantTotal) {

            }

            @Override
            public void setDateEcheance(LocalDate localDate) {

            }

            @Override
            public void setMontantPaye(double montantPaye) {

            }

            @Override
            public void setTitre(String titre) {

            }
        };
        c.setId(rs.getLong("id"));
        Date dateC = rs.getDate("date_consultation");
        if (dateC != null) c.setDateConsultation(dateC.toLocalDate());

        String statutStr = rs.getString("statut");
        if (statutStr != null) c.setStatut(StatutConsultation.valueOf(statutStr));

        c.setObservationMedecin(rs.getString("observation_medecin"));

        DossierMedical d = new DossierMedical();
        d.setId(rs.getLong("dossier_medical_id"));
        c.setDossierMedical(d);

        Medecin m = new Medecin();
        m.setId(rs.getLong("medecin_id"));
        c.setMedecin(m);

        Date dc = rs.getDate("date_creation");
        if (dc != null) c.setDateCreation(dc.toLocalDate());
        Timestamp dm = rs.getTimestamp("date_derniere_modification");
        if (dm != null) c.setDateDerniereModification(dm.toLocalDateTime());
        c.setCreePar(rs.getString("cree_par"));
        c.setModifiePar(rs.getString("modifie_par"));

        return c;
    }

    public static Ordonnance mapOrdonnance(ResultSet rs) throws SQLException {
        Ordonnance o = new Ordonnance();
        o.setId(rs.getLong("id"));

        Date date = rs.getDate("date");
        if (date != null) o.setDate(date.toLocalDate());

        DossierMedical d = new DossierMedical();
        d.setId(rs.getLong("dossier_medical_id"));
        o.setDossierMedical(d);

        Medecin m = new Medecin();
        m.setId(rs.getLong("medecin_id"));
        o.setMedecin(m);

        Date dc = rs.getDate("date_creation");
        if (dc != null) o.setDateCreation(dc.toLocalDate());
        Timestamp dm = rs.getTimestamp("date_derniere_modification");
        if (dm != null) o.setDateDerniereModification(dm.toLocalDateTime());
        o.setCreePar(rs.getString("cree_par"));
        o.setModifiePar(rs.getString("modifie_par"));

        return o;
    }

    public static Medicament mapMedicament(ResultSet rs) throws SQLException {
        Medicament m = new Medicament();
        m.setId(rs.getLong("id"));
        m.setNom(rs.getString("nom"));
        m.setLaboratoire(rs.getString("laboratoire"));
        m.setType(rs.getString("type"));

        String formeStr = rs.getString("forme");
        if (formeStr != null) m.setForme(FormeMedicament.valueOf(formeStr));

        m.setRemboursable(rs.getBoolean("remboursable"));
        m.setPrixUnitaire(rs.getDouble("prix_unitaire"));
        m.setDescription(rs.getString("description"));

        Date dc = rs.getDate("date_creation");
        if (dc != null) m.setDateCreation(dc.toLocalDate());
        Timestamp dm = rs.getTimestamp("date_derniere_modification");
        if (dm != null) m.setDateDerniereModification(dm.toLocalDateTime());
        m.setCreePar(rs.getString("cree_par"));
        m.setModifiePar(rs.getString("modifie_par"));

        return m;
    }

    public static Prescription mapPrescription(ResultSet rs) throws SQLException {
        Prescription p = new Prescription();
        p.setId(rs.getLong("id"));
        p.setQuantite(rs.getInt("quantite"));
        p.setFrequence(rs.getString("frequence"));
        p.setDureeEnJours(rs.getInt("duree_en_jours"));

        Ordonnance o = new Ordonnance();
        o.setId(rs.getLong("ordonnance_id"));
        p.setOrdonnance(o);

        Medicament m = new Medicament();
        m.setId(rs.getLong("medicament_id"));
        p.setMedicament(m);

        Date dc = rs.getDate("date_creation");
        if (dc != null) p.setDateCreation(dc.toLocalDate());
        Timestamp dm = rs.getTimestamp("date_derniere_modification");
        if (dm != null) p.setDateDerniereModification(dm.toLocalDateTime());
        p.setCreePar(rs.getString("cree_par"));
        p.setModifiePar(rs.getString("modifie_par"));

        return p;
    }

    public static Certificat mapCertificat(ResultSet rs) throws SQLException {
        Certificat c = new Certificat();
        c.setId(rs.getLong("id"));
        c.setDateDebut(rs.getDate("date_debut").toLocalDate());
        c.setDateFin(rs.getDate("date_fin").toLocalDate());
        c.setDuree(rs.getInt("duree"));
        c.setNoteMedecin(rs.getString("note_medecin"));

        DossierMedical d = new DossierMedical();
        d.setId(rs.getLong("dossier_medical_id"));
        c.setDossierMedical(d);

        Medecin m = new Medecin();
        m.setId(rs.getLong("medecin_id"));
        c.setMedecin(m);

        Date dc = rs.getDate("date_creation");
        if (dc != null) c.setDateCreation(dc.toLocalDate());
        Timestamp dm = rs.getTimestamp("date_derniere_modification");
        if (dm != null) c.setDateDerniereModification(dm.toLocalDateTime());
        c.setCreePar(rs.getString("cree_par"));
        c.setModifiePar(rs.getString("modifie_par"));

        return c;
    }

    public static InterventionMedecin mapInterventionMedecin(ResultSet rs) throws SQLException {
        InterventionMedecin im = new InterventionMedecin();
        im.setId(rs.getLong("id"));
        im.setPrixPatient(rs.getDouble("prix_patient"));
        im.setNumDent(rs.getInt("num_dent"));

        Consultation c = new Consultation() {
            @Override
            public void setPatientId(long patientId) {
            }

            @Override
            public String getLibelle() {
                return "";
            }

            @Override
            public LocalDateTime getDateCharge() {
                return null;
            }

            @Override
            public String getCategorie() {
                return "";
            }

            @Override
            public String getModePaiement() {
                return "";
            }

            @Override
            public CabinetMedical getCabinet() {
                return null;
            }

            @Override
            public void setCreatedAt(LocalDateTime now) {

            }

            @Override
            public void setUpdatedAt(LocalDateTime now) {

            }

            @Override
            public void setPrenom(String test) {

            }

            @Override
            public void setNom(String nom) {

            }

            @Override
            public void setAdresse(String adresse) {

            }

            @Override
            public void setTel1(String tel1) {

            }

            @Override
            public void setEmail(String email) {

            }

            @Override
            public void setSiteWeb(String siteWeb) {

            }

            @Override
            public void setLogo(String logo) {

            }

            @Override
            public Object getNom() {
                return null;
            }

            @Override
            public Object getEmail() {
                return null;
            }

            @Override
            public Object getAdresse() {
                return null;
            }

            @Override
            public void setTelephone(String telephone) {

            }

            @Override
            public void setPoste(String poste) {

            }

            @Override
            public void setDateEmbauche(LocalDate localDate) {

            }

            @Override
            public void setRole(RoleType roleType) {

            }

            @Override
            public void setMontantRestant(double montantRestant) {

            }

            @Override
            public void setNiveauAcces(int niveauAcces) {

            }

            @Override
            public void setNumeroOrdre(String numeroOrdre) {

            }

            @Override
            public void setDepartement(String departement) {

            }

            @Override
            public void setDescription(String description) {

            }

            @Override
            public void setPermissions(List<String> list) {

            }

            @Override
            public void setNumero(String numero) {

            }

            @Override
            public void setMontantTotal(double montantTotal) {

            }

            @Override
            public void setDateEcheance(LocalDate localDate) {

            }

            @Override
            public void setMontantPaye(double montantPaye) {

            }

            @Override
            public void setTitre(String titre) {

            }
        };
        c.setId(rs.getLong("consultation_id"));
        im.setConsultation(c);

        Acte a = new Acte();
        a.setId(rs.getLong("acte_id"));
        im.setActe(a);

        Date dc = rs.getDate("date_creation");
        if (dc != null) im.setDateCreation(dc.toLocalDate());
        Timestamp dm = rs.getTimestamp("date_derniere_modification");
        if (dm != null) im.setDateDerniereModification(dm.toLocalDateTime());
        im.setCreePar(rs.getString("cree_par"));
        im.setModifiePar(rs.getString("modifie_par"));

        return im;
    }

    public static SituationFinanciere mapSituationFinanciere(ResultSet rs) throws SQLException {
        SituationFinanciere sf = new SituationFinanciere();
        sf.setId(rs.getLong("id"));
        sf.setTotaleDesActes(rs.getDouble("totale_des_actes"));
        sf.setTotalePaye(rs.getDouble("totale_paye"));
        sf.setCredit(rs.getDouble("credit"));

        String statutStr = rs.getString("statut");
        if (statutStr != null) sf.setStatut(StatutSituationFinanciere.valueOf(statutStr));

        sf.setEnPromo(rs.getBoolean("en_promo"));

        DossierMedical d = new DossierMedical();
        d.setId(rs.getLong("dossier_medical_id"));
        sf.setDossierMedical(d);

        Date dc = rs.getDate("date_creation");
        if (dc != null) sf.setDateCreation(dc.toLocalDate());
        Timestamp dm = rs.getTimestamp("date_derniere_modification");
        if (dm != null) sf.setDateDerniereModification(dm.toLocalDateTime());
        sf.setCreePar(rs.getString("cree_par"));
        sf.setModifiePar(rs.getString("modifie_par"));

        return sf;
    }

    public static Acte mapActe(ResultSet rs) throws SQLException {
        Acte a = new Acte();
        a.setId(rs.getLong("id"));
        a.setLibelle(rs.getString("libelle"));
        a.setCategorie(rs.getString("categorie"));
        a.setPrixBase(rs.getDouble("prix_base"));

        Date dc = rs.getDate("date_creation");
        if (dc != null) a.setDateCreation(dc.toLocalDate());
        Timestamp dm = rs.getTimestamp("date_derniere_modification");
        if (dm != null) a.setDateDerniereModification(dm.toLocalDateTime());
        a.setCreePar(rs.getString("cree_par"));
        a.setModifiePar(rs.getString("modifie_par"));

        return a;
    }

    // ==================== CABINET MODULE ====================

    public static CabinetMedical mapToCabinetMedical(ResultSet rs) throws SQLException {
        CabinetMedical cabinet = new CabinetMedical();
        cabinet.setId(rs.getLong("id"));
        cabinet.setNom(rs.getString("nom"));
        cabinet.setEmail(rs.getString("email"));
        cabinet.setLogo(rs.getString("logo"));
        cabinet.setAdresse(rs.getString("adresse"));
        cabinet.setCin(rs.getString("cin"));
        cabinet.setTel1(rs.getString("tel1"));
        cabinet.setTel2(rs.getString("tel2"));
        cabinet.setSiteWeb(rs.getString("site_web"));
        cabinet.setInstagram(rs.getString("instagram"));
        cabinet.setFacebook(rs.getString("facebook"));
        cabinet.setDescription(rs.getString("description"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            cabinet.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            cabinet.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return cabinet;
    }

    public static Charges mapToCharges(ResultSet rs) throws SQLException {
        Charges charge = new Charges();
        charge.setId(rs.getLong("id"));
        charge.setTitre(rs.getString("titre"));
        charge.setDescription(rs.getString("description"));
        charge.setMontant(rs.getDouble("montant"));

        Timestamp date = rs.getTimestamp("date");
        if (date != null) {
            charge.setDate(date.toLocalDateTime());
        }

        CabinetMedical cabinet = new CabinetMedical();
        cabinet.setId(rs.getLong("cabinet_id"));
        charge.setCabinet(cabinet);

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            charge.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            charge.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return charge;
    }

    public static Revenues mapToRevenues(ResultSet rs) throws SQLException {
        Revenues revenue = new Revenues();
        revenue.setId(rs.getLong("id"));
        revenue.setTitre(rs.getString("titre"));
        revenue.setDescription(rs.getString("description"));
        revenue.setMontant(rs.getDouble("montant"));

        Timestamp date = rs.getTimestamp("date");
        if (date != null) {
            revenue.setDate(date.toLocalDateTime());
        }

        CabinetMedical cabinet = new CabinetMedical();
        cabinet.setId(rs.getLong("cabinet_id"));
        revenue.setCabinet(cabinet);

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            revenue.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            revenue.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return revenue;
    }

    public static Statistiques mapToStatistiques(ResultSet rs) throws SQLException {
        Statistiques statistique = new Statistiques();
        statistique.setId(rs.getLong("id"));
        statistique.setNom(rs.getString("nom"));

        String categorieStr = rs.getString("categorie");
        if (categorieStr != null) {
            statistique.setCategorie(CategorieStatistique.valueOf(categorieStr));
        }

        statistique.setChiffre(rs.getDouble("chiffre"));

        Date dateCalcul = rs.getDate("date_calcul");
        if (dateCalcul != null) {
            statistique.setDateCalcul(dateCalcul.toLocalDate());
        }

        CabinetMedical cabinet = new CabinetMedical();
        cabinet.setId(rs.getLong("cabinet_id"));
        statistique.setCabinet(cabinet);

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            statistique.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            statistique.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return statistique;
    }

    // ==================== USERS MODULE ====================

    public static Staff mapToStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setId(rs.getLong("id"));
        staff.setNom(rs.getString("nom"));
        staff.setPrenom(rs.getString("prenom"));
        staff.setEmail(rs.getString("email"));
        staff.setTelephone(rs.getString("telephone"));
        staff.setPoste(rs.getString("poste"));
        staff.setSalaire(rs.getDouble("salaire"));

        Date dateEmbauche = rs.getDate("date_embauche");
        if (dateEmbauche != null) {
            staff.setDateEmbauche(dateEmbauche.toLocalDate());
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            staff.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            staff.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return staff;
    }

    public static Utilisateur mapToUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(rs.getLong("id"));
        utilisateur.setNom(rs.getString("nom"));
        utilisateur.setPrenom(rs.getString("prenom"));
        utilisateur.setEmail(rs.getString("email"));
        utilisateur.setTelephone(rs.getString("telephone"));

        String roleStr = rs.getString("role");
        if (roleStr != null) {
            utilisateur.setRole(RoleType.valueOf(roleStr));
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            utilisateur.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            utilisateur.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return utilisateur;
    }

    public static Medecin mapToMedecin(ResultSet rs) throws SQLException {
        Medecin medecin = new Medecin();
        medecin.setId(rs.getLong("id"));
        medecin.setNom(rs.getString("nom"));
        medecin.setPrenom(rs.getString("prenom"));
        medecin.setEmail(rs.getString("email"));
        medecin.setTelephone(rs.getString("telephone"));
        medecin.setSpecialite(rs.getString("specialite"));
        medecin.setNumeroOrdre(rs.getString("numero_ordre"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            medecin.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            medecin.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return medecin;
    }

    public static Secretaire mapToSecretaire(ResultSet rs) throws SQLException {
        Secretaire secretaire = new Secretaire();
        secretaire.setId(rs.getLong("id"));
        secretaire.setNom(rs.getString("nom"));
        secretaire.setPrenom(rs.getString("prenom"));
        secretaire.setEmail(rs.getString("email"));
        secretaire.setTelephone(rs.getString("telephone"));
        secretaire.setDepartement(rs.getString("departement"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            secretaire.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            secretaire.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return secretaire;
    }

    public static Admin mapToAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getLong("id"));
        admin.setNom(rs.getString("nom"));
        admin.setPrenom(rs.getString("prenom"));
        admin.setEmail(rs.getString("email"));
        admin.setTelephone(rs.getString("telephone"));
        admin.setNiveauAcces(rs.getInt("niveau_acces"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            admin.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            admin.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return admin;
    }

    public static Notification mapToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getLong("id"));
        notification.setTitre(rs.getString("titre"));
        notification.setMessage(rs.getString("message"));

        String typeStr = rs.getString("type");
        if (typeStr != null) {
            notification.setType(TypeNotification.valueOf(typeStr));
        }

        String prioriteStr = rs.getString("priorite");
        if (prioriteStr != null) {
            notification.setPriorite(PrioriteNotification.valueOf(prioriteStr));
        }

        notification.setLue(rs.getBoolean("lue"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            notification.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            notification.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return notification;
    }

    public static Role mapToRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong("id"));
        role.setNom(rs.getString("nom"));
        role.setDescription(rs.getString("description"));

        String permissionsStr = rs.getString("permissions");
        if (permissionsStr != null && !permissionsStr.isEmpty()) {
            role.setPermissions(Arrays.asList(permissionsStr.split(",")));
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            role.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            role.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return role;
    }

    // ==================== AGENDA MODULE ====================

    public static AgendaMensuel mapToAgendaMensuel(ResultSet rs) throws SQLException {
        AgendaMensuel agenda = new AgendaMensuel();
        agenda.setId(rs.getLong("id"));

        String moisStr = rs.getString("mois");
        if (moisStr != null) {
            agenda.setMois(Mois.valueOf(moisStr));
        }

        agenda.setAnnee(rs.getInt("annee"));
        agenda.setMedecinId(rs.getLong("medecin_id"));

        return agenda;
    }

    public static RDV mapToRDV(ResultSet rs) throws SQLException {
        RDV rdv = new RDV();
        rdv.setId(rs.getLong("id"));

        Date dateRdv = rs.getDate("date_rdv");
        if (dateRdv != null) {
            rdv.setDate(dateRdv.toLocalDate());
        }

        Time heureRdv = rs.getTime("heure");
        if (heureRdv != null) {
            rdv.setHeure(heureRdv.toLocalTime());
        }

        rdv.setMotif(rs.getString("motif"));

        String statutStr = rs.getString("statut");
        if (statutStr != null) {
            rdv.setStatut(StatutRDV.valueOf(statutStr));
        }

        rdv.setNoteMedecin(rs.getString("note_medecin"));
        rdv.setAgendaMensuelId(rs.getLong("agenda_mensuel_id"));

        Patient patient = new Patient();
        patient.setId(rs.getLong("patient_id"));
        rdv.setPatient(patient);

        Medecin medecin = new Medecin();
        medecin.setId(rs.getLong("medecin_id"));
        rdv.setMedecin(medecin);

        DossierMedical dossier = new DossierMedical();
        dossier.setId(rs.getLong("dossier_medical_id"));
        rdv.setDossierMedical(dossier);

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            rdv.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            rdv.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return rdv;
    }

    // ==================== FACTURATION MODULE ====================

    public static Facture mapToFacture(ResultSet rs) throws SQLException {
        Facture facture = new Facture();
        facture.setId(rs.getLong("id"));
        facture.setNumero(rs.getString("numero"));
        facture.setMontantTotal(rs.getDouble("montant_total"));
        facture.setMontantPaye(rs.getDouble("montant_paye"));
        facture.setMontantRestant(rs.getDouble("montant_restant"));

        String statutStr = rs.getString("statut");
        if (statutStr != null) {
            facture.setStatut(StatutFacture.valueOf(statutStr));
        }

        Date dateFacture = rs.getDate("date_facture");
        if (dateFacture != null) {
            facture.setDateFacture(dateFacture.toLocalDate().atStartOfDay());
        }

        Date dateEcheance = rs.getDate("date_echeance");
        if (dateEcheance != null) {
            facture.setDateEcheance(dateEcheance.toLocalDate());
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            facture.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            facture.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return facture;
    }
}