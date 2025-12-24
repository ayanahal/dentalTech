package ma.dentalTech.service.modules.agendas.test;

import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.service.modules.agendas.api.AgendaMensuelService;
import ma.dentalTech.service.modules.agendas.api.RDVService;
import ma.dentalTech.service.modules.agendas.impl.AgendaMensuelServiceImpl;
import ma.dentalTech.service.modules.agendas.impl.RDVServiceImpl;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.entities.enums.StatutRDV;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Test unitaire SIMPLIFIÉ du module Agenda
 */
public class TestAgenda {

    private AgendaMensuelService agendaService;
    private RDVService rdvService;

    // Mocks simplifiés
    private SimpleAgendaRepo mockAgendaRepo;
    private SimpleRDVRepo mockRDVRepo;

    // Statistiques
    private int testsPassed = 0;
    private int testsFailed = 0;

    /**
     * Repository SIMPLE pour Agenda (sans héritage CrudRepository)
     */
    private static class SimpleAgendaRepo {
        private final Map<Long, AgendaMensuel> agendas = new HashMap<>();
        private long nextId = 1;

        public AgendaMensuel save(AgendaMensuel agenda) {
            if (agenda.getId() == null || agenda.getId() == 0) {
                agenda.setId(nextId++);
            }
            agendas.put(agenda.getId(), agenda);
            return agenda;
        }

        public Optional<AgendaMensuel> findById(Long id) {
            return Optional.ofNullable(agendas.get(id));
        }

        public List<AgendaMensuel> findAll() {
            return new ArrayList<>(agendas.values());
        }

        public void deleteById(Long id) {
            agendas.remove(id);
        }

        public Optional<AgendaMensuel> findByMedecinAndMoisAndAnnee(Long medecinId, String moisEnumName, int annee) {
            return agendas.values().stream()
                    .filter(a -> a.getMedecinId() != null &&
                            a.getMedecinId().equals(medecinId) &&
                            a.getMois() != null &&
                            a.getMois().name().equals(moisEnumName) &&
                            a.getAnnee() == annee)
                    .findFirst();
        }

        public List<AgendaMensuel> findByMedecin(Long medecinId) {
            return agendas.values().stream()
                    .filter(a -> a.getMedecinId() != null && a.getMedecinId().equals(medecinId))
                    .toList();
        }

        public boolean existsById(Long id) {
            return agendas.containsKey(id);
        }

        public void clear() {
            agendas.clear();
            nextId = 1;
        }
    }

    /**
     * Repository SIMPLE pour RDV (sans héritage CrudRepository)
     */
    private static class SimpleRDVRepo {
        private final Map<Long, RDV> rdvs = new HashMap<>();
        private long nextId = 1;

        public RDV save(RDV rdv) {
            if (rdv.getId() == null || rdv.getId() == 0) {
                rdv.setId(nextId++);
            }
            rdvs.put(rdv.getId(), rdv);
            return rdv;
        }

        public Optional<AgendaMensuel> findById(Long id) {
            return Optional.ofNullable(rdvs.get(id).get());
        }

        public List<RDV> findAll() {
            return new ArrayList<>(rdvs.values());
        }

        public void deleteById(Long id) {
            rdvs.remove(id);
        }

        public List<RDV> findByMedecinAndDate(Long medecinId, LocalDate date) {
            return rdvs.values().stream()
                    .filter(r -> date.equals(r.getDate()))
                    .toList();
        }

        public List<RDV> findByMedecinAndDateRange(Long medecinId, LocalDate start, LocalDate end) {
            return rdvs.values().stream()
                    .filter(r -> r.getDate() != null &&
                            !r.getDate().isBefore(start) &&
                            !r.getDate().isAfter(end))
                    .toList();
        }

        public List<RDV> findByPatient(Long patientId) {
            return rdvs.values().stream()
                    .filter(r -> true) // Simplifié pour les tests
                    .toList();
        }

        public List<RDV> findByAgenda(Long agendaId) {
            return rdvs.values().stream()
                    .filter(r -> agendaId != null && agendaId.equals(r.getAgendaMensuelId()))
                    .toList();
        }

        public void clear() {
            rdvs.clear();
            nextId = 1;
        }
    }

    // Adaptateurs pour faire fonctionner les services avec nos repos simples
    private static class AdaptedAgendaRepo implements ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository {
        private final SimpleAgendaRepo simpleRepo;

        public AdaptedAgendaRepo(SimpleAgendaRepo simpleRepo) {
            this.simpleRepo = simpleRepo;
        }

        @Override
        public AgendaMensuel save(AgendaMensuel agenda) {
            return simpleRepo.save(agenda);
        }

        @Override
        public CabinetMedical save(RDV rdv) {
            return null;
        }

