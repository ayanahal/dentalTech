package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.enums.FormeMedicament;
import ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.MedicamentService;
import java.util.*;
import java.util.stream.Collectors;

public class MedicamentServiceImpl implements MedicamentService {

    private final MedicamentRepo medicamentRepo;

    public MedicamentServiceImpl() {
        this.medicamentRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.MedicamentRepositoryImpl();
    }

    @Override
    public Medicament createMedicament(Medicament medicament) {
        // Validation
        if (medicament.getNom() == null || medicament.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du médicament est requis");
        }

        if (medicament.getForme() == null) {
            throw new IllegalArgumentException("La forme du médicament est requise");
        }

        if (medicament.getPrixUnitaire() == null || medicament.getPrixUnitaire() < 0) {
            throw new IllegalArgumentException("Le prix unitaire doit être positif");
        }

        // Vérifier si le médicament existe déjà (par nom)
        List<Medicament> existing = medicamentRepo.findByNomContaining(medicament.getNom());
        if (!existing.isEmpty()) {
            // On pourrait faire une vérification plus stricte ici
            // throw new IllegalStateException("Un médicament avec ce nom existe déjà");
        }

        medicamentRepo.create(medicament);
        return medicament;
    }

    @Override
    public Medicament updateMedicament(Medicament medicament) {
        if (medicament.getId() == null) {
            throw new IllegalArgumentException("L'ID du médicament est requis");
        }

        Medicament existing = medicamentRepo.findById(medicament.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Médicament non trouvé avec ID: " + medicament.getId());
        }

        medicamentRepo.update(medicament);
        return medicament;
    }

    @Override
    public void deleteMedicament(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du médicament est requis");
        }

        // Vérifier si le médicament est utilisé dans des prescriptions
        // Cette vérification dépend de votre logique métier

        medicamentRepo.deleteById(id);
    }

    @Override
    public Optional<Medicament> findMedicamentById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(medicamentRepo.findById(id));
    }

    @Override
    public List<Medicament> findAllMedicaments() {
        return medicamentRepo.findAll();
    }

    @Override
    public List<Medicament> searchMedicamentsByNom(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllMedicaments();
        }

        return medicamentRepo.findByNomContaining(keyword);
    }

    @Override
    public List<Medicament> findMedicamentsByLaboratoire(String laboratoire) {
        if (laboratoire == null || laboratoire.trim().isEmpty()) {
            throw new IllegalArgumentException("Le laboratoire est requis");
        }

        return medicamentRepo.findByLaboratoire(laboratoire);
    }

    @Override
    public List<Medicament> findMedicamentsByForme(FormeMedicament forme) {
        if (forme == null) {
            throw new IllegalArgumentException("La forme est requise");
        }

        return medicamentRepo.findByForme(forme);
    }

    @Override
    public List<Medicament> findMedicamentsRemboursables() {
        return medicamentRepo.findByRemboursable(true);
    }

    @Override
    public List<Medicament> findMedicamentsByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Le type est requis");
        }

        return medicamentRepo.findByType(type);
    }

    @Override
    public List<Medicament> findMedicamentsByPrixBetween(Double min, Double max) {
        if (min == null || max == null) {
            throw new IllegalArgumentException("Les prix min et max sont requis");
        }

        if (min > max) {
            throw new IllegalArgumentException("Le prix min doit être inférieur au prix max");
        }

        return medicamentRepo.findByPrixUnitaireBetween(min, max);
    }

    @Override
    public boolean isMedicamentRemboursable(Long medicamentId) {
        if (medicamentId == null) {
            return false;
        }

        Medicament medicament = medicamentRepo.findById(medicamentId);
        return medicament != null && medicament.isRemboursable();
    }

    @Override
    public double getPrixMoyenMedicaments() {
        List<Medicament> medicaments = findAllMedicaments();
        if (medicaments.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (Medicament medicament : medicaments) {
            if (medicament.getPrixUnitaire() != null) {
                total += medicament.getPrixUnitaire();
            }
        }

        return total / medicaments.size();
    }

    @Override
    public long countMedicamentsByForme(FormeMedicament forme) {
        if (forme == null) {
            return 0;
        }

        List<Medicament> medicaments = findMedicamentsByForme(forme);
        return medicaments.size();
    }

    @Override
    public List<Medicament> getMedicamentsLesPlusPrescrits(int limit) {
        // Cette méthode nécessite une jointure avec la table prescription
        // Pour l'instant, retournons simplement les médicaments les plus chers comme exemple
        List<Medicament> medicaments = findAllMedicaments();

        // Trier par prix décroissant
        medicaments.sort((m1, m2) -> {
            Double prix1 = m1.getPrixUnitaire() != null ? m1.getPrixUnitaire() : 0.0;
            Double prix2 = m2.getPrixUnitaire() != null ? m2.getPrixUnitaire() : 0.0;
            return prix2.compareTo(prix1);
        });

        // Limiter le nombre de résultats
        if (limit > 0 && limit < medicaments.size()) {
            return medicaments.subList(0, limit);
        }

        return medicaments;
    }

    // Méthode supplémentaire : rechercher des médicaments génériques
    public List<Medicament> findMedicamentsGeneriques() {
        List<Medicament> allMedicaments = findAllMedicaments();
        return allMedicaments.stream()
                .filter(m -> m.getNom() != null && m.getNom().toLowerCase().contains("générique"))
                .collect(Collectors.toList());
    }

    // Méthode supplémentaire : mettre à jour le prix d'un médicament
    public void updatePrixMedicament(Long medicamentId, Double nouveauPrix) {
        if (medicamentId == null || nouveauPrix == null) {
            throw new IllegalArgumentException("L'ID et le nouveau prix sont requis");
        }

        if (nouveauPrix < 0) {
            throw new IllegalArgumentException("Le prix doit être positif");
        }

        Medicament medicament = medicamentRepo.findById(medicamentId);
        if (medicament == null) {
            throw new IllegalArgumentException("Médicament non trouvé avec ID: " + medicamentId);
        }

        medicament.setPrixUnitaire(nouveauPrix);
        medicamentRepo.update(medicament);
    }
}