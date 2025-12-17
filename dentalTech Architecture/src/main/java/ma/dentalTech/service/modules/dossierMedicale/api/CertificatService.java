package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Certificat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CertificatService {

    Certificat createCertificat(Certificat certificat);
    Certificat updateCertificat(Certificat certificat);
    void deleteCertificat(Long id);
    Optional<Certificat> findCertificatById(Long id);
    List<Certificat> findAllCertificats();

    List<Certificat> findCertificatsByDossierMedicalId(Long dossierId);
    List<Certificat> findCertificatsByMedecinId(Long medecinId);
    List<Certificat> findCertificatsByDateBetween(LocalDate start, LocalDate end);
    List<Certificat> findCertificatsByDureeGreaterThan(Integer duree);

    // Méthodes métier
    boolean isCertificatValide(Long certificatId);
    boolean verifierCertificatEnCours(Long dossierId);
    long countCertificatsByMedecin(Long medecinId);
    LocalDate getProchaineDateExpirationCertificat(Long dossierId);
}