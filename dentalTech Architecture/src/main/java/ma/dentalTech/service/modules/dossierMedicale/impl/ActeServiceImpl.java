package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository;
import ma.dentalTech.service.modules.dossierMedicale.api.ActeService;
import java.util.List;
import java.util.Optional;

public class ActeServiceImpl implements ActeService {

    private final ActeRepository acteRepo;

    public ActeServiceImpl() {
        this.acteRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.ActeRepositoryImpl();
    }

    @Override
    public Acte createActe(Acte acte) {
        // Validation
        if (acte.getLibelle() == null || acte.getLibelle().trim().isEmpty()) {
            throw new IllegalArgumentException("Le libellé de l'acte est requis");
        }

        if (acte.getCategorie() == null || acte.getCategorie().trim().isEmpty()) {
            throw new IllegalArgumentException("La catégorie de l'acte est requise");
        }

        if (acte.getPrixBase() == null || acte.getPrixBase() < 0) {
            throw new IllegalArgumentException("Le prix de base doit être positif");
        }

        acteRepo.create(acte);
        return acte;
    }

    @Override
    public Acte updateActe(Acte acte) {
        if (acte.getId() == null) {
            throw new IllegalArgumentException("L'ID de l'acte est requis");
        }

        Acte existing = acteRepo.findById(acte.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Acte non trouvé avec ID: " + acte.getId());
        }

        acteRepo.update(acte);
        return acte;
    }

    @Override
    public void deleteActe(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de l'acte est requis");
        }

        // Vérifier s'il est utilisé dans des interventions
        long count = acteRepo.countActesByCategorie("TODO"); // À adapter
        // Implémentation simplifiée

        acteRepo.deleteById(id);
    }

    @Override
    public Optional<Acte> findActeById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(acteRepo.findById(id));
    }

    @Override
    public List<Acte> findAllActes() {
        return acteRepo.findAll();
    }

    @Override
    public List<Acte> findActesByCategorie(String categorie) {
        if (categorie == null || categorie.trim().isEmpty()) {
            throw new IllegalArgumentException("La catégorie est requise");
        }

        return acteRepo.findByCategorie(categorie);
    }

    @Override
    public List<Acte> searchActesByLibelle(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllActes();
        }

        return acteRepo.findByLibelleContaining(keyword);
    }

    @Override
    public List<Acte> findActesByPrixBetween(Double min, Double max) {
        if (min == null || max == null) {
            throw new IllegalArgumentException("Les prix min et max sont requis");
        }

        if (min > max) {
            throw new IllegalArgumentException("Le prix min doit être inférieur au prix max");
        }

        return acteRepo.findByPrixBetween(min, max);
    }

    @Override
    public long countActesByCategorie(String categorie) {
        if (categorie == null || categorie.trim().isEmpty()) {
            return 0;
        }

        return acteRepo.countActesByCategorie(categorie);
    }

    @Override
    public double getPrixMoyenActes() {
        List<Acte> actes = findAllActes();
        if (actes.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (Acte acte : actes) {
            total += acte.getPrixBase();
        }

        return total / actes.size();
    }

    @Override
    public double getPrixTotalActesByCategorie(String categorie) {
        if (categorie == null || categorie.trim().isEmpty()) {
            return 0.0;
        }

        List<Acte> actes = findActesByCategorie(categorie);
        double total = 0.0;
        for (Acte acte : actes) {
            total += acte.getPrixBase();
        }

        return total;
    }
}