        @Override
        public Optional<AgendaMensuel> findById(Long id) {
            return simpleRepo.findById(id);
        }

        @Override
        public void create(AgendaMensuel newElement) {

        }

        @Override
        public CabinetMedical update(AgendaMensuel newValuesElement) {
            return null;
        }

        @Override
        public void delete(AgendaMensuel patient) {

        }

        @Override
        public List<AgendaMensuel> findAll() {
            return simpleRepo.findAll();
        }

        @Override
        public void deleteById(Long id) {
            simpleRepo.deleteById(id);
        }

        @Override
        public void syncAntecedentsForPatient(Patient patient) {

        }

        @Override
        public List<Patient> searchByTelephoneLike(String prefix) {
            return List.of();
        }

        @Override
        public List<Patient> findBySexe(Sexe sexe) {
            return List.of();
        }

        @Override
        public List<Patient> findByAssurance(Assurance assurance) {
            return List.of();
        }

        @Override
        public CabinetMedical save(CabinetMedical cabinet) {
            return null;
        }

        @Override
        public boolean existsById(Long id) {
            return false;
        }

        @Override
        public Optional<AgendaMensuel> findByMedecinAndMoisAndAnnee(Long medecinId, String moisEnumName, int annee) {
            return simpleRepo.findByMedecinAndMoisAndAnnee(medecinId, moisEnumName, annee);
        }

        @Override
        public List<AgendaMensuel> findByMedecin(Long medecinId) {
            return simpleRepo.findByMedecin(medecinId);
        }

        @Override
        public List<LocalDate> getJoursNonDisponibles(Long agendaId) {
            return new ArrayList<>(); // Simplifié
        }

        @Override
        public void addJourNonDisponible(Long agendaId, LocalDate date) {
            // Simplifié
        }

        @Override
        public void removeJourNonDisponible(Long agendaId, LocalDate date) {
            // Simplifié
        }

        @Override
        public void clearJoursNonDisponibles(Long agendaId) {
            // Simplifié
        }
    }

    private static class AdaptedRDVRepo implements ma.dentalTech.repository.modules.agenda.api.RDVRepository {
        private final SimpleRDVRepo simpleRepo;

        public AdaptedRDVRepo(SimpleRDVRepo simpleRepo) {
            this.simpleRepo = simpleRepo;
        }

        @Override
        public CabinetMedical save(RDV rdv) {
            return simpleRepo.save(rdv);
        }

        @Override
        public Optional<AgendaMensuel> findById(Long id) {
            return simpleRepo.findById(id);
        }

        @Override
        public void create(RDV newElement) {

        }

        @Override
        public CabinetMedical update(RDV newValuesElement) {
            return null;
        }

        @Override
        public void delete(RDV patient) {

        }

        @Override
        public List<RDV> findAll() {
            return simpleRepo.findAll();
        }

        @Override
        public void deleteById(Long id) {
            simpleRepo.deleteById(id);
        }

        @Override
        public void syncAntecedentsForPatient(Patient patient) {

        }

        @Override
        public List<Patient> searchByTelephoneLike(String prefix) {
            return List.of();
        }

        @Override
        public List<Patient> findBySexe(Sexe sexe) {
            return List.of();
        }

        @Override
        public List<Patient> findByAssurance(Assurance assurance) {
            return List.of();
        }

        @Override
        public CabinetMedical save(CabinetMedical cabinet) {
            return null;
        }

        @Override
        public boolean existsById(Long id) {
            return false;
        }

        @Override
        public AgendaMensuel save(AgendaMensuel toUpdate) {
            return null;
        }

        @Override
        public List<RDV> findByMedecinAndDate(Long medecinId, LocalDate date) {
            return simpleRepo.findByMedecinAndDate(medecinId, date);
        }

        @Override
        public List<RDV> findByMedecinAndDateRange(Long medecinId, LocalDate start, LocalDate end) {
            return simpleRepo.findByMedecinAndDateRange(medecinId, start, end);
        }

        @Override
        public List<RDV> findByPatient(Long patientId) {
            return simpleRepo.findByPatient(patientId);
        }

        @Override
        public List<RDV> findByAgenda(Long agendaId) {
            return simpleRepo.findByAgenda(agendaId);
        }
    }

    public TestAgenda() {
        this.mockAgendaRepo = new SimpleAgendaRepo();
        this.mockRDVRepo = new SimpleRDVRepo();

        // Utiliser les adaptateurs
        AdaptedAgendaRepo adaptedAgendaRepo = new AdaptedAgendaRepo(mockAgendaRepo);
        AdaptedRDVRepo adaptedRDVRepo = new AdaptedRDVRepo(mockRDVRepo);

        this.agendaService = new AgendaMensuelServiceImpl(adaptedAgendaRepo);
        this.rdvService = new RDVServiceImpl(adaptedRDVRepo);
    }

