package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.enums.FormeMedicament;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MedicamentRepo extends CrudRepository<Medicament, Long> {

    List<Medicament> findByNomContaining(String keyword);
    List<Medicament> findByLaboratoire(String laboratoire);
    List<Medicament> findByForme(FormeMedicament forme);
    List<Medicament> findByRemboursable(boolean remboursable);
    List<Medicament> findByType(String type);
    List<Medicament> findByPrixUnitaireBetween(Double min, Double max);
}
