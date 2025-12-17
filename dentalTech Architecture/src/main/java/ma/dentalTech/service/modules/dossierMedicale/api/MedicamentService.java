package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.enums.FormeMedicament;
import java.util.List;
import java.util.Optional;

public interface MedicamentService {

    Medicament createMedicament(Medicament medicament);
    Medicament updateMedicament(Medicament medicament);
    void deleteMedicament(Long id);
    Optional<Medicament> findMedicamentById(Long id);
    List<Medicament> findAllMedicaments();

    List<Medicament> searchMedicamentsByNom(String keyword);
    List<Medicament> findMedicamentsByLaboratoire(String laboratoire);
    List<Medicament> findMedicamentsByForme(FormeMedicament forme);
    List<Medicament> findMedicamentsRemboursables();
    List<Medicament> findMedicamentsByType(String type);
    List<Medicament> findMedicamentsByPrixBetween(Double min, Double max);

    // Méthodes métier
    boolean isMedicamentRemboursable(Long medicamentId);
    double getPrixMoyenMedicaments();
    long countMedicamentsByForme(FormeMedicament forme);
    List<Medicament> getMedicamentsLesPlusPrescrits(int limit);
}