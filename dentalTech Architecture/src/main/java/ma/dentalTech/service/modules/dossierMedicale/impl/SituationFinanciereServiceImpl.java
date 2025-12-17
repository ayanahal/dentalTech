package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.SituationFinanciere;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import ma.dentalTech.repository.modules.dossierMedical.api.SituationFinanciereRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.SituationFinanciereService;
import java.util.List;
import java.util.Optional;

public class SituationFinanciereServiceImpl implements SituationFinanciereService {

    private final SituationFinanciereRepo situationRepo;

    public SituationFinanciereServiceImpl() {
        this.situationRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.SituationFinanciereRepositoryImpl();
    }

    @Override
    public SituationFinanciere createSituationFinanciere(SituationFinanciere situation) {
        // Validation
        if (situation.getDossierMedical() == null || situation.getDossierMedical().getId() == null) {
            throw new IllegalArgumentException("La situation financière doit être associée à un dossier médical");
        }

        if (situation.getTotaleDesActes() == null || situation.getTotaleDesActes() < 0) {
            throw new IllegalArgumentException("Le total des actes doit être positif");
        }

        if (situation.getTotalePaye() == null || situation.getTotalePaye() < 0) {
            throw new IllegalArgumentException("Le total payé doit être positif");
        }

        if (situation.getCredit() == null || situation.getCredit() < 0) {
            throw new IllegalArgumentException("Le crédit doit être positif");
        }

        if (situation.getStatut() == null) {
            situation.setStatut(StatutSituationFinanciere.SOLDE);
        }

        // Calculer automatiquement le crédit si non fourni
        if (situation.getCredit() == 0.0) {
            double reste = situation.getTotaleDesActes() - situation.getTotalePaye();
            situation.setCredit(Math.max(0, reste));
        }

        // Mettre à jour le statut selon le crédit
        if (situation.getCredit() > 0) {
            situation.setStatut(StatutSituationFinanciere.EN_RETARD);
        }

        situationRepo.create(situation);
        return situation;
    }

    @Override
    public SituationFinanciere updateSituationFinanciere(SituationFinanciere situation) {
        if (situation.getId() == null) {
            throw new IllegalArgumentException("L'ID de la situation financière est requis");
        }

        SituationFinanciere existing = situationRepo.findById(situation.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Situation financière non trouvée avec ID: " + situation.getId());
        }

        situationRepo.update(situation);
        return situation;
    }

    @Override
    public void deleteSituationFinanciere(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de la situation financière est requis");
        }

