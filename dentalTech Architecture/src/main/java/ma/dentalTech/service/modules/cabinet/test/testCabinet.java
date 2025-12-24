package ma.dentalTech.service.modules.cabinet.test;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.service.modules.cabinet.api.CabinetMedicalService;
import ma.dentalTech.service.modules.cabinet.api.ParametrageService;
import ma.dentalTech.service.modules.cabinet.impl.CabinetMedicalServiceImpl;
import ma.dentalTech.service.modules.cabinet.impl.ParametrageServiceImpl;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository;
import ma.dentalTech.entities.users.Staff;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Test complet du module Cabinet
 * Teste CabinetMedicalService et ParametrageService
 */
public class testCabinet {

    // Services à tester
    private CabinetMedicalService cabinetService;
    private ParametrageService parametrageService;

    // Repository mock partagé
    private MockCabinetRepository mockRepository;

    // Statistiques de test
    private int testsPassed = 0;
    private int testsFailed = 0;
    private final List<String> errors = new ArrayList<>();

    /**
     * Repository mock qui simule une base de données en mémoire
     */
    private static class MockCabinetRepository implements CabinetMedicalRepository {
        private final Map<Long, CabinetMedical> cabinets = new HashMap<>();
        private long nextCabinetId = 1;

        @Override
        public CabinetMedical save(CabinetMedical cabinet) {
            if (cabinet.getId() == null || cabinet.getId() == 0) {
                cabinet.setId(nextCabinetId++);
                cabinet.setCreatedAt(LocalDateTime.now());
            }
            cabinet.setUpdatedAt(LocalDateTime.now());
            cabinets.put(cabinet.getId(), cabinet);
            return cabinet;
        }

        @Override
        public Optional<AgendaMensuel> findById(Long id) {
            return Optional.ofNullable(cabinets.get(id));
        }

        @Override
        public void create(CabinetMedical newElement) {

        }

        @Override
        public CabinetMedical update(CabinetMedical newValuesElement) {
            return null;
        }

        @Override
        public void delete(CabinetMedical patient) {

        }

        @Override
        public List<CabinetMedical> findAll() {
            return new ArrayList<>(cabinets.values());
        }

        @Override
        public void deleteById(Long id) {
            cabinets.remove(id);
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
        public Optional<CabinetMedical> findByNom(String nom) {
            return cabinets.values().stream()
                    .filter(c -> c.getNom().equalsIgnoreCase(nom))
                    .findFirst();
        }

        @Override
        public Optional<CabinetMedical> findByEmail(String email) {
            return cabinets.values().stream()
                    .filter(c -> c.getEmail().equalsIgnoreCase(email))
                    .findFirst();
        }

        @Override
        public List<CabinetMedical> searchByNomOrAdresse(String keyword) {
            return cabinets.values().stream()
                    .filter(c -> c.getNom().toLowerCase().contains(keyword.toLowerCase()) ||
                            (c.getAdresse() != null && c.getAdresse().toLowerCase().contains(keyword.toLowerCase())))
                    .toList();
        }

        @Override
        public boolean existsById(Long id) {
            return cabinets.containsKey(id);
        }

        @Override
        public long count() {
            return cabinets.size();
        }

        @Override
        public List<CabinetMedical> findPage(int limit, int offset) {
            return cabinets.values().stream()
                    .sorted(Comparator.comparing(CabinetMedical::getId))
                    .skip(offset)
                    .limit(limit)
                    .toList();
        }

        @Override
        public void addStaffToCabinet(Long cabinetId, Long staffId) {
            System.out.println("[MOCK] Staff " + staffId + " ajouté au cabinet " + cabinetId);
        }

        @Override
        public void removeStaffFromCabinet(Long cabinetId, Long staffId) {
            System.out.println("[MOCK] Staff " + staffId + " retiré du cabinet " + cabinetId);
        }

        @Override
        public void removeAllStaffFromCabinet(Long cabinetId) {
            System.out.println("[MOCK] Tous les staff retirés du cabinet " + cabinetId);
        }

        @Override
        public List<Staff> getStaffOfCabinet(Long cabinetId) {
            return new ArrayList<>();
        }

        @Override
        public List<CabinetMedical> getCabinetsOfStaff(Long staffId) {
            return new ArrayList<>();
        }

        @Override
        public CabinetMedical save(RDV toUpdate) {
            return null;
        }

        public void clear() {
            cabinets.clear();
            nextCabinetId = 1;
        }

        public int getCabinetCount() {
            return cabinets.size();
        }
    }

    public testCabinet() {
        this.mockRepository = new MockCabinetRepository();
        this.cabinetService = new CabinetMedicalServiceImpl(mockRepository);
        this.parametrageService = new ParametrageServiceImpl(mockRepository);
    }

