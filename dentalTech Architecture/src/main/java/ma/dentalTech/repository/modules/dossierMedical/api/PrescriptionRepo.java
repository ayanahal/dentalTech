package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepo extends CrudRepository<Prescription, Long> {

    List<Prescription> findByOrdonnanceId(Long ordonnanceId);
    List<Prescription> findByMedicamentId(Long medicamentId);
    List<Prescription> findByQuantiteGreaterThan(Integer quantite);

    // Méthodes utilitaires
    void updateQuantite(Long prescriptionId, Integer nouvelleQuantite);
    double calculerCoutTotalPrescription(Long prescriptionId);
}
