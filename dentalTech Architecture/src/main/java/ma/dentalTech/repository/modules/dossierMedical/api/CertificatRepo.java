package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CertificatRepo extends CrudRepository<Certificat, Long> {

    List<Certificat> findByDossierMedicalId(Long dossierId);
    List<Certificat> findByMedecinId(Long medecinId);
    List<Certificat> findByDateBetween(LocalDate start, LocalDate end);
    List<Certificat> findByDureeGreaterThan(Integer duree);

    // Méthodes utilitaires
    boolean isCertificatValide(Long certificatId);
    long countCertificatsByMedecin(Long medecinId);
}
