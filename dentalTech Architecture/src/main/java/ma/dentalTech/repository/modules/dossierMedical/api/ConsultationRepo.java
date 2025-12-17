package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConsultationRepo extends CrudRepository<Consultation, Long> {

    List<Consultation> findByDossierMedicalId(Long dossierId);
    List<Consultation> findByMedecinId(Long medecinId);
    List<Consultation> findByDate(LocalDate date);
    List<Consultation> findByStatut(StatutConsultation statut);
    List<Consultation> findBetweenDates(LocalDate start, LocalDate end);

    // Méthodes pour les relations
    List<InterventionMedecin> getInterventionsOfConsultation(Long consultationId);
    void addInterventionToConsultation(Long consultationId, Long interventionId);
    void removeInterventionFromConsultation(Long consultationId, Long interventionId);
}
