package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.SituationFinanciere;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import java.util.List;
import java.util.Optional;

public interface SituationFinanciereService {

    SituationFinanciere createSituationFinanciere(SituationFinanciere situation);
    SituationFinanciere updateSituationFinanciere(SituationFinanciere situation);
    void deleteSituationFinanciere(Long id);
    Optional<SituationFinanciere> findSituationFinanciereById(Long id);
    List<SituationFinanciere> findAllSituationsFinancieres();

    Optional<SituationFinanciere> findSituationFinanciereByDossierId(Long dossierId);
    List<SituationFinanciere> findSituationsByStatut(StatutSituationFinanciere statut);
    List<SituationFinanciere> findSituationsByCreditGreaterThan(Double montant);
    List<SituationFinanciere> findSituationsEnPromo();

    // Méthodes métier
    void updateCreditSituation(Long situationId, Double nouveauCredit);
    void updateStatutSituation(Long situationId, StatutSituationFinanciere statut);
    double calculerResteAPayer(Long situationId);
    double calculerTotalCredit();
    double calculerTotalEncaissements();
    void appliquerPromotion(Long situationId, Double pourcentageReduction);
}