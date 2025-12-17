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

public final class RowMappers {

    private RowMappers(){}

    // ---------------- Patient & Antecedent ----------------
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

        p.setSexe(Sexe.valueOf(rs.getString("sexe")));
        p.setAssurance(Assurance.valueOf(rs.getString("assurance")));

        return p;
    }

    public static Antecedent mapAntecedent(ResultSet rs) throws SQLException {
        Antecedent a = new Antecedent();
        a.setId(rs.getLong("id"));
        a.setNom(rs.getString("nom"));
        a.setCategorie(CategorieAntecedent.valueOf(rs.getString("categorie")));
        a.setNiveauRisque(NiveauRisque.valueOf(rs.getString("niveauRisque")));

        Date dc = rs.getDate("dateCreation");
        if (dc != null) a.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) a.setDateDerniereModification(dm.toLocalDateTime());

        a.setCreePar(rs.getString("creePar"));
        a.setModifiePar(rs.getString("modifiePar"));

        return a;
    }

    // ---------------- DossierMedical ----------------
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

    // ---------------- Consultation ----------------
    public static Consultation mapConsultation(ResultSet rs) throws SQLException {
        Consultation c = new Consultation() {
            @Override
            public void setPatientId(long patientId) {

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

    // ---------------- Ordonnance ----------------
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

    // ---------------- Medicament ----------------
    public static Medicament mapMedicament(ResultSet rs) throws SQLException {
        Medicament m = new Medicament();
        m.setId(rs.getLong("id"));
        m.setNom(rs.getString("nom"));
        m.setLaboratoire(rs.getString("laboratoire"));
        m.setType(rs.getString("type"));
        m.setForme(FormeMedicament.valueOf(rs.getString("forme")));
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

    // ---------------- Prescription ----------------
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

    // ---------------- Certificat ----------------
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

    // ---------------- InterventionMedecin ----------------
    public static InterventionMedecin mapInterventionMedecin(ResultSet rs) throws SQLException {
        InterventionMedecin im = new InterventionMedecin();
        im.setId(rs.getLong("id"));
        im.setPrixPatient(rs.getDouble("prix_patient"));
        im.setNumDent(rs.getInt("num_dent"));

        Consultation c = new Consultation() {
            @Override
            public void setPatientId(long patientId) {

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

    // ---------------- SituationFinanciere ----------------
    public static SituationFinanciere mapSituationFinanciere(ResultSet rs) throws SQLException {
        SituationFinanciere sf = new SituationFinanciere();
        sf.setId(rs.getLong("id"));
        sf.setTotaleDesActes(rs.getDouble("totale_des_actes"));
        sf.setTotalePaye(rs.getDouble("totale_paye"));
        sf.setCredit(rs.getDouble("credit"));
        sf.setStatut(StatutSituationFinanciere.valueOf(rs.getString("statut")));
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

    // TODO : Acte mapping
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
}