    /**
     * Exécute tous les tests du module Cabinet
     */
    public void runAllTests() {
        System.out.println("""
            ╔══════════════════════════════╗
            ║     TEST DU MODULE CABINET   ║
            ╚══════════════════════════════╝
            """);

        try {
            resetTestEnvironment();

            testCreationEtValidation();
            testOperationsCRUD();
            testRechercheEtFiltrage();
            testParametrage();
            testIntegration();
            testGestionStaff();

            printResults();

        } catch (Exception e) {
            System.err.println("\n❌ ERREUR CRITIQUE:");
            e.printStackTrace();
            testsFailed++;
            printResults();
        }
    }

    // ========== TESTS ==========

    private void testCreationEtValidation() {
        System.out.println("\n1. CRÉATION ET VALIDATION");
        System.out.println("-".repeat(40));

        // Test 1.1: Création valide
        try {
            CabinetMedical cabinet = createCabinetTest("Dental Care", "contact@dental.com");
            CabinetMedical created = cabinetService.createCabinet(cabinet);

            assertNotNull(created, "Cabinet créé ne doit pas être null");
            assertNotNull(created.getId(), "ID doit être généré");
            assertEquals("Dental Care", created.getNom(), "Nom doit correspondre");

            logSuccess("Création d'un cabinet valide");
        } catch (AssertionError e) {
            logFailure("Création valide", e.getMessage());
        }

        // Test 1.2: Validation données
        try {
            // Sans nom
            CabinetMedical invalid = CabinetMedical.builder().email("test@test.com").build();
            try {
                cabinetService.createCabinet(invalid);
                fail("Devrait échouer sans nom");
            } catch (IllegalArgumentException e) {
                // OK
            }

            // Sans email
            invalid = CabinetMedical.builder().nom("Test").build();
            try {
                cabinetService.createCabinet(invalid);
                fail("Devrait échouer sans email");
            } catch (IllegalArgumentException e) {
                // OK
            }

            logSuccess("Validation des données");
        } catch (AssertionError e) {
            logFailure("Validation données", e.getMessage());
        }

        // Test 1.3: Doublons
        try {
            cabinetService.createCabinet(createCabinetTest("Unique", "unique@test.com"));

            try {
                cabinetService.createCabinet(createCabinetTest("Unique", "other@test.com"));
                fail("Devrait échouer avec nom dupliqué");
            } catch (IllegalArgumentException e) {
                // OK
            }

            logSuccess("Prévention des doublons");
        } catch (AssertionError e) {
            logFailure("Prévention doublons", e.getMessage());
        }
    }

    private void testOperationsCRUD() {
        System.out.println("\n2. OPÉRATIONS CRUD");
        System.out.println("-".repeat(40));

        Long cabinetId = null;

        // Création
        try {
            CabinetMedical cabinet = createCabinetTest("CRUD Test", "crud@test.com");
            cabinet.setAdresse("123 Test Street");
            CabinetMedical created = cabinetService.createCabinet(cabinet);
            cabinetId = created.getId();

            assertNotNull(cabinetId, "Cabinet ID ne doit pas être null");
            logSuccess("Création pour tests CRUD");
        } catch (AssertionError e) {
            logFailure("Création CRUD", e.getMessage());
            return;
        }

        // Lecture
        try {
            Optional<CabinetMedical> found = cabinetService.getCabinetById(cabinetId);
            assertTrue(found.isPresent(), "Doit trouver le cabinet");
            logSuccess("Lecture par ID");
        } catch (AssertionError e) {
            logFailure("Lecture par ID", e.getMessage());
        }

        // Mise à jour
        try {
            CabinetMedical updates = CabinetMedical.builder()
                    .nom("CRUD Updated")
                    .adresse("456 New Street")
                    .build();

            CabinetMedical updated = cabinetService.updateCabinet(cabinetId, updates);
            assertEquals("CRUD Updated", updated.getNom(), "Nom doit être mis à jour");
            logSuccess("Mise à jour");
        } catch (AssertionError e) {
            logFailure("Mise à jour", e.getMessage());
        }

        // Liste
        try {
            List<CabinetMedical> all = cabinetService.getAllCabinets();
            assertTrue(all.size() >= 1, "Doit avoir au moins un cabinet");
            logSuccess("Liste tous les cabinets");
        } catch (AssertionError e) {
            logFailure("Liste cabinets", e.getMessage());
        }

        // Suppression
        try {
            assertTrue(cabinetService.cabinetExistsById(cabinetId), "Doit exister avant suppression");
            cabinetService.deleteCabinet(cabinetId);
            assertFalse(cabinetService.cabinetExistsById(cabinetId), "Ne doit plus exister");
            logSuccess("Suppression");
        } catch (AssertionError e) {
            logFailure("Suppression", e.getMessage());
        }
    }

