package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService;
import java.util.*;
import java.util.stream.Collectors;

public class InterventionMedecinServiceImpl implements InterventionMedecinService {

    private final InterventionMedecinRepo interventionRepo;

    public InterventionMedecinServiceImpl() {
        this.interventionRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.InterventionMedecinRepositoryImpl();
    }

    @Override
    public InterventionMedecin createIntervention(InterventionMedecin intervention) {
        // Validation
        if (intervention.getActeId() == null) {
            throw new IllegalArgumentException("L'intervention doit être associée à un acte");
        }

        if (intervention.getPrixPatient() == null || intervention.getPrixPatient() < 0) {
            throw new IllegalArgumentException("Le prix patient doit être positif");
        }

        // Le numéro de dent est optionnel (certains actes ne concernent pas une dent spécifique)

        interventionRepo.create(intervention);
        return intervention;
    }

    @Override
    public InterventionMedecin updateIntervention(InterventionMedecin intervention) {
        if (intervention.getId() == null) {
            throw new IllegalArgumentException("L'ID de l'intervention est requis");
        }

        InterventionMedecin existing = interventionRepo.findById(intervention.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Intervention non trouvée avec ID: " + intervention.getId());
        }

        interventionRepo.update(intervention);
        return intervention;
    }

    @Override
    public void deleteIntervention(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de l'intervention est requis");
        }

        interventionRepo.deleteById(id);
    }

    @Override
    public Optional<InterventionMedecin> findInterventionById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(interventionRepo.findById(id));
    }

    @Override
    public List<InterventionMedecin> findAllInterventions() {
        return interventionRepo.findAll();
    }

    @Override
    public List<InterventionMedecin> findInterventionsByConsultationId(Long consultationId) {
        if (consultationId == null) {
            throw new IllegalArgumentException("L'ID de la consultation est requis");
        }

        return interventionRepo.findByConsultationId(consultationId);
    }

    @Override
    public List<InterventionMedecin> findInterventionsByActeId(Long acteId) {
        if (acteId == null) {
            throw new IllegalArgumentException("L'ID de l'acte est requis");
        }

        return interventionRepo.findByActeId(acteId);
    }

    @Override
    public List<InterventionMedecin> findInterventionsByNumDent(Integer numDent) {
        if (numDent == null) {
            throw new IllegalArgumentException("Le numéro de dent est requis");
        }

        if (numDent < 1 || numDent > 32) {
            throw new IllegalArgumentException("Le numéro de dent doit être entre 1 et 32");
        }

        return interventionRepo.findByNumDent(numDent);
    }

    @Override
    public List<InterventionMedecin> findInterventionsByPrixBetween(Double min, Double max) {
        if (min == null || max == null) {
            throw new IllegalArgumentException("Les prix min et max sont requis");
        }

        if (min > max) {
            throw new IllegalArgumentException("Le prix min doit être inférieur au prix max");
        }

        return interventionRepo.findByPrixPatientBetween(min, max);
    }

    @Override
    public double calculerTotalInterventionsConsultation(Long consultationId) {
        if (consultationId == null) {
            throw new IllegalArgumentException("L'ID de la consultation est requis");
        }

        return interventionRepo.calculerTotalInterventionsConsultation(consultationId);
    }

    @Override
    public long countInterventionsByActe(Long acteId) {
        if (acteId == null) {
            return 0;
        }

        return interventionRepo.countInterventionsByActe(acteId);
    }

    @Override
    public Integer getDentLaPlusTraitee(Long dossierId) {
        // Cette méthode nécessite des jointures complexes
        // Implémentation simplifiée - à adapter

        if (dossierId == null) {
            return null;
        }

        // Récupérer toutes les interventions du dossier
        // Note: Cette logique est simplifiée et nécessite des ajustements

        List<InterventionMedecin> toutesInterventions = findAllInterventions();

        // Filtrer par consultation du dossier (logique simplifiée)
        Map<Integer, Long> compteurDents = new HashMap<>();

        for (InterventionMedecin intervention : toutesInterventions) {
            if (intervention.getNumDent() != null) {
                compteurDents.merge(intervention.getNumDent(), 1L, Long::sum);
            }
        }

        if (compteurDents.isEmpty()) {
            return null;
        }

        // Trouver la dent avec le plus grand nombre d'interventions
        return Collections.max(compteurDents.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    @Override
    public double getCoutMoyenIntervention() {
        List<InterventionMedecin> interventions = findAllInterventions();
        if (interventions.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        int count = 0;

        for (InterventionMedecin intervention : interventions) {
            if (intervention.getPrixPatient() != null) {
                total += intervention.getPrixPatient();
                count++;
            }
        }

        return count > 0 ? total / count : 0.0;
    }

    // Méthode supplémentaire : regrouper les interventions par type d'acte
    public Map<Long, List<InterventionMedecin>> grouperInterventionsParActe() {
        List<InterventionMedecin> interventions = findAllInterventions();

        return interventions.stream()
                .filter(i -> i.getActeId() != null)
                .collect(Collectors.groupingBy(InterventionMedecin::getActeId));
    }

    // Méthode supplémentaire : calculer le revenu par dent
    public Map<Integer, Double> calculerRevenuParDent() {
        List<InterventionMedecin> interventions = findAllInterventions();
        Map<Integer, Double> revenuParDent = new HashMap<>();

        for (InterventionMedecin intervention : interventions) {
            if (intervention.getNumDent() != null && intervention.getPrixPatient() != null) {
                revenuParDent.merge(
                        intervention.getNumDent(),
                        intervention.getPrixPatient(),
                        Double::sum
                );
            }
        }

        return revenuParDent;
    }

    // Méthode supplémentaire : vérifier si une dent a déjà été traitée
    public boolean dentDejaTraitee(Integer numDent, Long dossierId) {
        if (numDent == null || dossierId == null) {
            return false;
        }

        List<InterventionMedecin> interventions = findInterventionsByNumDent(numDent);
        // Logique simplifiée - à adapter pour vérifier par dossier spécifique

        return !interventions.isEmpty();
    }
}