    /**
     * Exécute les tests basiques
     */
    public void runAllTests() {
        System.out.println("""
            ╔══════════════════════════════╗
            ║     TEST MODULE AGENDA       ║
            ╚══════════════════════════════╝
            """);

        try {
            resetTestEnvironment();

            testCreationAgenda();
            testCreationRDV();
            testRecherche();

            printResults();

        } catch (Exception e) {
            System.err.println("\n❌ ERREUR:");
            e.printStackTrace();
            testsFailed++;
            printResults();
        }
    }

    private void testCreationAgenda() {
        System.out.println("\n1. CRÉATION AGENDA");
        System.out.println("-".repeat(40));

        try {
            AgendaMensuel agenda = new AgendaMensuel();
            agenda.setMois(Mois.JANVIER);
            agenda.setAnnee(2025);
            agenda.setMedecinId(1L);

            AgendaMensuel created = agendaService.createAgenda(agenda);

            assertNotNull(created, "Agenda créé ne doit pas être null");
            assertNotNull(created.getId(), "ID doit être généré");
            assertEquals(Mois.JANVIER, created.getMois(), "Mois doit correspondre");

            logSuccess("Création agenda OK");
        } catch (Exception e) {
            logFailure("Création agenda", e.getMessage());
        }
    }

    private void testCreationRDV() {
        System.out.println("\n2. CRÉATION RDV");
        System.out.println("-".repeat(40));

        try {
            RDV rdv = new RDV();
            rdv.setDate(LocalDate.of(2025, 5, 15));
            rdv.setHeure(LocalTime.of(10, 30));
            rdv.setMotif("Consultation");
            rdv.setStatut(StatutRDV.PLANIFIE);

            // Pour simplifier, on ne met pas les relations
            // Dans un vrai test, il faudrait mock ces objets

            RDV created = rdvService.createRDV(rdv);

            assertNotNull(created, "RDV créé ne doit pas être null");
            assertNotNull(created.getId(), "ID doit être généré");
            assertEquals(LocalTime.of(10, 30), created.getHeure(), "Heure doit correspondre");

            logSuccess("Création RDV OK");
        } catch (Exception e) {
            logFailure("Création RDV", e.getMessage());
        }
    }

    private void testRecherche() {
        System.out.println("\n3. RECHERCHE");
        System.out.println("-".repeat(40));

        try {
            // Créer un agenda pour tester la recherche
            AgendaMensuel agenda = new AgendaMensuel();
            agenda.setMois(Mois.FEVRIER);
            agenda.setAnnee(2025);
            agenda.setMedecinId(2L);
            AgendaMensuel created = agendaService.createAgenda(agenda);
            Long agendaId = created.getId();

            // Rechercher par ID
            Patient found = agendaService.getAgendaById(agendaId);
            assertTrue(found.isPresent(), "Doit trouver l'agenda par ID");

            // Liste tous
            List<AgendaMensuel> all = agendaService.getAllAgendas();
            assertTrue(all.size() >= 1, "Doit avoir au moins 1 agenda");

            logSuccess("Recherche OK");
        } catch (Exception e) {
            logFailure("Recherche", e.getMessage());
        }
    }

    private void resetTestEnvironment() {
        mockAgendaRepo.clear();
        mockRDVRepo.clear();
        testsPassed = 0;
        testsFailed = 0;
        System.out.println("Environnement réinitialisé\n");
    }

    private void logSuccess(String testName) {
        testsPassed++;
        System.out.println("✅ " + testName);
    }

    private void logFailure(String testName, String error) {
        testsFailed++;
        System.out.println("❌ " + testName + " - " + error);
    }

    private void printResults() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("RÉSULTATS");
        System.out.println("=".repeat(50));

        int total = testsPassed + testsFailed;
        double taux = total > 0 ? (testsPassed * 100.0 / total) : 0;

        System.out.println("Tests: " + total);
        System.out.println("Réussis: " + testsPassed + " ✅");
        System.out.println("Échoués: " + testsFailed + " ❌");
        System.out.printf("Taux: %.1f%%\n", taux);

        System.out.println("\n" + "=".repeat(50));

        if (testsFailed == 0) {
            System.out.println("🎉 TESTS RÉUSSIS !");
        } else {
            System.out.println("⚠️  Tests échoués.");
        }

        System.out.println("=".repeat(50));
    }

    // ========== ASSERTIONS SIMPLES ==========

    private void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError(message);
        }
    }

    private void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " (attendu: " + expected + ", obtenu: " + actual + ")");
        }
    }

    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    // ========== MAIN ==========

    public static void main(String[] args) {
        TestAgenda test = new TestAgenda();
        test.runAllTests();
    }
}