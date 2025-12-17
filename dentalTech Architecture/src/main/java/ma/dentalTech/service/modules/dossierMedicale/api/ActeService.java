package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Acte;
import java.util.List;
import java.util.Optional;

public interface ActeService {

    Acte createActe(Acte acte);
    Acte updateActe(Acte acte);
    void deleteActe(Long id);
    Optional<Acte> findActeById(Long id);
    List<Acte> findAllActes();

    List<Acte> findActesByCategorie(String categorie);
    List<Acte> searchActesByLibelle(String keyword);
    List<Acte> findActesByPrixBetween(Double min, Double max);

    // Statistiques
    long countActesByCategorie(String categorie);
    double getPrixMoyenActes();
    double getPrixTotalActesByCategorie(String categorie);
}