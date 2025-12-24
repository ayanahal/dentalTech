package ma.dentalTech.service.modules.agendas.impl;

import ma.dentalTech.service.modules.agendas.api.RDVService;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.StatutRDV;
import ma.dentalTech.repository.modules.agenda.api.RDVRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RDVServiceImpl implements RDVService {

    private final RDVRepository rdvRepository;

    public RDVServiceImpl(RDVRepository rdvRepository) {
        this.rdvRepository = rdvRepository;
    }

    @Override
    public RDV createRDV(RDV rdv) {
        // Validation
        if (rdv == null) {
            throw new IllegalArgumentException("Le RDV ne peut pas être null");
        }

        if (rdv.getDate() == null) {
            throw new IllegalArgumentException("La date du RDV est obligatoire");
        }

        if (rdv.getHeure() == null) {
            throw new IllegalArgumentException("L'heure du RDV est obligatoire");
        }

        if (rdv.getMedecin() == null || rdv.getMedecin().getId() == null) {
            throw new IllegalArgumentException("Le médecin est obligatoire");
        }

        if (rdv.getPatient() == null || rdv.getPatient().getId() == null) {
            throw new IllegalArgumentException("Le patient est obligatoire");
        }

        // Vérifier la disponibilité du créneau
        Long medecinId = rdv.getMedecin().getId();
        LocalDate date = rdv.getDate();
        LocalTime heure = rdv.getHeure();

        if (!isTimeSlotAvailable(medecinId, date, heure)) {
            throw new IllegalArgumentException("Le créneau horaire n'est pas disponible");
        }

        // Définir le statut par défaut
        if (rdv.getStatut() == null) {
            rdv.setStatut(StatutRDV.PLANIFIE);
        }

        // Sauvegarder le RDV
        return rdvRepository.save(rdv);
    }

    @Override
    public Optional<RDV> getRDVById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID du RDV invalide");
        }

        return rdvRepository.findById(id);
    }

    @Override
    public List<RDV> getAllRDVs() {
        return rdvRepository.findAll();
    }

    @Override
    public RDV updateRDV(Long id, RDV rdv) {
        // Validation
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID du RDV invalide");
        }

        if (rdv == null) {
            throw new IllegalArgumentException("Le RDV ne peut pas être null");
        }

        // Vérifier l'existence
        Optional<RDV> existingRDV = rdvRepository.findById(id);
        if (existingRDV.isEmpty()) {
            throw new IllegalArgumentException("RDV non trouvé avec l'ID: " + id);
        }

        // Mettre à jour
        RDV toUpdate = existingRDV.get();
        updateRDVFields(toUpdate, rdv);

        return rdvRepository.save(toUpdate);
    }

    @Override
    public void deleteRDV(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID du RDV invalide");
        }

        // Vérifier l'existence
        Optional<RDV> rdv = rdvRepository.findById(id);
        if (rdv.isEmpty()) {
            throw new IllegalArgumentException("RDV non trouvé avec l'ID: " + id);
        }

        rdvRepository.deleteById(id);
    }

    @Override
    public List<RDV> getRDVsByMedecinAndDate(Long medecinId, LocalDate date) {
        validateMedecinIdAndDate(medecinId, date);
        return rdvRepository.findByMedecinAndDate(medecinId, date);
    }

    @Override
    public List<RDV> getRDVsByMedecinAndDateRange(Long medecinId, LocalDate start, LocalDate end) {
        validateMedecinId(medecinId);

        if (start == null) {
            throw new IllegalArgumentException("La date de début ne peut pas être null");
        }

        if (end == null) {
            throw new IllegalArgumentException("La date de fin ne peut pas être null");
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        return rdvRepository.findByMedecinAndDateRange(medecinId, start, end);
    }

    @Override
    public List<RDV> getRDVsByPatient(Long patientId) {
        if (patientId == null || patientId <= 0) {
            throw new IllegalArgumentException("ID du patient invalide");
        }

        return rdvRepository.findByPatient(patientId);
    }

    @Override
    public List<RDV> getRDVsByAgenda(Long agendaId) {
        if (agendaId == null || agendaId <= 0) {
            throw new IllegalArgumentException("ID de l'agenda invalide");
        }

        return rdvRepository.findByAgenda(agendaId);
    }

    @Override
    public RDV updateRDVStatus(Long rdvId, StatutRDV newStatus) {
        if (rdvId == null || rdvId <= 0) {
            throw new IllegalArgumentException("ID du RDV invalide");
        }

        if (newStatus == null) {
            throw new IllegalArgumentException("Le nouveau statut ne peut pas être null");
        }

        // Vérifier l'existence
        Optional<RDV> rdv = rdvRepository.findById(rdvId);
        if (rdv.isEmpty()) {
            throw new IllegalArgumentException("RDV non trouvé avec l'ID: " + rdvId);
        }

        RDV toUpdate = rdv.get();
        toUpdate.setStatut(newStatus);

        return rdvRepository.save(toUpdate);
    }

    @Override
    public List<RDV> getRDVsByStatus(StatutRDV status) {
        if (status == null) {
            throw new IllegalArgumentException("Le statut ne peut pas être null");
        }

        List<RDV> allRDVs = rdvRepository.findAll();

        return allRDVs.stream()
                .filter(rdv -> status.equals(rdv.getStatut()))
                .toList();
    }

    @Override
    public List<RDV> getRDVsByMedecinAndStatus(Long medecinId, StatutRDV status) {
        validateMedecinId(medecinId);

        if (status == null) {
            throw new IllegalArgumentException("Le statut ne peut pas être null");
        }

        List<RDV> rdvsByMedecin = getRDVsByMedecinAndDateRange(medecinId,
                LocalDate.now().minusYears(1),
                LocalDate.now().plusYears(1)
        );

        return rdvsByMedecin.stream()
                .filter(rdv -> status.equals(rdv.getStatut()))
                .toList();
    }

    @Override
    public boolean isTimeSlotAvailable(Long medecinId, LocalDate date, LocalTime heure) {
        validateMedecinIdAndDate(medecinId, date);

        if (heure == null) {
            throw new IllegalArgumentException("L'heure ne peut pas être null");
        }

        // Récupérer tous les RDVs du médecin pour cette date
        List<RDV> rdvsDuJour = rdvRepository.findByMedecinAndDate(medecinId, date);

        // Vérifier si un RDV existe déjà à cette heure
        return rdvsDuJour.stream()
                .noneMatch(rdv -> rdv.getHeure().equals(heure) &&
                        !StatutRDV.ANNULE.equals(rdv.getStatut()) &&
                        !StatutRDV.TERMINE.equals(rdv.getStatut()));
    }

    @Override
    public List<LocalTime> getAvailableTimeSlots(Long medecinId, LocalDate date) {
        validateMedecinIdAndDate(medecinId, date);

        // Heures de travail par défaut (9h-17h)
        List<LocalTime> allTimeSlots = new ArrayList<>();
        for (int hour = 9; hour < 17; hour++) {
            allTimeSlots.add(LocalTime.of(hour, 0));
            allTimeSlots.add(LocalTime.of(hour, 30));
        }

        // Récupérer les RDVs existants
        List<RDV> rdvsDuJour = rdvRepository.findByMedecinAndDate(medecinId, date);
        List<LocalTime> occupiedSlots = rdvsDuJour.stream()
                .filter(rdv -> !StatutRDV.ANNULE.equals(rdv.getStatut()))
                .map(RDV::getHeure)
                .toList();

        // Filtrer les créneaux disponibles
        return allTimeSlots.stream()
                .filter(slot -> !occupiedSlots.contains(slot))
                .toList();
    }

    @Override
    public List<RDV> getTodaysRDVsByMedecin(Long medecinId) {
        validateMedecinId(medecinId);

        LocalDate today = LocalDate.now();
        return rdvRepository.findByMedecinAndDate(medecinId, today);
    }

    @Override
    public List<RDV> getUpcomingRDVsByMedecin(Long medecinId, int daysAhead) {
        validateMedecinId(medecinId);

        if (daysAhead <= 0) {
            throw new IllegalArgumentException("Le nombre de jours doit être positif");
        }

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(daysAhead);

        return rdvRepository.findByMedecinAndDateRange(medecinId, start, end);
    }

    @Override
    public void cancelRDV(Long rdvId, String reason) {
        if (rdvId == null || rdvId <= 0) {
            throw new IllegalArgumentException("ID du RDV invalide");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("La raison d'annulation est obligatoire");
        }

        // Vérifier l'existence
        Optional<RDV> rdv = rdvRepository.findById(rdvId);
        if (rdv.isEmpty()) {
            throw new IllegalArgumentException("RDV non trouvé avec l'ID: " + rdvId);
        }

        RDV toUpdate = rdv.get();
        toUpdate.setStatut(StatutRDV.ANNULE);
        toUpdate.setNoteMedecin("Annulé - Raison: " + reason);

        rdvRepository.save(toUpdate);
    }

    @Override
    public void confirmRDV(Long rdvId) {
        if (rdvId == null || rdvId <= 0) {
            throw new IllegalArgumentException("ID du RDV invalide");
        }

        // Vérifier l'existence
        Optional<RDV> rdv = rdvRepository.findById(rdvId);
        if (rdv.isEmpty()) {
            throw new IllegalArgumentException("RDV non trouvé avec l'ID: " + rdvId);
        }

        RDV toUpdate = rdv.get();
        toUpdate.setStatut(StatutRDV.CONFIRME);

        rdvRepository.save(toUpdate);
    }

    @Override
    public void rescheduleRDV(Long rdvId, LocalDate newDate, LocalTime newHeure) {
        if (rdvId == null || rdvId <= 0) {
            throw new IllegalArgumentException("ID du RDV invalide");
        }

        if (newDate == null) {
            throw new IllegalArgumentException("La nouvelle date ne peut pas être null");
        }

        if (newHeure == null) {
            throw new IllegalArgumentException("La nouvelle heure ne peut pas être null");
        }

        // Vérifier l'existence
        Optional<RDV> rdv = rdvRepository.findById(rdvId);
        if (rdv.isEmpty()) {
            throw new IllegalArgumentException("RDV non trouvé avec l'ID: " + rdvId);
        }

        RDV toUpdate = rdv.get();

        // Vérifier la disponibilité du nouveau créneau
        Long medecinId = toUpdate.getMedecin().getId();
        if (!isTimeSlotAvailable(medecinId, newDate, newHeure)) {
            throw new IllegalArgumentException("Le nouveau créneau n'est pas disponible");
        }

        // Mettre à jour
        toUpdate.setDate(newDate);
        toUpdate.setHeure(newHeure);
        toUpdate.setStatut(StatutRDV.REPROGRAMME);
        toUpdate.setNoteMedecin((toUpdate.getNoteMedecin() != null ? toUpdate.getNoteMedecin() + " " : "") +
                "Reprogrammé le " + LocalDateTime.now());

        rdvRepository.save(toUpdate);
    }

    @Override
    public long countRDVsByMedecinAndDate(Long medecinId, LocalDate date) {
        validateMedecinIdAndDate(medecinId, date);

        List<RDV> rdvs = rdvRepository.findByMedecinAndDate(medecinId, date);
        return rdvs.size();
    }

    @Override
    public long countRDVsByPatient(Long patientId) {
        if (patientId == null || patientId <= 0) {
            return 0;
        }

        List<RDV> rdvs = rdvRepository.findByPatient(patientId);
        return rdvs.size();
    }

    @Override
    public long countRDVsByStatus(StatutRDV status) {
        if (status == null) {
            return 0;
        }

        List<RDV> allRDVs = rdvRepository.findAll();
        return allRDVs.stream()
                .filter(rdv -> status.equals(rdv.getStatut()))
                .count();
    }

    @Override
    public List<RDV> getRDVsRequiringReminder() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<RDV> allRDVs = rdvRepository.findAll();

        return allRDVs.stream()
                .filter(rdv -> {
                    if (rdv.getDate() == null || rdv.getStatut() == null) {
                        return false;
                    }

                    // RDV pour demain avec statut PLANIFIE ou CONFIRME
                    boolean isTomorrow = rdv.getDate().equals(tomorrow);
                    boolean isValidStatus = StatutRDV.PLANIFIE.equals(rdv.getStatut()) ||
                            StatutRDV.CONFIRME.equals(rdv.getStatut());

                    return isTomorrow && isValidStatus;
                })
                .toList();
    }

    @Override
    public List<RDV> getMissedRDVs() {
        LocalDate today = LocalDate.now();

        List<RDV> allRDVs = rdvRepository.findAll();

        return allRDVs.stream()
                .filter(rdv -> {
                    if (rdv.getDate() == null || rdv.getStatut() == null) {
                        return false;
                    }

                    // RDV passés avec statut PLANIFIE ou CONFIRME
                    boolean isPast = rdv.getDate().isBefore(today);
                    boolean isValidStatus = StatutRDV.PLANIFIE.equals(rdv.getStatut()) ||
                            StatutRDV.CONFIRME.equals(rdv.getStatut());

                    return isPast && isValidStatus;
                })
                .toList();
    }

    // ========== Méthodes Privées ==========

    private void validateMedecinId(Long medecinId) {
        if (medecinId == null || medecinId <= 0) {
            throw new IllegalArgumentException("ID du médecin invalide");
        }
    }

    private void validateMedecinIdAndDate(Long medecinId, LocalDate date) {
        validateMedecinId(medecinId);

        if (date == null) {
            throw new IllegalArgumentException("La date ne peut pas être null");
        }
    }

    private void updateRDVFields(RDV toUpdate, RDV newData) {
        if (newData.getDate() != null) {
            toUpdate.setDate(newData.getDate());
        }

        if (newData.getHeure() != null) {
            toUpdate.setHeure(newData.getHeure());
        }

        if (newData.getMotif() != null) {
            toUpdate.setMotif(newData.getMotif());
        }

        if (newData.getStatut() != null) {
            toUpdate.setStatut(newData.getStatut());
        }

        if (newData.getNoteMedecin() != null) {
            toUpdate.setNoteMedecin(newData.getNoteMedecin());
        }

        if (newData.getAgendaMensuelId() != null) {
            toUpdate.setAgendaMensuelId(newData.getAgendaMensuelId());
        }

        if (newData.getPatient() != null && newData.getPatient().getId() != null) {
            toUpdate.setPatient(newData.getPatient());
        }

        if (newData.getMedecin() != null && newData.getMedecin().getId() != null) {
            toUpdate.setMedecin(newData.getMedecin());
        }

        if (newData.getDossierMedical() != null && newData.getDossierMedical().getId() != null) {
            toUpdate.setDossierMedical(newData.getDossierMedical());
        }
    }
}
