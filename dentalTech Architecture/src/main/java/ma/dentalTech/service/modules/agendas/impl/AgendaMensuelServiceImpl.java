package ma.dentalTech.service.modules.agendas.impl;

import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.service.modules.agendas.api.AgendaMensuelService;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AgendaMensuelServiceImpl implements AgendaMensuelService {

    private final AgendaMensuelRepository agendaRepository;

    public AgendaMensuelServiceImpl(AgendaMensuelRepository agendaRepository) {
        this.agendaRepository = agendaRepository;
    }

    @Override
    public CabinetMedical createAgenda(CabinetMedical agenda) {
        // Validation
        if (agenda == null) {
            throw new IllegalArgumentException("L'agenda ne peut pas être null");
        }

        if (agenda.getMois() == null) {
            throw new IllegalArgumentException("Le mois de l'agenda est obligatoire");
        }

        if (agenda.getAnnee() == null || agenda.getAnnee() <= 0) {
            throw new IllegalArgumentException("L'année de l'agenda est invalide");
        }

        if (agenda.getMedecinId() == null || agenda.getMedecinId() <= 0) {
            throw new IllegalArgumentException("L'ID du médecin est obligatoire");
        }

        // Vérifier si un agenda existe déjà pour ce médecin, mois et année
        Optional<AgendaMensuel> existingAgenda = agendaRepository.findByMedecinAndMoisAndAnnee(
                agenda.getMedecinId(),
                agenda.getMois().name(),
                agenda.getAnnee()
        );

        if (existingAgenda.isPresent()) {
            throw new IllegalArgumentException("Un agenda existe déjà pour ce médecin, mois et année");
        }

        // Sauvegarder l'agenda
        return agendaRepository.save(agenda);
    }

    @Override
    public Patient getAgendaById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de l'agenda invalide");
        }

        return agendaRepository.findById(id);
    }

    @Override
    public List<AgendaMensuel> getAllAgendas() {
        return agendaRepository.findAll();
    }

    @Override
    public AgendaMensuel updateAgenda(Long id, AgendaMensuel agenda) {
        // Validation
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de l'agenda invalide");
        }

        if (agenda == null) {
            throw new IllegalArgumentException("L'agenda ne peut pas être null");
        }

        // Vérifier l'existence
        Patient existingAgenda = agendaRepository.findById(id);
        if (existingAgenda.isEmpty()) {
            throw new IllegalArgumentException("Agenda non trouvé avec l'ID: " + id);
        }

        // Mettre à jour
        AgendaMensuel toUpdate = existingAgenda.get();
        updateAgendaFields(toUpdate, agenda);

        return agendaRepository.save(toUpdate).get();
    }

    @Override
    public void deleteAgenda(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de l'agenda invalide");
        }

        // Vérifier l'existence
        if (!agendaRepository.existsById(id)) {
            throw new IllegalArgumentException("Agenda non trouvé avec l'ID: " + id);
        }

        agendaRepository.deleteById(id);
    }

    @Override
    public Optional<AgendaMensuel> getAgendaByMedecinAndMoisAndAnnee(Long medecinId, Mois mois, int annee) {
        if (medecinId == null || medecinId <= 0) {
            throw new IllegalArgumentException("ID du médecin invalide");
        }

        if (mois == null) {
            throw new IllegalArgumentException("Le mois ne peut pas être null");
        }

        if (annee <= 0) {
            throw new IllegalArgumentException("L'année est invalide");
        }

        return agendaRepository.findByMedecinAndMoisAndAnnee(medecinId, mois.name(), annee);
    }

    @Override
    public List<AgendaMensuel> getAgendasByMedecin(Long medecinId) {
        if (medecinId == null || medecinId <= 0) {
            throw new IllegalArgumentException("ID du médecin invalide");
        }

        return agendaRepository.findByMedecin(medecinId);
    }

    @Override
    public List<LocalDate> getJoursNonDisponibles(Long agendaId) {
        if (agendaId == null || agendaId <= 0) {
            throw new IllegalArgumentException("ID de l'agenda invalide");
        }

        // Vérifier l'existence de l'agenda
        if (!agendaRepository.existsById(agendaId)) {
            throw new IllegalArgumentException("Agenda non trouvé avec l'ID: " + agendaId);
        }

        return agendaRepository.getJoursNonDisponibles(agendaId);
    }

    @Override
    public void addJourNonDisponible(Long agendaId, LocalDate date) {
        validateAgendaIdAndDate(agendaId, date);

        // Vérifier si la date est déjà marquée comme non disponible
        List<LocalDate> joursNonDisponibles = agendaRepository.getJoursNonDisponibles(agendaId);
        if (joursNonDisponibles.contains(date)) {
            throw new IllegalArgumentException("La date " + date + " est déjà marquée comme non disponible");
        }

        agendaRepository.addJourNonDisponible(agendaId, date);
    }

    @Override
    public void removeJourNonDisponible(Long agendaId, LocalDate date) {
        validateAgendaIdAndDate(agendaId, date);
        agendaRepository.removeJourNonDisponible(agendaId, date);
    }

    @Override
    public void clearJoursNonDisponibles(Long agendaId) {
        if (agendaId == null || agendaId <= 0) {
            throw new IllegalArgumentException("ID de l'agenda invalide");
        }

        agendaRepository.clearJoursNonDisponibles(agendaId);
    }

    @Override
    public boolean isDateDisponible(Long agendaId, LocalDate date) {
        if (agendaId == null || agendaId <= 0) {
            throw new IllegalArgumentException("ID de l'agenda invalide");
        }

        if (date == null) {
            throw new IllegalArgumentException("La date ne peut pas être null");
        }

        // Vérifier l'existence de l'agenda
        Optional<AgendaMensuel> agenda = agendaRepository.findById(agendaId);
        if (agenda.isEmpty()) {
            throw new IllegalArgumentException("Agenda non trouvé avec l'ID: " + agendaId);
        }

        // Vérifier si la date est dans les jours non disponibles
        List<LocalDate> joursNonDisponibles = agendaRepository.getJoursNonDisponibles(agendaId);
        return !joursNonDisponibles.contains(date);
    }

    @Override
    public boolean agendaExistsForMedecinAndMois(Long medecinId, Mois mois, int annee) {
        if (medecinId == null || medecinId <= 0) {
            return false;
        }

        if (mois == null) {
            return false;
        }

        if (annee <= 0) {
            return false;
        }

        return agendaRepository.findByMedecinAndMoisAndAnnee(medecinId, mois.name(), annee).isPresent();
    }

    @Override
    public long countAgendasByMedecin(Long medecinId) {
        if (medecinId == null || medecinId <= 0) {
            return 0;
        }

        List<AgendaMensuel> agendas = agendaRepository.findByMedecin(medecinId);
        return agendas.size();
    }

    @Override
    public List<AgendaMensuel> getAgendasByMedecinAndYear(Long medecinId, int annee) {
        if (medecinId == null || medecinId <= 0) {
            throw new IllegalArgumentException("ID du médecin invalide");
        }

        if (annee <= 0) {
            throw new IllegalArgumentException("L'année est invalide");
        }

        List<AgendaMensuel> allAgendas = agendaRepository.findByMedecin(medecinId);

        // Filtrer par année
        return allAgendas.stream()
                .filter(agenda -> agenda.getAnnee() == annee)
                .toList();
    }

    // ========== Méthodes Privées ==========

    private void validateAgendaIdAndDate(Long agendaId, LocalDate date) {
        if (agendaId == null || agendaId <= 0) {
            throw new IllegalArgumentException("ID de l'agenda invalide");
        }

        if (date == null) {
            throw new IllegalArgumentException("La date ne peut pas être null");
        }

        // Vérifier l'existence de l'agenda
        if (!agendaRepository.existsById(agendaId)) {
            throw new IllegalArgumentException("Agenda non trouvé avec l'ID: " + agendaId);
        }
    }

    private void updateAgendaFields(AgendaMensuel toUpdate, AgendaMensuel newData) {
        if (newData.getMois() != null) {
            toUpdate.setMois(newData.getMois());
        }

        if (newData.getAnnee() != null && newData.getAnnee() > 0) {
            toUpdate.setAnnee(newData.getAnnee());
        }

        if (newData.getMedecinId() != null && newData.getMedecinId() > 0) {
            toUpdate.setMedecinId(newData.getMedecinId());
        }

        if (newData.getJoursNonDisponibles() != null) {
            toUpdate.setJoursNonDisponibles(newData.getJoursNonDisponibles());
        }
    }
}