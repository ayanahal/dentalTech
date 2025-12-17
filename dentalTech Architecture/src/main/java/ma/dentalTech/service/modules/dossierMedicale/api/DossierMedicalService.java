package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.*;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DossierMedicalService {

    // CRUD DossierMedical
    DossierMedical createDossierMedical(DossierMedical dossier);
    DossierMedical updateDossierMedical(DossierMedical dossier);
    void deleteDossierMedical(Long id);
    Optional<DossierMedical> findDossierMedicalById(Long id);
    Optional<DossierMedical> findDossierMedicalByPatientId(Long patientId);
    List<DossierMedical> findAllDossiers();
    List<DossierMedical> searchDossiersByPatientNomPrenom(String keyword);

    // Gestion des consultations
    Consultation createConsultation(Consultation consultation);
    Consultation updateConsultation(Consultation consultation);
    void deleteConsultation(Long id);
    Optional<Consultation> findConsultationById(Long id);
    List<Consultation> findConsultationsByDossierId(Long dossierId);
    List<Consultation> findConsultationsByMedecinId(Long medecinId);
    List<Consultation> findConsultationsByDate(LocalDate date);

    // Gestion des interventions
    InterventionMedecin createIntervention(InterventionMedecin intervention);
    void addInterventionToConsultation(Long consultationId, InterventionMedecin intervention);
    List<InterventionMedecin> getInterventionsByConsultationId(Long consultationId);
    double calculerCoutTotalConsultation(Long consultationId);

    // Gestion des ordonnances
    Ordonnance createOrdonnance(Ordonnance ordonnance);
    List<Ordonnance> getOrdonnancesByDossierId(Long dossierId);

    // Gestion des prescriptions
    Prescription createPrescription(Prescription prescription);
    void addPrescriptionToOrdonnance(Long ordonnanceId, Prescription prescription);
    List<Prescription> getPrescriptionsByOrdonnanceId(Long ordonnanceId);

    // Gestion des médicaments
    Medicament createMedicament(Medicament medicament);
    Optional<Medicament> findMedicamentById(Long id);
    List<Medicament> searchMedicamentsByNom(String keyword);

    // Gestion des certificats
    Certificat createCertificat(Certificat certificat);
    List<Certificat> getCertificatsByDossierId(Long dossierId);
    boolean verifierValiditeCertificat(Long certificatId);

    // Gestion de la situation financière
    SituationFinanciere createSituationFinanciere(SituationFinanciere situation);
    Optional<SituationFinanciere> findSituationFinanciereByDossierId(Long dossierId);
    void updateStatutSituationFinanciere(Long situationId, StatutSituationFinanciere statut);
    double calculerTotalDettePatient(Long dossierId);

    // Statistiques
    long countTotalDossiers();
    long countConsultationsByDossier(Long dossierId);
    double calculerChiffreAffairesMensuel(int mois, int annee);
}