package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrdonnanceRepo extends CrudRepository<Ordonnance, Long> {

    List<Ordonnance> findByDossierMedicalId(Long dossierId);
    List<Ordonnance> findByMedecinId(Long medecinId);
    List<Ordonnance> findByDate(LocalDate date);
    List<Ordonnance> findBetweenDates(LocalDate start, LocalDate end);

    // Méthodes pour les relations
    List<Prescription> getPrescriptionsOfOrdonnance(Long ordonnanceId);
    void addPrescriptionToOrdonnance(Long ordonnanceId, Long prescriptionId);
    void removePrescriptionFromOrdonnance(Long ordonnanceId, Long prescriptionId);
}
