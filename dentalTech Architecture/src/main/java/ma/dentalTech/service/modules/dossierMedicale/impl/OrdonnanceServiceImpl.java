package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.OrdonnanceService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class OrdonnanceServiceImpl implements OrdonnanceService {

    private final OrdonnanceRepo ordonnanceRepo;
    private final PrescriptionRepo prescriptionRepo;

    public OrdonnanceServiceImpl() {
        this.ordonnanceRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.OrdonnanceRepositoryImpl();
        this.prescriptionRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.PrescriptionRepositoryImpl();
    }

    @Override
    public Ordonnance createOrdonnance(Ordonnance ordonnance) {
        // Validation
        if (ordonnance.getDossierMedical() == null || ordonnance.getDossierMedical().getId() == null) {
            throw new IllegalArgumentException("L'ordonnance doit être associée à un dossier médical");
        }

        if (ordonnance.getMedecin() == null || ordonnance.getMedecin().getId() == null) {
            throw new IllegalArgumentException("L'ordonnance doit être associée à un médecin");
        }

        if (ordonnance.getDate() == null) {
            ordonnance.setDate(LocalDate.now());
        }

        ordonnanceRepo.create(ordonnance);
        return ordonnance;
    }

    @Override
    public Ordonnance updateOrdonnance(Ordonnance ordonnance) {
        if (ordonnance.getId() == null) {
            throw new IllegalArgumentException("L'ID de l'ordonnance est requis");
        }

        Ordonnance existing = ordonnanceRepo.findById(ordonnance.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Ordonnance non trouvée avec ID: " + ordonnance.getId());
        }

        ordonnanceRepo.update(ordonnance);
        return ordonnance;
    }

    @Override
    public void deleteOrdonnance(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de l'ordonnance est requis");
        }

        // Supprimer d'abord les prescriptions associées
        List<Prescription> prescriptions = prescriptionRepo.findByOrdonnanceId(id);
        for (Prescription prescription : prescriptions) {
            prescriptionRepo.deleteById(prescription.getId());
        }

        ordonnanceRepo.deleteById(id);
    }

    @Override
    public Optional<Ordonnance> findOrdonnanceById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        Ordonnance ordonnance = ordonnanceRepo.findById(id);
        if (ordonnance != null) {
            // Charger les prescriptions
            ordonnance.setPrescriptions(prescriptionRepo.findByOrdonnanceId(id));
        }

        return Optional.ofNullable(ordonnance);
    }

    @Override
    public List<Ordonnance> findAllOrdonnances() {
        List<Ordonnance> ordonnances = ordonnanceRepo.findAll();
        // Charger les prescriptions pour chaque ordonnance
        for (Ordonnance ordonnance : ordonnances) {
            ordonnance.setPrescriptions(prescriptionRepo.findByOrdonnanceId(ordonnance.getId()));
        }
        return ordonnances;
    }

    @Override
    public List<Ordonnance> findOrdonnancesByDossierMedicalId(Long dossierId) {
        if (dossierId == null) {
            throw new IllegalArgumentException("L'ID du dossier est requis");
        }

        List<Ordonnance> ordonnances = ordonnanceRepo.findByDossierMedicalId(dossierId);
        // Charger les prescriptions
        for (Ordonnance ordonnance : ordonnances) {
            ordonnance.setPrescriptions(prescriptionRepo.findByOrdonnanceId(ordonnance.getId()));
        }
        return ordonnances;
    }

    @Override
    public List<Ordonnance> findOrdonnancesByMedecinId(Long medecinId) {
        if (medecinId == null) {
            throw new IllegalArgumentException("L'ID du médecin est requis");
        }

        return ordonnanceRepo.findByMedecinId(medecinId);
    }

    @Override
    public List<Ordonnance> findOrdonnancesByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("La date est requise");
        }

        return ordonnanceRepo.findByDate(date);
    }

    @Override
    public List<Ordonnance> findOrdonnancesBetweenDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Les dates de début et fin sont requises");
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        return ordonnanceRepo.findBetweenDates(start, end);
    }

    @Override
    public Prescription addPrescriptionToOrdonnance(Long ordonnanceId, Prescription prescription) {
        if (ordonnanceId == null) {
            throw new IllegalArgumentException("L'ID de l'ordonnance est requis");
        }

        // Vérifier que l'ordonnance existe
        Ordonnance ordonnance = ordonnanceRepo.findById(ordonnanceId);
        if (ordonnance == null) {
            throw new IllegalArgumentException("Ordonnance non trouvée avec ID: " + ordonnanceId);
        }

        // Associer la prescription à l'ordonnance
        prescription.setOrdonnance(ordonnance);
        prescriptionRepo.create(prescription);

        return prescription;
    }

    @Override
    public void removePrescriptionFromOrdonnance(Long ordonnanceId, Long prescriptionId) {
        if (ordonnanceId == null || prescriptionId == null) {
            throw new IllegalArgumentException("Les IDs d'ordonnance et prescription sont requis");
        }

        prescriptionRepo.removePrescriptionFromOrdonnance(ordonnanceId, prescriptionId);
    }

    @Override
    public List<Prescription> getPrescriptionsOfOrdonnance(Long ordonnanceId) {
        if (ordonnanceId == null) {
            throw new IllegalArgumentException("L'ID de l'ordonnance est requis");
        }

        return prescriptionRepo.findByOrdonnanceId(ordonnanceId);
    }

    @Override
    public double calculerCoutTotalOrdonnance(Long ordonnanceId) {
        if (ordonnanceId == null) {
            throw new IllegalArgumentException("L'ID de l'ordonnance est requis");
        }

        List<Prescription> prescriptions = prescriptionRepo.findByOrdonnanceId(ordonnanceId);
        double total = 0.0;

        for (Prescription prescription : prescriptions) {
            total += prescriptionRepo.calculerCoutTotalPrescription(prescription.getId());
        }

        return total;
    }

    @Override
    public boolean verifierOrdonnanceComplete(Long ordonnanceId) {
        if (ordonnanceId == null) {
            return false;
        }

        List<Prescription> prescriptions = prescriptionRepo.findByOrdonnanceId(ordonnanceId);
        return !prescriptions.isEmpty();
    }

    @Override
    public long countOrdonnancesByMedecin(Long medecinId) {
        if (medecinId == null) {
            return 0;
        }

        List<Ordonnance> ordonnances = ordonnanceRepo.findByMedecinId(medecinId);
        return ordonnances.size();
    }
}