package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Prescription;
import java.util.List;
import java.util.Optional;

public interface PrescriptionService {

    Prescription createPrescription(Prescription prescription);
    Prescription updatePrescription(Prescription prescription);
    void deletePrescription(Long id);
    Optional<Prescription> findPrescriptionById(Long id);
    List<Prescription> findAllPrescriptions();

    List<Prescription> findPrescriptionsByOrdonnanceId(Long ordonnanceId);
    List<Prescription> findPrescriptionsByMedicamentId(Long medicamentId);
    List<Prescription> findPrescriptionsByQuantiteGreaterThan(Integer quantite);

    // Méthodes métier
    void updateQuantitePrescription(Long prescriptionId, Integer nouvelleQuantite);
    double calculerCoutPrescription(Long prescriptionId);
    double calculerCoutTotalPrescriptionsOrdonnance(Long ordonnanceId);
    boolean verifierStockSuffisant(Long medicamentId, Integer quantiteDemandee);
}