package ma.dentalTech.repository.modules.facturation.api;

import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.entities.enums.StatutFacture;

import java.util.List;
import java.util.Optional;

public interface FacturationRepo {

    /* ===== CRUD ===== */

    Facture save(Facture facture);

    Optional<Facture> findById(Long id);

    List<Facture> findAll();

    void deleteById(Long id);

    /* ===== Recherches métier ===== */

    List<Facture> findBySituationFinanciere(Long situationFinanciereId);

    List<Facture> findByStatut(StatutFacture statut);

    List<Facture> findImpaye();

    void updateStatut(Long factureId, StatutFacture statut);

    void updateMontantPaye(Long factureId, Double montantPaye);
}