    private void testRechercheEtFiltrage() {
        System.out.println("\n3. RECHERCHE ET FILTRAGE");
        System.out.println("-".repeat(40));

        // Créer des cabinets de test
        cabinetService.createCabinet(createCabinetTest("Clinique Paris", "paris@test.com"));
        cabinetService.createCabinet(createCabinetTest("Cabinet Lyon", "lyon@test.com"));
        cabinetService.createCabinet(createCabinetTest("Centre Marseille", "marseille@test.com"));

        // Recherche par nom
        try {
            Optional<CabinetMedical> found = cabinetService.getCabinetByNom("Clinique Paris");
            assertTrue(found.isPresent(), "Doit trouver par nom");
            logSuccess("Recherche par nom");
        } catch (AssertionError e) {
            logFailure("Recherche par nom", e.getMessage());
        }

        // Recherche par email
        try {
            Optional<CabinetMedical> found = cabinetService.getCabinetByEmail("lyon@test.com");
            assertTrue(found.isPresent(), "Doit trouver par email");
            logSuccess("Recherche par email");
        } catch (AssertionError e) {
            logFailure("Recherche par email", e.getMessage());
        }

        // Recherche par mot-clé
        try {
            List<CabinetMedical> results = cabinetService.searchCabinetsByNomOrAdresse("Paris");
            assertTrue(results.size() >= 1, "Doit trouver avec mot-clé");
            logSuccess("Recherche par mot-clé");
        } catch (AssertionError e) {
            logFailure("Recherche par mot-clé", e.getMessage());
        }

        // Pagination
        try {
            List<CabinetMedical> page = cabinetService.getCabinetsPaginated(0, 2);
            assertTrue(page.size() >= 1, "Pagination doit fonctionner");
            logSuccess("Pagination");
        } catch (AssertionError e) {
            logFailure("Pagination", e.getMessage());
        }

        // Comptage
        try {
            long count = cabinetService.countCabinets();
            assertTrue(count >= 3, "Doit compter au moins 3 cabinets");
            logSuccess("Comptage");
        } catch (AssertionError e) {
            logFailure("Comptage", e.getMessage());
        }
    }

    private void testParametrage() {
        System.out.println("\n4. PARAMÉTRAGE");
        System.out.println("-".repeat(40));

        // Créer un cabinet pour les tests
        CabinetMedical cabinet = createCabinetTest("Cabinet Param", "param@test.com");
        CabinetMedical created = cabinetService.createCabinet(cabinet);
        Long cabinetId = created.getId();

        // Configuration générale
        try {
            CabinetMedical config = parametrageService.getCabinetConfiguration(cabinetId);
            assertNotNull(config, "Configuration ne doit pas être null");
            logSuccess("Configuration générale");
        } catch (AssertionError e) {
            logFailure("Configuration générale", e.getMessage());
        }

        // Mise à jour configuration
        try {
            CabinetMedical updates = CabinetMedical.builder()
                    .nom("Cabinet Param Updated")
                    .tel1("0611111111")
                    .build();

            CabinetMedical updated = parametrageService.updateCabinetConfiguration(cabinetId, updates);
            assertEquals("Cabinet Param Updated", updated.getNom(), "Nom doit être mis à jour");
            logSuccess("Mise à jour configuration");
        } catch (AssertionError e) {
            logFailure("Mise à jour configuration", e.getMessage());
        }

        // Paramètres financiers
        try {
            Map<String, Object> settings = parametrageService.getFinancialSettings(cabinetId);
            assertNotNull(settings, "Paramètres financiers ne doivent pas être null");
            logSuccess("Paramètres financiers");
        } catch (AssertionError e) {
            logFailure("Paramètres financiers", e.getMessage());
        }

        // Export/Import
        try {
            String exported = parametrageService.exportConfiguration(cabinetId);
            assertNotNull(exported, "Export ne doit pas être null");
            logSuccess("Export configuration");
        } catch (AssertionError e) {
            logFailure("Export configuration", e.getMessage());
        }
    }

