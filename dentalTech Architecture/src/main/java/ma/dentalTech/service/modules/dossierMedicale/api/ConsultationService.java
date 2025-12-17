package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.entities.enums.StatutConsultation;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConsultationService {

    Consultation createConsultation(Consultation consultation);
    Consultation updateConsultation(Consultation consultation);
    void deleteConsultation(Long id);
    Optional<Consultation> findConsultationById(Long id);
    List<Consultation> findAllConsultations();

    List<Consultation> findConsultationsByDossierMedicalId(Long dossierId);
    List<Consultation> findConsultationsByMedecinId(Long medecinId);
    List<Consultation> findConsultationsByDate(LocalDate date);
    List<Consultation> findConsultationsByStatut(StatutConsultation statut);
    List<Consultation> findConsultationsBetweenDates(LocalDate start, LocalDate end);

    // Gestion des interventions
    InterventionMedecin addInterventionToConsultation(Long consultationId, InterventionMedecin intervention);
    void removeInterventionFromConsultation(Long consultationId, Long interventionId);
    List<InterventionMedecin> getInterventionsOfConsultation(Long consultationId);
    double calculerCoutConsultation(Long consultationId);

    // Méthodes métier
    void changerStatutConsultation(Long consultationId, StatutConsultation nouveauStatut);
    boolean isConsultationComplete(Long consultationId);
    long countConsultationsByMedecin(Long medecinId);
}