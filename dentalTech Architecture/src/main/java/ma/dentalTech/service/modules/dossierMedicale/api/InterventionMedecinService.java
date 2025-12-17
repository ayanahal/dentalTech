package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import java.util.List;
import java.util.Optional;

public interface InterventionMedecinService {

    InterventionMedecin createIntervention(InterventionMedecin intervention);
    InterventionMedecin updateIntervention(InterventionMedecin intervention);
    void deleteIntervention(Long id);
    Optional<InterventionMedecin> findInterventionById(Long id);
    List<InterventionMedecin> findAllInterventions();

    List<InterventionMedecin> findInterventionsByConsultationId(Long consultationId);
    List<InterventionMedecin> findInterventionsByActeId(Long acteId);
    List<InterventionMedecin> findInterventionsByNumDent(Integer numDent);
    List<InterventionMedecin> findInterventionsByPrixBetween(Double min, Double max);

    // Méthodes métier
    double calculerTotalInterventionsConsultation(Long consultationId);
    long countInterventionsByActe(Long acteId);
    Integer getDentLaPlusTraitee(Long dossierId);
    double getCoutMoyenIntervention();
}