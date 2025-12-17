package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.PrescriptionService;
import java.util.List;
import java.util.Optional;

public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepo prescriptionRepo;

    public PrescriptionServiceImpl() {
        this.prescriptionRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.PrescriptionRepositoryImpl();
    }

    @Override
    public Prescription createPrescription(Prescription prescription) {
        // Validation
        if (prescription.getOrdonnance() == null || prescription.getOrdonnance().getId() == null) {
            throw new IllegalArgumentException("La prescription doit être associée à une ordonnance");
        }

        if (prescription.getMedicament() == null || prescription.getMedicament().getId() == null) {
            throw new IllegalArgumentException("La prescription doit être associée à un médicament");
        }

        if (prescription.getQuantite() == null || prescription.getQuantite() <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }

        if (prescription.getDureeEnJours() == null || prescription.getDureeEnJours() <= 0) {
            throw new IllegalArgumentException("La durée en jours doit être positive");
        }

        if (prescription.getFrequence() == null || prescription.getFrequence().trim().isEmpty()) {
            throw new IllegalArgumentException("La fréquence est requise");
        }

        prescriptionRepo.create(prescription);
        return prescription;
    }

    @Override
    public Prescription updatePrescription(Prescription prescription) {
        if (prescription.getId() == null) {
            throw new IllegalArgumentException("L'ID de la prescription est requis");
        }

        Prescription existing = prescriptionRepo.findById(prescription.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Prescription non trouvée avec ID: " + prescription.getId());
        }

        prescriptionRepo.update(prescription);
        return prescription;
    }

    @Override
    public void deletePrescription(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de la prescription est requis");
        }

        prescriptionRepo.deleteById(id);
    }

    @Override
    public Optional<Prescription> findPrescriptionById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(prescriptionRepo.findById(id));
    }

    @Override
    public List<Prescription> findAllPrescriptions() {
        return prescriptionRepo.findAll();
    }

    @Override
    public List<Prescription> findPrescriptionsByOrdonnanceId(Long ordonnanceId) {
        if (ordonnanceId == null) {
            throw new IllegalArgumentException("L'ID de l'ordonnance est requis");
        }

        return prescriptionRepo.findByOrdonnanceId(ordonnanceId);
    }

    @Override
    public List<Prescription> findPrescriptionsByMedicamentId(Long medicamentId) {
        if (medicamentId == null) {
            throw new IllegalArgumentException("L'ID du médicament est requis");
        }

        return prescriptionRepo.findByMedicamentId(medicamentId);
    }

    @Override
    public List<Prescription> findPrescriptionsByQuantiteGreaterThan(Integer quantite) {
        if (quantite == null) {
            throw new IllegalArgumentException("La quantité est requise");
        }

        return prescriptionRepo.findByQuantiteGreaterThan(quantite);
    }

    @Override
    public void updateQuantitePrescription(Long prescriptionId, Integer nouvelleQuantite) {
        if (prescriptionId == null || nouvelleQuantite == null) {
            throw new IllegalArgumentException("L'ID de la prescription et la nouvelle quantité sont requis");
        }

        if (nouvelleQuantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }

        prescriptionRepo.updateQuantite(prescriptionId, nouvelleQuantite);
    }

    @Override
    public double calculerCoutPrescription(Long prescriptionId) {
        if (prescriptionId == null) {
            throw new IllegalArgumentException("L'ID de la prescription est requis");
        }

        return prescriptionRepo.calculerCoutTotalPrescription(prescriptionId);
    }

    @Override
    public double calculerCoutTotalPrescriptionsOrdonnance(Long ordonnanceId) {
        if (ordonnanceId == null) {
            throw new IllegalArgumentException("L'ID de l'ordonnance est requis");
        }

        List<Prescription> prescriptions = prescriptionRepo.findByOrdonnanceId(ordonnanceId);
        double total = 0.0;

        for (Prescription prescription : prescriptions) {
            total += prescriptionRepo.calculerCoutTotalPrescription(prescription.getId());
        }

        return total;
    }

    @Override
    public boolean verifierStockSuffisant(Long medicamentId, Integer quantiteDemandee) {
        // Cette méthode nécessite une table de stock/gestion d'inventaire
        // Pour l'instant, retournons toujours true comme implémentation de base
        // À adapter selon votre logique métier

        if (medicamentId == null || quantiteDemandee == null) {
            return false;
        }

        // Implémentation de base - toujours suffisant
        // Dans une application réelle, vous vérifieriez une table de stock
        return true;
    }

    // Méthode supplémentaire : calculer la durée totale d'une prescription
    public int calculerDureeTotalePrescription(Long prescriptionId) {
        if (prescriptionId == null) {
            return 0;
        }

        Prescription prescription = prescriptionRepo.findById(prescriptionId);
        if (prescription == null) {
            return 0;
        }

        // Exemple : si fréquence est "2 fois par jour" et durée est 7 jours
        // Ceci est simplifié - à adapter selon votre format de fréquence
        Integer duree = prescription.getDureeEnJours();
        return duree != null ? duree : 0;
    }

    // Méthode supplémentaire : vérifier les interactions médicamenteuses
    public boolean verifierInteractionsMedicamenteuses(List<Long> medicamentIds) {
        // Cette méthode nécessite une base de données d'interactions médicamenteuses
        // Pour l'instant, retournons false (pas d'interactions)
        // À implémenter selon vos besoins

        if (medicamentIds == null || medicamentIds.isEmpty()) {
            return false;
        }

        // Logique simplifiée : si plus de 5 médicaments, risque d'interaction
        return medicamentIds.size() > 5;
    }
}