    private void testIntegration() {
        System.out.println("\n5. INTÉGRATION");
        System.out.println("-".repeat(40));

        Long cabinetId = null;

        // Création via CabinetMedicalService
        try {
            CabinetMedical cabinet = createCabinetTest("Integration Test", "integration@test.com");
            CabinetMedical created = cabinetService.createCabinet(cabinet);
            cabinetId = created.getId();

            assertNotNull(cabinetId, "Cabinet ID ne doit pas être null");
            logSuccess("Création pour intégration");
        } catch (AssertionError e) {
            logFailure("Création intégration", e.getMessage());
            return;
        }

        // Vérification via ParametrageService
        try {
            CabinetMedical config = parametrageService.getCabinetConfiguration(cabinetId);
            assertEquals("Integration Test", config.getNom(), "Les services doivent être synchronisés");
            logSuccess("Synchronisation services");
        } catch (AssertionError e) {
            logFailure("Synchronisation services", e.getMessage());
        }

        // État global cohérent
        try {
            List<CabinetMedical> all = cabinetService.getAllCabinets();
            for (CabinetMedical c : all) {
                assertNotNull(c.getId(), "Tous doivent avoir un ID");
                assertNotNull(c.getNom(), "Tous doivent avoir un nom");
                assertNotNull(c.getEmail(), "Tous doivent avoir un email");
            }
            logSuccess("État global cohérent");
        } catch (AssertionError e) {
            logFailure("État global cohérent", e.getMessage());
        }
    }

    private void testGestionStaff() {
        System.out.println("\n6. GESTION DU STAFF");
        System.out.println("-".repeat(40));

        // Créer un cabinet
        CabinetMedical cabinet = createCabinetTest("Staff Cabinet", "staff@test.com");
        CabinetMedical created = cabinetService.createCabinet(cabinet);
        Long cabinetId = created.getId();

        // Ajout staff
        try {
            cabinetService.addStaffToCabinet(cabinetId, 100L);
            cabinetService.addStaffToCabinet(cabinetId, 101L);
            logSuccess("Ajout de staff");
        } catch (Exception e) {
            logFailure("Ajout de staff", e.getMessage());
        }

        // Récupération staff
        try {
            List<Staff> staff = cabinetService.getCabinetStaff(cabinetId);
            assertNotNull(staff, "Liste staff ne doit pas être null");
            logSuccess("Récupération staff");
        } catch (Exception e) {
            logFailure("Récupération staff", e.getMessage());
        }

        // Suppression staff
        try {
            cabinetService.removeStaffFromCabinet(cabinetId, 100L);
            cabinetService.removeAllStaffFromCabinet(cabinetId);
            logSuccess("Suppression staff");
        } catch (Exception e) {
            logFailure("Suppression staff", e.getMessage());
        }
    }

    // ========== MÉTHODES UTILITAIRES ==========

    private CabinetMedical createCabinetTest(String nom, String email) {
        return CabinetMedical.builder()
                .nom(nom)
                .email(email)
                .adresse("123 Rue Test")
                .tel1("0612345678")
                .build();
    }

    private void resetTestEnvironment() {
        mockRepository.clear();
        testsPassed = 0;
        testsFailed = 0;
        errors.clear();
        System.out.println("Environnement réinitialisé");
    }

    private void logSuccess(String testName) {
        testsPassed++;
        System.out.println("✅ " + testName);
    }

    private void logFailure(String testName, String error) {
        testsFailed++;
        errors.add(testName + ": " + error);
        System.out.println("❌ " + testName);
    }

    private void printResults() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("RÉSULTATS DU TEST CABINET");
        System.out.println("=".repeat(50));

        int total = testsPassed + testsFailed;
        double rate = total > 0 ? (testsPassed * 100.0 / total) : 0;

        System.out.println("Total: " + total);
        System.out.println("Réussis: " + testsPassed + " ✅");
        System.out.println("Échoués: " + testsFailed + " ❌");
        System.out.printf("Taux: %.1f%%\n", rate);

        if (testsFailed > 0) {
            System.out.println("\nErreurs:");
            for (int i = 0; i < errors.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + errors.get(i));
            }
        }

        System.out.println("\n" + "=".repeat(50));

        if (testsFailed == 0) {
            System.out.println("🎉 MODULE CABINET TESTÉ AVEC SUCCÈS !");
        } else {
            System.out.println("⚠️  Des corrections sont nécessaires.");
        }
        System.out.println("=".repeat(50));
    }

    // ========== ASSERTIONS ==========

    private void assertNotNull(Object obj, String message) {
        if (obj == null) throw new AssertionError(message);
    }

    private void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " (attendu: " + expected + ", obtenu: " + actual + ")");
        }
    }

    private void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private void assertFalse(boolean condition, String message) {
        if (condition) throw new AssertionError(message);
    }

    private void fail(String message) {
        throw new AssertionError(message);
    }

    // ========== MAIN ==========

    public static void main(String[] args) {
        testCabinet test = new testCabinet();
        test.runAllTests();
    }
}