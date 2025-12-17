package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.CertificatService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CertificatServiceImpl implements CertificatService {

    private final CertificatRepo certificatRepo;

    public CertificatServiceImpl() {
        this.certificatRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.CertificatRepositoryImpl();
    }

    @Override
    public Certificat createCertificat(Certificat certificat) {
        // Validation
        if (certificat.getDossierMedical() == null || certificat.getDossierMedical().getId() == null) {
            throw new IllegalArgumentException("Le certificat doit être associé à un dossier médical");
        }

        if (certificat.getMedecin() == null || certificat.getMedecin().getId() == null) {
            throw new IllegalArgumentException("Le certificat doit être associé à un médecin");
        }

        if (certificat.getDateDebut() == null) {
            throw new IllegalArgumentException("La date de début est requise");
        }

        if (certificat.getDateFin() == null) {
            throw new IllegalArgumentException("La date de fin est requise");
        }

        if (certificat.getDateDebut().isAfter(certificat.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        // Calculer la durée automatiquement si non fournie
        if (certificat.getDuree() == null) {
            long jours = java.time.temporal.ChronoUnit.DAYS.between(
                    certificat.getDateDebut(),
                    certificat.getDateFin()
            );
            certificat.setDuree((int) jours + 1); // +1 pour inclure le jour de début
        }

        certificatRepo.create(certificat);
        return certificat;
    }

    @Override
    public Certificat updateCertificat(Certificat certificat) {
        if (certificat.getId() == null) {
            throw new IllegalArgumentException("L'ID du certificat est requis");
        }

        Certificat existing = certificatRepo.findById(certificat.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Certificat non trouvé avec ID: " + certificat.getId());
        }

        certificatRepo.update(certificat);
        return certificat;
    }

    @Override
    public void deleteCertificat(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du certificat est requis");
        }

        certificatRepo.deleteById(id);
    }

    @Override
    public Optional<Certificat> findCertificatById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(certificatRepo.findById(id));
    }

    @Override
    public List<Certificat> findAllCertificats() {
        return certificatRepo.findAll();
    }

    @Override
    public List<Certificat> findCertificatsByDossierMedicalId(Long dossierId) {
        if (dossierId == null) {
            throw new IllegalArgumentException("L'ID du dossier est requis");
        }

        return certificatRepo.findByDossierMedicalId(dossierId);
    }

    @Override
    public List<Certificat> findCertificatsByMedecinId(Long medecinId) {
        if (medecinId == null) {
            throw new IllegalArgumentException("L'ID du médecin est requis");
        }

        return certificatRepo.findByMedecinId(medecinId);
    }

    @Override
    public List<Certificat> findCertificatsByDateBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Les dates de début et fin sont requises");
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        return certificatRepo.findByDateBetween(start, end);
    }

    @Override
    public List<Certificat> findCertificatsByDureeGreaterThan(Integer duree) {
        if (duree == null) {
            throw new IllegalArgumentException("La durée est requise");
        }

        return certificatRepo.findByDureeGreaterThan(duree);
    }

    @Override
    public boolean isCertificatValide(Long certificatId) {
        if (certificatId == null) {
            return false;
        }

        return certificatRepo.isCertificatValide(certificatId);
    }

    @Override
    public boolean verifierCertificatEnCours(Long dossierId) {
        if (dossierId == null) {
            return false;
        }

        List<Certificat> certificats = findCertificatsByDossierMedicalId(dossierId);
        LocalDate aujourdhui = LocalDate.now();

        for (Certificat certificat : certificats) {
            if (certificat.getDateDebut() != null &&
                    certificat.getDateFin() != null &&
                    !aujourdhui.isBefore(certificat.getDateDebut()) &&
                    !aujourdhui.isAfter(certificat.getDateFin())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public long countCertificatsByMedecin(Long medecinId) {
        if (medecinId == null) {
            return 0;
        }

        return certificatRepo.countCertificatsByMedecin(medecinId);
    }

    @Override
    public LocalDate getProchaineDateExpirationCertificat(Long dossierId) {
        if (dossierId == null) {
            return null;
        }

        List<Certificat> certificats = findCertificatsByDossierMedicalId(dossierId);
        LocalDate prochaineExpiration = null;
        LocalDate aujourdhui = LocalDate.now();

        for (Certificat certificat : certificats) {
            if (certificat.getDateFin() != null &&
                    certificat.getDateFin().isAfter(aujourdhui)) {

                if (prochaineExpiration == null ||
                        certificat.getDateFin().isBefore(prochaineExpiration)) {
                    prochaineExpiration = certificat.getDateFin();
                }
            }
        }

        return prochaineExpiration;
    }

    // Méthode supplémentaire : générer un certificat médical standard
    public Certificat genererCertificatMedicalStandard(Long dossierId, Long medecinId, Integer dureeJours) {
        if (dossierId == null || medecinId == null || dureeJours == null) {
            throw new IllegalArgumentException("Tous les paramètres sont requis");
        }

        if (dureeJours <= 0) {
            throw new IllegalArgumentException("La durée doit être positive");
        }

        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = dateDebut.plusDays(dureeJours - 1);

        Certificat certificat = Certificat.builder()
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .duree(dureeJours)
                .noteMedecin("Certificat médical standard - repos recommandé")
                .build();

        // Note: besoin de setter dossierMedical et medecin avec des objets complets
        // Cette méthode est un template - à adapter selon vos entités

        return certificat;
    }

    // Méthode supplémentaire : vérifier les chevauchements de certificats
    public boolean verifierChevauchementCertificats(Long dossierId, LocalDate nouvelleDateDebut, LocalDate nouvelleDateFin) {
        if (dossierId == null || nouvelleDateDebut == null || nouvelleDateFin == null) {
            return false;
        }

        List<Certificat> certificats = findCertificatsByDossierMedicalId(dossierId);

        for (Certificat certificat : certificats) {
            if (certificat.getDateDebut() != null && certificat.getDateFin() != null) {
                // Vérifier si les périodes se chevauchent
                if (!(nouvelleDateFin.isBefore(certificat.getDateDebut()) ||
                        nouvelleDateDebut.isAfter(certificat.getDateFin()))) {
                    return true; // Chevauchement détecté
                }
            }
        }

        return false; // Pas de chevauchement
    }
}