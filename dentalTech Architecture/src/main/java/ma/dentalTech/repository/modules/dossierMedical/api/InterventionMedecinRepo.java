package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface InterventionMedecinRepo extends CrudRepository<InterventionMedecin, Long> {

    List<InterventionMedecin> findByConsultationId(Long consultationId);
    List<InterventionMedecin> findByActeId(Long acteId);
    List<InterventionMedecin> findByNumDent(Integer numDent);
    List<InterventionMedecin> findByPrixPatientBetween(Double min, Double max);

    // Méthodes utilitaires
    double calculerTotalInterventionsConsultation(Long consultationId);
    long countInterventionsByActe(Long acteId);
}
