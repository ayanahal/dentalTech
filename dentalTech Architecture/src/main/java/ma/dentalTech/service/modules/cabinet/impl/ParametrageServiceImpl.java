package ma.dentalTech.service.modules.cabinet.impl;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.service.modules.cabinet.api.ParametrageService;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ParametrageServiceImpl implements ParametrageService {

    private final CabinetMedicalRepository cabinetRepository;

    public ParametrageServiceImpl(CabinetMedicalRepository cabinetRepository) {
        this.cabinetRepository = cabinetRepository;
    }

    // ========== Paramètres généraux du cabinet ==========

    @Override
    public AgendaMensuel getCabinetConfiguration(Long cabinetId) {
        validateCabinetId(cabinetId);

        Optional<AgendaMensuel> cabinet = cabinetRepository.findById(cabinetId);
        if (cabinet.isEmpty()) {
            throw new IllegalArgumentException("Cabinet non trouvé avec l'ID: " + cabinetId);
        }

        return cabinet.get();
    }

    @Override
    public CabinetMedical updateCabinetConfiguration(Long cabinetId, CabinetMedical configuration) {
        validateCabinetId(cabinetId);

        if (configuration == null) {
            throw new IllegalArgumentException("La configuration ne peut pas être null");
        }

        Optional<AgendaMensuel> existingCabinet = cabinetRepository.findById(cabinetId);
        if (existingCabinet.isEmpty()) {
            throw new IllegalArgumentException("Cabinet non trouvé avec l'ID: " + cabinetId);
        }

        AgendaMensuel toUpdate = existingCabinet.get();

        // Mettre à jour les champs de configuration
        if (configuration.getNom() != null) {
            toUpdate.setNom(configuration.getNom());
        }

        if (configuration.getAdresse() != null) {
            toUpdate.setAdresse(configuration.getAdresse());
        }

        if (configuration.getTel1() != null) {
            toUpdate.setTel1(configuration.getTel1());
        }

        if (configuration.getEmail() != null) {
            toUpdate.setEmail(configuration.getEmail());
        }

        if (configuration.getSiteWeb() != null) {
            toUpdate.setSiteWeb(configuration.getSiteWeb());
        }

        if (configuration.getLogo() != null) {
            toUpdate.setLogo(configuration.getLogo());
        }

        return cabinetRepository.save(toUpdate);
    }

    // ========== Paramètres financiers ==========

    @Override
    public Map<String, Object> getFinancialSettings(Long cabinetId) {
        validateCabinetId(cabinetId);

        Map<String, Object> settings = new HashMap<>();

        // Exemple de paramètres financiers
        settings.put("tva", 20.0); // TVA par défaut
        settings.put("devise", "MAD");
        settings.put("arrondi_facture", true);
        settings.put("seuil_alerte_solde", 10000.0);
        settings.put("mode_calcul_remises", "pourcentage");

        return settings;
    }

    @Override
    public void updateFinancialSettings(Long cabinetId, Map<String, Object> settings) {
        validateCabinetId(cabinetId);

        if (settings == null || settings.isEmpty()) {
            throw new IllegalArgumentException("Les paramètres financiers ne peuvent pas être vides");
        }

        // Ici, normalement, vous sauvegarderiez dans une table spécifique
        // Pour l'exemple, on affiche juste les paramètres
        System.out.println("Paramètres financiers mis à jour pour le cabinet " + cabinetId + ": " + settings);
    }

    // ========== Paramètres de facturation ==========

    @Override
    public Map<String, Object> getBillingSettings(Long cabinetId) {
        validateCabinetId(cabinetId);

        Map<String, Object> settings = new HashMap<>();

        settings.put("prefixe_facture", "FACT");
        settings.put("prochain_numero", 1001);
        settings.put("delai_paiement", 30);
        settings.put("penalite_retard", 5.0);
        settings.put("mention_legale", "TVA non applicable, article 293 B du CGI");
        settings.put("modele_facture", "standard");

        return settings;
    }

    @Override
    public void updateBillingSettings(Long cabinetId, Map<String, Object> settings) {
        validateCabinetId(cabinetId);

        if (settings == null || settings.isEmpty()) {
            throw new IllegalArgumentException("Les paramètres de facturation ne peuvent pas être vides");
        }

        // Validation des paramètres
        if (settings.containsKey("delai_paiement")) {
            Integer delai = (Integer) settings.get("delai_paiement");
            if (delai != null && delai < 0) {
                throw new IllegalArgumentException("Le délai de paiement ne peut pas être négatif");
            }
        }

        System.out.println("Paramètres de facturation mis à jour pour le cabinet " + cabinetId);
    }

    // ========== Paramètres de notification ==========

    @Override
    public Map<String, Object> getNotificationSettings(Long cabinetId) {
        validateCabinetId(cabinetId);

        Map<String, Object> settings = new HashMap<>();

        settings.put("notifications_email", true);
        settings.put("notifications_sms", false);
        settings.put("rappel_rdv", true);
        settings.put("delai_rappel", 24); // heures
        settings.put("alertes_financieres", true);
        settings.put("seuil_alerte", 5000.0);

        return settings;
    }

    @Override
    public void updateNotificationSettings(Long cabinetId, Map<String, Object> settings) {
        validateCabinetId(cabinetId);

        if (settings == null || settings.isEmpty()) {
            throw new IllegalArgumentException("Les paramètres de notification ne peuvent pas être vides");
        }

        System.out.println("Paramètres de notification mis à jour pour le cabinet " + cabinetId);
    }

    // ========== Paramètres de sécurité ==========

    @Override
    public Map<String, Object> getSecuritySettings(Long cabinetId) {
        validateCabinetId(cabinetId);

        Map<String, Object> settings = new HashMap<>();

        settings.put("complexite_mot_de_passe", "moyenne");
        settings.put("expiration_mot_de_passe", 90); // jours
        settings.put("verification_double_facteur", false);
        settings.put("session_timeout", 30); // minutes
        settings.put("ip_restriction", false);
        settings.put("journalisation", true);

        return settings;
    }

    @Override
    public void updateSecuritySettings(Long cabinetId, Map<String, Object> settings) {
        validateCabinetId(cabinetId);

        if (settings == null || settings.isEmpty()) {
            throw new IllegalArgumentException("Les paramètres de sécurité ne peuvent pas être vides");
        }

        System.out.println("Paramètres de sécurité mis à jour pour le cabinet " + cabinetId);
    }

    // ========== Sauvegarde/Restauration ==========

    @Override
    public void backupConfiguration(Long cabinetId, String backupPath) {
        validateCabinetId(cabinetId);

        if (backupPath == null || backupPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Le chemin de sauvegarde ne peut pas être vide");
        }

        // Implémentation de la sauvegarde
        System.out.println("Sauvegarde de la configuration du cabinet " + cabinetId + " vers: " + backupPath);

        // Ici, vous pourriez exporter la configuration en JSON et la sauvegarder
        String config = exportConfiguration(cabinetId);

        // Écrire dans un fichier (exemple simplifié)
        try {
            java.nio.file.Files.write(
                    java.nio.file.Paths.get(backupPath, "cabinet_" + cabinetId + "_config.json"),
                    config.getBytes()
            );
            System.out.println("Sauvegarde terminée avec succès");
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde: " + e.getMessage(), e);
        }
    }

    @Override
    public void restoreConfiguration(Long cabinetId, String backupPath) {
        validateCabinetId(cabinetId);

        if (backupPath == null || backupPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Le chemin de restauration ne peut pas être vide");
        }

        // Vérifier que le fichier existe
        java.nio.file.Path path = java.nio.file.Paths.get(backupPath);
        if (!java.nio.file.Files.exists(path)) {
            throw new IllegalArgumentException("Fichier de sauvegarde non trouvé: " + backupPath);
        }

        System.out.println("Restauration de la configuration du cabinet " + cabinetId + " depuis: " + backupPath);

        // Lire et importer la configuration
        try {
            String configJson = new String(java.nio.file.Files.readAllBytes(path));
            importConfiguration(cabinetId, configJson);
            System.out.println("Restauration terminée avec succès");
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la restauration: " + e.getMessage(), e);
        }
    }

    // ========== Réinitialisation ==========

    @Override
    public void resetToDefaults(Long cabinetId) {
        validateCabinetId(cabinetId);

        // Vérifier que le cabinet existe
        if (!cabinetRepository.existsById(cabinetId)) {
            throw new IllegalArgumentException("Cabinet non trouvé avec l'ID: " + cabinetId);
        }

        System.out.println("Réinitialisation des paramètres du cabinet " + cabinetId + " aux valeurs par défaut");

        // Ici, vous réinitialiseriez tous les paramètres
        // Pour l'exemple, on fait juste un log
    }

    // ========== Export/Import ==========

    @Override
    public String exportConfiguration(Long cabinetId) {
        validateCabinetId(cabinetId);

        Optional<RDV> cabinet = cabinetRepository.findById(cabinetId);
        if (cabinet.isEmpty()) {
            throw new IllegalArgumentException("Cabinet non trouvé avec l'ID: " + cabinetId);
        }

        // Exemple d'export JSON simple
        Map<String, Object> config = new HashMap<>();
        config.put("cabinet_id", cabinetId);
        config.put("nom", cabinet.get().getNom());
        config.put("email", cabinet.get().getEmail());
        config.put("adresse", cabinet.get().getAdresse());
        config.put("date_export", java.time.LocalDateTime.now().toString());

        // Convertir en JSON (simplifié)
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            json.append("  \"").append(entry.getKey()).append("\": ");
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else {
                json.append(entry.getValue());
            }
            json.append(",\n");
        }
        json.append("  \"export_version\": \"1.0\"\n");
        json.append("}");

        return json.toString();
    }

    @Override
    public void importConfiguration(Long cabinetId, String configJson) {
        validateCabinetId(cabinetId);

        if (configJson == null || configJson.trim().isEmpty()) {
            throw new IllegalArgumentException("La configuration JSON ne peut pas être vide");
        }

        System.out.println("Importation de la configuration pour le cabinet " + cabinetId);
        System.out.println("Configuration JSON: " + configJson.substring(0, Math.min(100, configJson.length())) + "...");

        // Ici, vous parseriez le JSON et mettriez à jour la configuration
        // Pour l'exemple, on fait juste un log
    }

    // ========== Méthode utilitaire ==========

    private void validateCabinetId(Long cabinetId) {
        if (cabinetId == null || cabinetId <= 0) {
            throw new IllegalArgumentException("ID du cabinet invalide");
        }
    }
}
