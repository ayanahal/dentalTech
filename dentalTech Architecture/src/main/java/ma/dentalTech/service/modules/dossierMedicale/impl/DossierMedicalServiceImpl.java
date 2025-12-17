package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.*;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import ma.dentalTech.repository.modules.dossierMedical.api.*;
import ma.dentalTech.service.modules.dossierMedicale.api.DossierMedicalService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DossierMedicalServiceImpl implements DossierMedicalService {

    private final DossierMedicalRepo dossierMedicalRepo;
    private final ConsultationRepo consultationRepo;
    private final OrdonnanceRepo ordonnanceRepo;
    private final CertificatRepo certificatRepo;
    private final SituationFinanciereRepo situationFinanciereRepo;
    private final MedicamentRepo medicamentRepo;
    private final InterventionMedecinRepo interventionRepo;
    private final PrescriptionRepo prescriptionRepo;
    private final ActeRepository acteRepo;

    // Constructeur avec injection des dépendances (à adapter selon votre système d'IoC)
    public DossierMedicalServiceImpl() {
        this.dossierMedicalRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.DossierMedicalRepositoryImpl();
        this.consultationRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl();
        this.ordonnanceRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.OrdonnanceRepositoryImpl();
        this.certificatRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.CertificatRepositoryImpl();
        this.situationFinanciereRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.SituationFinanciereRepositoryImpl();
        this.medicamentRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.MedicamentRepositoryImpl();
        this.interventionRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.InterventionMedecinRepositoryImpl();
        this.prescriptionRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.PrescriptionRepositoryImpl();
        this.acteRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.ActeRepositoryImpl();
    }

    // CRUD DossierMedical
    @Override
    public DossierMedical createDossierMedical(DossierMedical dossier) {
        // Validation
        if (dossier.getPatient() == null || dossier.getPatient().getId() == null) {
            throw new IllegalArgumentException("Le dossier doit être associé à un patient");
        }

        // Vérifier si le patient n'a pas déjà un dossier
        Optional<DossierMedical> existingDossier = dossierMedicalRepo.findByPatientId(dossier.getPatient().getId());
        if (existingDossier.isPresent()) {
            throw new IllegalStateException("Ce patient possède déjà un dossier médical");
        }

        // Date de création par défaut
        if (dossier.getDateCreation() == null) {
            dossier.setDateCreation(LocalDate.now());
        }

        dossierMedicalRepo.create(dossier);

        // Créer automatiquement une situation financière vide
        SituationFinanciere situation = SituationFinanciere.builder()
                .totaleDesActes(0.0)
                .totalePaye(0.0)
                .credit(0.0)
                .statut(StatutSituationFinanciere.SOLDE)
                .enPromo(false)
                .dossierMedical(dossier)
                .build();

        situationFinanciereRepo.create(situation);
        dossier.setSituationFinanciere(situation);

        return dossier;
    }

    @Override
    public DossierMedical updateDossierMedical(DossierMedical dossier) {
        if (dossier.getId() == null) {
            throw new IllegalArgumentException("L'ID du dossier est requis pour la mise à jour");
        }

        DossierMedical existing = dossierMedicalRepo.findById(dossier.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Dossier médical non trouvé avec ID: " + dossier.getId());
        }

        dossierMedicalRepo.update(dossier);
        return dossier;
    }

    @Override
    public void deleteDossierMedical(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du dossier est requis");
        }

        // Vérifier s'il existe des consultations associées
        List<Consultation> consultations = consultationRepo.findByDossierMedicalId(id);
        if (!consultations.isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer le dossier : il contient des consultations");
        }

        dossierMedicalRepo.deleteById(id);
    }

    @Override
    public Optional<DossierMedical> findDossierMedicalById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        DossierMedical dossier = dossierMedicalRepo.findById(id);
        if (dossier != null) {
            // Charger les relations
            dossier.setConsultations(consultationRepo.findByDossierMedicalId(id));
            dossier.setOrdonnances(ordonnanceRepo.findByDossierMedicalId(id));
            dossier.setCertificats(certificatRepo.findByDossierMedicalId(id));
            dossier.setSituationFinanciere(situationFinanciereRepo.findByDossierMedicalId(id).orElse(null));
        }

        return Optional.ofNullable(dossier);
    }

    @Override
    public Optional<DossierMedical> findDossierMedicalByPatientId(Long patientId) {
        if (patientId == null) {
            return Optional.empty();
        }

        Optional<DossierMedical> dossierOpt = dossierMedicalRepo.findByPatientId(patientId);
        if (dossierOpt.isPresent()) {
            DossierMedical dossier = dossierOpt.get();
            dossier.setConsultations(consultationRepo.findByDossierMedicalId(dossier.getId()));
            dossier.setOrdonnances(ordonnanceRepo.findByDossierMedicalId(dossier.getId()));
        }

        return dossierOpt;
    }

    @Override
    public List<DossierMedical> findAllDossiers() {
        List<DossierMedical> dossiers = dossierMedicalRepo.findAll();
        // Charger les consultations pour chaque dossier (optionnel, selon besoin)
        for (DossierMedical dossier : dossiers) {
            dossier.setConsultations(consultationRepo.findByDossierMedicalId(dossier.getId()));
        }
        return dossiers;
    }

    @Override
    public List<DossierMedical> searchDossiersByPatientNomPrenom(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllDossiers();
        }

        return dossierMedicalRepo.findByPatientNomPrenom(keyword);
    }

    // Gestion des consultations
    @Override
    public Consultation createConsultation(Consultation consultation) {
        if (consultation.getDossierMedical() == null || consultation.getDossierMedical().getId() == null) {
            throw new IllegalArgumentException("La consultation doit être associée à un dossier médical");
        }

        if (consultation.getMedecin() == null || consultation.getMedecin().getId() == null) {
            throw new IllegalArgumentException("La consultation doit être associée à un médecin");
        }

        if (consultation.getDateConsultation() == null) {
            consultation.setDateConsultation(LocalDate.now());
        }

        consultationRepo.create(consultation);
        return consultation;
    }

    @Override
    public Consultation updateConsultation(Consultation consultation) {
        if (consultation.getId() == null) {
            throw new IllegalArgumentException("L'ID de la consultation est requis");
        }

        Consultation existing = consultationRepo.findById(consultation.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Consultation non trouvée avec ID: " + consultation.getId());
        }

        consultationRepo.update(consultation);
        return consultation;
    }

    @Override
    public void deleteConsultation(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de la consultation est requis");
        }

        consultationRepo.deleteById(id);
    }

    @Override
    public Optional<Consultation> findConsultationById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        Consultation consultation = consultationRepo.findById(id);
        if (consultation != null) {
            consultation.setInterventions(interventionRepo.findByConsultationId(id));
        }

        return Optional.ofNullable(consultation);
    }

    @Override
    public List<Consultation> findConsultationsByDossierId(Long dossierId) {
        if (dossierId == null) {
            throw new IllegalArgumentException("L'ID du dossier est requis");
        }

        List<Consultation> consultations = consultationRepo.findByDossierMedicalId(dossierId);
        // Charger les interventions pour chaque consultation
        for (Consultation consultation : consultations) {
            consultation.setInterventions(interventionRepo.findByConsultationId(consultation.getId()));
        }
        return consultations;
    }

    @Override
    public List<Consultation> findConsultationsByMedecinId(Long medecinId) {
        if (medecinId == null) {
            throw new IllegalArgumentException("L'ID du médecin est requis");
        }

        return consultationRepo.findByMedecinId(medecinId);
    }

    @Override
    public List<Consultation> findConsultationsByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("La date est requise");
        }

        return consultationRepo.findByDate(date);
    }

    // Gestion des interventions
    @Override
    public InterventionMedecin createIntervention(InterventionMedecin intervention) {
        if (intervention.getActeId() == null) {
            throw new IllegalArgumentException("L'intervention doit être associée à un acte");
        }

        interventionRepo.create(intervention);
        return intervention;
    }

    @Override
    public void addInterventionToConsultation(Long consultationId, InterventionMedecin intervention) {
        if (consultationId == null) {
            throw new IllegalArgumentException("L'ID de la consultation est requis");
        }

        if (intervention.getId() == null) {
            // Créer l'intervention d'abord
            interventionRepo.create(intervention);
        }

        intervention.setConsultationId(consultationId);
        interventionRepo.update(intervention);
    }

    @Override
    public List<InterventionMedecin> getInterventionsByConsultationId(Long consultationId) {
        if (consultationId == null) {
            throw new IllegalArgumentException("L'ID de la consultation est requis");
        }

        return interventionRepo.findByConsultationId(consultationId);
    }

    @Override
    public double calculerCoutTotalConsultation(Long consultationId) {
        if (consultationId == null) {
            throw new IllegalArgumentException("L'ID de la consultation est requis");
        }

        return interventionRepo.calculerTotalInterventionsConsultation(consultationId);
    }

    // Gestion des ordonnances
    @Override
    public Ordonnance createOrdonnance(Ordonnance ordonnance) {
        if (ordonnance.getDossierMedical() == null || ordonnance.getDossierMedical().getId() == null) {
            throw new IllegalArgumentException("L'ordonnance doit être associée à un dossier médical");
        }

        if (ordonnance.getMedecin() == null || ordonnance.getMedecin().getId() == null) {
            throw new IllegalArgumentException("L'ordonnance doit être associée à un médecin");
        }

        if (ordonnance.getDate() == null) {
            ordonnance.setDate(LocalDate.now());
        }

        ordonnanceRepo.create(ordonnance);
        return ordonnance;
    }

    @Override
    public List<Ordonnance> getOrdonnancesByDossierId(Long dossierId) {
        if (dossierId == null) {
            throw new IllegalArgumentException("L'ID du dossier est requis");
        }

        List<Ordonnance> ordonnances = ordonnanceRepo.findByDossierMedicalId(dossierId);
        // Charger les prescriptions pour chaque ordonnance
        for (Ordonnance ordonnance : ordonnances) {
            ordonnance.setPrescriptions(prescriptionRepo.findByOrdonnanceId(ordonnance.getId()));
        }
        return ordonnances;
    }

    // Gestion des prescriptions
    @Override
    public Prescription createPrescription(Prescription prescription) {
        if (prescription.getOrdonnance() == null || prescription.getOrdonnance().getId() == null) {
            throw new IllegalArgumentException("La prescription doit être associée à une ordonnance");
        }

        if (prescription.getMedicament() == null || prescription.getMedicament().getId() == null) {
            throw new IllegalArgumentException("La prescription doit être associée à un médicament");
        }

        if (prescription.getQuantite() == null || prescription.getQuantite() <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }

        prescriptionRepo.create(prescription);
        return prescription;
    }

    @Override
    public void addPrescriptionToOrdonnance(Long ordonnanceId, Prescription prescription) {
        if (ordonnanceId == null) {
            throw new IllegalArgumentException("L'ID de l'ordonnance est requis");
        }

        prescription.getOrdonnance().setId(ordonnanceId);
        createPrescription(prescription);
    }

    @Override
    public List<Prescription> getPrescriptionsByOrdonnanceId(Long ordonnanceId) {
        if (ordonnanceId == null) {
            throw new IllegalArgumentException("L'ID de l'ordonnance est requis");
        }

        return prescriptionRepo.findByOrdonnanceId(ordonnanceId);
    }

    // Gestion des médicaments
    @Override
    public Medicament createMedicament(Medicament medicament) {
        if (medicament.getNom() == null || medicament.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du médicament est requis");
        }

        medicamentRepo.create(medicament);
        return medicament;
    }

    @Override
    public Optional<Medicament> findMedicamentById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(medicamentRepo.findById(id));
    }

    @Override
    public List<Medicament> searchMedicamentsByNom(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return medicamentRepo.findAll();
        }

        return medicamentRepo.findByNomContaining(keyword);
    }

    // Gestion des certificats
    @Override
    public Certificat createCertificat(Certificat certificat) {
        if (certificat.getDossierMedical() == null || certificat.getDossierMedical().getId() == null) {
            throw new IllegalArgumentException("Le certificat doit être associé à un dossier médical");
        }

        if (certificat.getMedecin() == null || certificat.getMedecin().getId() == null) {
            throw new IllegalArgumentException("Le certificat doit être associé à un médecin");
        }

        if (certificat.getDateDebut() == null) {
            throw new IllegalArgumentException("La date de début est requise");
        }

        if (certificat.getDateFin() == null) {
            throw new IllegalArgumentException("La date de fin est requise");
        }

        // Calculer la durée automatiquement
        if (certificat.getDuree() == null) {
            long jours = java.time.temporal.ChronoUnit.DAYS.between(
                    certificat.getDateDebut(),
                    certificat.getDateFin()
            );
            certificat.setDuree((int) jours + 1); // +1 pour inclure le jour de début
        }

        certificatRepo.create(certificat);
        return certificat;
    }

    @Override
    public List<Certificat> getCertificatsByDossierId(Long dossierId) {
        if (dossierId == null) {
            throw new IllegalArgumentException("L'ID du dossier est requis");
        }

        return certificatRepo.findByDossierMedicalId(dossierId);
    }

    @Override
    public boolean verifierValiditeCertificat(Long certificatId) {
        if (certificatId == null) {
            return false;
        }

        return certificatRepo.isCertificatValide(certificatId);
    }

    // Gestion de la situation financière
    @Override
    public SituationFinanciere createSituationFinanciere(SituationFinanciere situation) {
        if (situation.getDossierMedical() == null || situation.getDossierMedical().getId() == null) {
            throw new IllegalArgumentException("La situation financière doit être associée à un dossier médical");
        }

        // Vérifier s'il n'existe pas déjà une situation pour ce dossier
        Optional<SituationFinanciere> existing = situationFinanciereRepo.findByDossierMedicalId(
                situation.getDossierMedical().getId()
        );

        if (existing.isPresent()) {
            throw new IllegalStateException("Ce dossier possède déjà une situation financière");
        }

        situationFinanciereRepo.create(situation);
        return situation;
    }

    @Override
    public Optional<SituationFinanciere> findSituationFinanciereByDossierId(Long dossierId) {
        if (dossierId == null) {
            return Optional.empty();
        }

        return situationFinanciereRepo.findByDossierMedicalId(dossierId);
    }

    @Override
    public void updateStatutSituationFinanciere(Long situationId, StatutSituationFinanciere statut) {
        if (situationId == null) {
            throw new IllegalArgumentException("L'ID de la situation financière est requis");
        }

        if (statut == null) {
            throw new IllegalArgumentException("Le statut est requis");
        }

        situationFinanciereRepo.updateStatut(situationId, statut);
    }

    @Override
    public double calculerTotalDettePatient(Long dossierId) {
        if (dossierId == null) {
            return 0.0;
        }

        Optional<SituationFinanciere> situationOpt = situationFinanciereRepo.findByDossierMedicalId(dossierId);
        if (situationOpt.isPresent()) {
            SituationFinanciere situation = situationOpt.get();
            return situation.getReste();
        }

        return 0.0;
    }

    // Statistiques
    @Override
    public long countTotalDossiers() {
        return dossierMedicalRepo.countDossiers();
    }

    @Override
    public long countConsultationsByDossier(Long dossierId) {
        if (dossierId == null) {
            return 0;
        }

        List<Consultation> consultations = consultationRepo.findByDossierMedicalId(dossierId);
        return consultations.size();
    }

    @Override
    public double calculerChiffreAffairesMensuel(int mois, int annee) {
        // Implémentation simplifiée - à adapter selon votre logique métier
        // Ceci est un exemple
        LocalDate debutMois = LocalDate.of(annee, mois, 1);
        LocalDate finMois = debutMois.withDayOfMonth(debutMois.lengthOfMonth());

        List<Consultation> consultations = consultationRepo.findBetweenDates(debutMois, finMois);
        double total = 0.0;

        for (Consultation consultation : consultations) {
            total += interventionRepo.calculerTotalInterventionsConsultation(consultation.getId());
        }

        return total;
    }
}