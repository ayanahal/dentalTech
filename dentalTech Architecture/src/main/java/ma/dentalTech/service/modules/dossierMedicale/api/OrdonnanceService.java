package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrdonnanceService {

    Ordonnance createOrdonnance(Ordonnance ordonnance);
    Ordonnance updateOrdonnance(Ordonnance ordonnance);
    void deleteOrdonnance(Long id);
    Optional<Ordonnance> findOrdonnanceById(Long id);
    List<Ordonnance> findAllOrdonnances();

    List<Ordonnance> findOrdonnancesByDossierMedicalId(Long dossierId);
    List<Ordonnance> findOrdonnancesByMedecinId(Long medecinId);
    List<Ordonnance> findOrdonnancesByDate(LocalDate date);
    List<Ordonnance> findOrdonnancesBetweenDates(LocalDate start, LocalDate end);

    // Gestion des prescriptions
    Prescription addPrescriptionToOrdonnance(Long ordonnanceId, Prescription prescription);
    void removePrescriptionFromOrdonnance(Long ordonnanceId, Long prescriptionId);
    List<Prescription> getPrescriptionsOfOrdonnance(Long ordonnanceId);

    // Méthodes métier
    double calculerCoutTotalOrdonnance(Long ordonnanceId);
    boolean verifierOrdonnanceComplete(Long ordonnanceId);
    long countOrdonnancesByMedecin(Long medecinId);
}