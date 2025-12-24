package ma.dentalTech.service.modules.patient.api;

import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.patient.Patient;

import java.util.List;
import java.util.Optional;

public interface AntecedentService {

    // CRUD Antecedent
    List<Antecedent> getAllAntecedents();
    Optional<Optional<Statistiques>> getAntecedentById(Long id);
    void createAntecedent(Antecedent antecedent);
    void updateAntecedent(Antecedent antecedent);
    void deleteAntecedent(Antecedent antecedent);

    // Recherches
    Optional<Antecedent> getAntecedentByNom(String nom);
    List<Antecedent> getAntecedentsByCategorie(CategorieAntecedent categorie);
    List<Antecedent> getAntecedentsByNiveauRisque(NiveauRisque niveau);

    // Pagination
    List<Antecedent> getAntecedentsPage(int limit, int offset);
    long countAntecedents();

    // Navigation inverse
    List<Patient> getPatientsHavingAntecedent(Long antecedentId);
}

