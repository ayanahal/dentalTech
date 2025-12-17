package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ActeRepository extends CrudRepository<Acte, Long> {

    List<Acte> findByCategorie(String categorie);
    List<Acte> findByLibelleContaining(String keyword);
    List<Acte> findByPrixBetween(Double min, Double max);

    // Méthodes pour les relations
    List<InterventionMedecin> getInterventionsOfActe(Long acteId);
    long countActesByCategorie(String categorie);
}
