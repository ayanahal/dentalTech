package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo;
import ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.ConsultationService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepo consultationRepo;
    private final InterventionMedecinRepo interventionRepo;

    public ConsultationServiceImpl() {
        this.consultationRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl();
        this.interventionRepo = new ma.dentalTech.repository.modules.dossierMedical.impl.InterventionMedecinRepositoryImpl();
    }

    @Override
    public Consultation createConsultation(Consultation consultation) {
        // Validation
        if (consultation.getDossierMedical() == null || consultation.getDossierMedical().getId() == null) {
            throw new IllegalArgumentException("La consultation doit être associée à un dossier médical");
        }

        if (consultation.getMedecin() == null || consultation.getMedecin().getId() == null) {
            throw new IllegalArgumentException("La consultation doit être associée à un médecin");
        }

        if (consultation.getDateConsultation() == null) {
            consultation.setDateConsultation(LocalDate.now());
        }

        if (consultation.getStatut() == null) {
            consultation.setStatut(StatutConsultation.PLANIFIEE);
        }

        consultationRepo.create(consultation);
        return consultation;
    }

    @Override
    public Consultation updateConsultation(Consultation consultation) {
        if (consultation.getId() == null) {
            throw new IllegalArgumentException("L'ID de la consultation est requis");
        }

        Consultation existing = consultationRepo.findById(consultation.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Consultation non trouvée avec ID: " + consultation.getId());
        }

        consultationRepo.update(consultation);
        return consultation;
    }

    @Override
    public void deleteConsultation(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de la consultation est requis");
        }

        // Vérifier s'il existe des interventions associées
        List<InterventionMedecin> interventions = interventionRepo.findByConsultationId(id);
        if (!interventions.isEmpty()) {
            // On peut choisir de supprimer aussi les interventions
            for (InterventionMedecin intervention : interventions) {
                interventionRepo.deleteById(intervention.getId());
            }
        }

        consultationRepo.deleteById(id);
    }

    @Override
    public Optional<Consultation> findConsultationById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        Consultation consultation = consultationRepo.findById(id);
        if (consultation != null) {
            // Charger les interventions
            consultation.setInterventions(interventionRepo.findByConsultationId(id));
        }

        return Optional.ofNullable(consultation);
    }

    @Override
    public List<Consultation> findAllConsultations() {
        List<Consultation> consultations = consultationRepo.findAll();
        // Optionnel: charger les interventions pour chaque consultation
        for (Consultation consultation : consultations) {
            consultation.setInterventions(interventionRepo.findByConsultationId(consultation.getId()));
        }
        return consultations;
    }

    @Override
    public List<Consultation> findConsultationsByDossierMedicalId(Long dossierId) {
        if (dossierId == null) {
            throw new IllegalArgumentException("L'ID du dossier est requis");
        }

        return consultationRepo.findByDossierMedicalId(dossierId);
    }

    @Override
    public List<Consultation> findConsultationsByMedecinId(Long medecinId) {
        if (medecinId == null) {
            throw new IllegalArgumentException("L'ID du médecin est requis");
        }

        return consultationRepo.findByMedecinId(medecinId);
    }

    @Override
    public List<Consultation> findConsultationsByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("La date est requise");
        }

        return consultationRepo.findByDate(date);
    }

    @Override
    public List<Consultation> findConsultationsByStatut(StatutConsultation statut) {
        if (statut == null) {
            throw new IllegalArgumentException("Le statut est requis");
        }

        return consultationRepo.findByStatut(statut);
    }

    @Override
    public List<Consultation> findConsultationsBetweenDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Les dates de début et fin sont requises");
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        return consultationRepo.findBetweenDates(start, end);
    }

    @Override
    public InterventionMedecin addInterventionToConsultation(Long consultationId, InterventionMedecin intervention) {
        if (consultationId == null) {
            throw new IllegalArgumentException("L'ID de la consultation est requis");
        }

        // Vérifier que la consultation existe
        Consultation consultation = consultationRepo.findById(consultationId);
        if (consultation == null) {
            throw new IllegalArgumentException("Consultation non trouvée avec ID: " + consultationId);
        }

        // Créer l'intervention
        intervention.setConsultationId(consultationId);
        interventionRepo.create(intervention);

        // Mettre à jour le statut de la consultation si nécessaire
        if (consultation.getStatut() == StatutConsultation.PLANIFIEE) {
            consultation.setStatut(StatutConsultation.EN_COURS);
            consultationRepo.update(consultation);
        }

        return intervention;
    }

    @Override
    public void removeInterventionFromConsultation(Long consultationId, Long interventionId) {
        if (consultationId == null || interventionId == null) {
            throw new IllegalArgumentException("Les IDs de consultation et intervention sont requis");
        }

        interventionRepo.removeInterventionFromConsultation(consultationId, interventionId);
    }

    @Override
    public List<InterventionMedecin> getInterventionsOfConsultation(Long consultationId) {
        if (consultationId == null) {
            throw new IllegalArgumentException("L'ID de la consultation est requis");
        }

        return interventionRepo.findByConsultationId(consultationId);
    }

    @Override
    public double calculerCoutConsultation(Long consultationId) {
        if (consultationId == null) {
            throw new IllegalArgumentException("L'ID de la consultation est requis");
        }

        return interventionRepo.calculerTotalInterventionsConsultation(consultationId);
    }

    @Override
    public void changerStatutConsultation(Long consultationId, StatutConsultation nouveauStatut) {
        if (consultationId == null) {
            throw new IllegalArgumentException("L'ID de la consultation est requis");
        }

        if (nouveauStatut == null) {
            throw new IllegalArgumentException("Le nouveau statut est requis");
        }

        Consultation consultation = consultationRepo.findById(consultationId);
        if (consultation == null) {
            throw new IllegalArgumentException("Consultation non trouvée avec ID: " + consultationId);
        }

        consultation.setStatut(nouveauStatut);
        consultationRepo.update(consultation);
    }

    @Override
    public boolean isConsultationComplete(Long consultationId) {
        if (consultationId == null) {
            return false;
        }

        Consultation consultation = consultationRepo.findById(consultationId);
        if (consultation == null) {
            return false;
        }

        return consultation.getStatut() == StatutConsultation.TERMINEE;
    }

    @Override
    public long countConsultationsByMedecin(Long medecinId) {
        if (medecinId == null) {
            return 0;
        }

        List<Consultation> consultations = consultationRepo.findByMedecinId(medecinId);
        return consultations.size();
    }
}