        situationRepo.deleteById(id);
    }

    @Override
    public Optional<SituationFinanciere> findSituationFinanciereById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(situationRepo.findById(id));
    }

    @Override
    public List<SituationFinanciere> findAllSituationsFinancieres() {
        return situationRepo.findAll();
    }

    @Override
    public Optional<SituationFinanciere> findSituationFinanciereByDossierId(Long dossierId) {
        if (dossierId == null) {
            return Optional.empty();
        }

        return situationRepo.findByDossierMedicalId(dossierId);
    }

    @Override
    public List<SituationFinanciere> findSituationsByStatut(StatutSituationFinanciere statut) {
        if (statut == null) {
            throw new IllegalArgumentException("Le statut est requis");
        }

        return situationRepo.findByStatut(statut);
    }

    @Override
    public List<SituationFinanciere> findSituationsByCreditGreaterThan(Double montant) {
        if (montant == null) {
            throw new IllegalArgumentException("Le montant est requis");
        }

        return situationRepo.findByCreditGreaterThan(montant);
    }

    @Override
    public List<SituationFinanciere> findSituationsEnPromo() {
        return situationRepo.findByEnPromo(true);
    }

    @Override
    public void updateCreditSituation(Long situationId, Double nouveauCredit) {
        if (situationId == null || nouveauCredit == null) {
            throw new IllegalArgumentException("L'ID et le nouveau crédit sont requis");
        }

        if (nouveauCredit < 0) {
            throw new IllegalArgumentException("Le crédit doit être positif");
        }

        situationRepo.updateCredit(situationId, nouveauCredit);

        // Mettre à jour le statut selon le nouveau crédit
        if (nouveauCredit > 0) {
            updateStatutSituation(situationId, StatutSituationFinanciere.EN_RETARD);
        } else {
            updateStatutSituation(situationId, StatutSituationFinanciere.SOLDE);
        }
    }

    @Override
    public void updateStatutSituation(Long situationId, StatutSituationFinanciere statut) {
        if (situationId == null || statut == null) {
            throw new IllegalArgumentException("L'ID et le statut sont requis");
        }

        situationRepo.updateStatut(situationId, statut);
    }

    @Override
    public double calculerResteAPayer(Long situationId) {
        if (situationId == null) {
            return 0.0;
        }

        SituationFinanciere situation = situationRepo.findById(situationId);
        if (situation == null) {
            return 0.0;
        }

        return situation.getReste();
    }

    @Override
    public double calculerTotalCredit() {
        return situationRepo.calculerTotalCredit();
    }

    @Override
    public double calculerTotalEncaissements() {
        List<SituationFinanciere> situations = findAllSituationsFinancieres();
        double total = 0.0;

        for (SituationFinanciere situation : situations) {
            if (situation.getTotalePaye() != null) {
                total += situation.getTotalePaye();
            }
        }

        return total;
    }

    @Override
    public void appliquerPromotion(Long situationId, Double pourcentageReduction) {
        if (situationId == null || pourcentageReduction == null) {
            throw new IllegalArgumentException("L'ID et le pourcentage de réduction sont requis");
        }

        if (pourcentageReduction < 0 || pourcentageReduction > 100) {
            throw new IllegalArgumentException("Le pourcentage doit être entre 0 et 100");
        }

        SituationFinanciere situation = situationRepo.findById(situationId);
        if (situation == null) {
            throw new IllegalArgumentException("Situation financière non trouvée avec ID: " + situationId);
        }

        // Appliquer la réduction au total des actes
        Double nouveauTotal = situation.getTotaleDesActes() * (1 - pourcentageReduction / 100);
        situation.setTotaleDesActes(nouveauTotal);

        // Recalculer le crédit
        double nouveauCredit = Math.max(0, nouveauTotal - situation.getTotalePaye());
        situation.setCredit(nouveauCredit);

        // Activer la promotion
        situation.setEnPromo(true);

        situationRepo.update(situation);
    }

    // Méthode supplémentaire : enregistrer un paiement
    public void enregistrerPaiement(Long situationId, Double montantPaiement) {
        if (situationId == null || montantPaiement == null) {
            throw new IllegalArgumentException("L'ID et le montant du paiement sont requis");
        }

        if (montantPaiement <= 0) {
            throw new IllegalArgumentException("Le montant du paiement doit être positif");
        }

        SituationFinanciere situation = situationRepo.findById(situationId);
        if (situation == null) {
            throw new IllegalArgumentException("Situation financière non trouvée avec ID: " + situationId);
        }

        // Mettre à jour le total payé
        Double nouveauTotalPaye = situation.getTotalePaye() + montantPaiement;
        situation.setTotalePaye(nouveauTotalPaye);

        // Recalculer le crédit
        Double nouveauCredit = Math.max(0, situation.getTotaleDesActes() - nouveauTotalPaye);
        situation.setCredit(nouveauCredit);

        // Mettre à jour le statut
        if (nouveauCredit == 0) {
            situation.setStatut(StatutSituationFinanciere.SOLDE);
        } else if (nouveauCredit > 0) {
            situation.setStatut(StatutSituationFinanciere.EN_RETARD);
        }

        situationRepo.update(situation);
    }

    // Méthode supplémentaire : générer un rapport financier
    public Map<String, Object> genererRapportFinancier() {
        Map<String, Object> rapport = new HashMap<>();

        List<SituationFinanciere> situations = findAllSituationsFinancieres();

        double totalActes = 0.0;
        double totalPaye = 0.0;
        double totalCredit = 0.0;
        int nombrePatientsSolde = 0;
        int nombrePatientsEnRetard = 0;

        for (SituationFinanciere situation : situations) {
            if (situation.getTotaleDesActes() != null) {
                totalActes += situation.getTotaleDesActes();
            }

            if (situation.getTotalePaye() != null) {
                totalPaye += situation.getTotalePaye();
            }

            if (situation.getCredit() != null) {
                totalCredit += situation.getCredit();
            }

            if (situation.getStatut() == StatutSituationFinanciere.SOLDE) {
                nombrePatientsSolde++;
            } else if (situation.getStatut() == StatutSituationFinanciere.EN_RETARD) {
                nombrePatientsEnRetard++;
            }
        }

        rapport.put("totalActes", totalActes);
        rapport.put("totalPaye", totalPaye);
        rapport.put("totalCredit", totalCredit);
        rapport.put("nombrePatientsSolde", nombrePatientsSolde);
        rapport.put("nombrePatientsEnRetard", nombrePatientsEnRetard);
        rapport.put("tauxEncaissement", totalActes > 0 ? (totalPaye / totalActes) * 100 : 0);

        return rapport;
    }
}