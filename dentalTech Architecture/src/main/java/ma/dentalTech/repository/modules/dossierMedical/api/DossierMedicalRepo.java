package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DossierMedicalRepo extends CrudRepository<DossierMedical, Long> {

    Optional<DossierMedical> findByPatientId(Long patientId);
    List<DossierMedical> findByPatientNomPrenom(String keyword);
    List<DossierMedical> findAllWithConsultations();
    long countDossiers();

    // Méthodes pour les relations
    List<Consultation> getConsultationsOfDossier(Long dossierId);
    List<Ordonnance> getOrdonnancesOfDossier(Long dossierId);
    List<Certificat> getCertificatsOfDossier(Long dossierId);
}