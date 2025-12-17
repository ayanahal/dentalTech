package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.SituationFinanciere;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SituationFinanciereRepo extends CrudRepository<SituationFinanciere, Long> {

    Optional<SituationFinanciere> findByDossierMedicalId(Long dossierId);
    List<SituationFinanciere> findByStatut(StatutSituationFinanciere statut);
    List<SituationFinanciere> findByCreditGreaterThan(Double montant);
    List<SituationFinanciere> findByEnPromo(boolean enPromo);

    // Méthodes utilitaires
    void updateCredit(Long situationId, Double nouveauCredit);
    void updateStatut(Long situationId, StatutSituationFinanciere statut);
    double calculerTotalCredit();
}
