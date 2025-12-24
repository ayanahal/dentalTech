package ma.dentalTech.service.modules.cabinet.api;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import java.util.Map;

public interface ParametrageService {

    // ========== Paramètres généraux du cabinet ==========
    AgendaMensuel getCabinetConfiguration(Long cabinetId);

    CabinetMedical updateCabinetConfiguration(Long cabinetId, CabinetMedical configuration);

    // ========== Paramètres financiers ==========
    Map<String, Object> getFinancialSettings(Long cabinetId);

    void updateFinancialSettings(Long cabinetId, Map<String, Object> settings);

    // ========== Paramètres de facturation ==========
    Map<String, Object> getBillingSettings(Long cabinetId);

    void updateBillingSettings(Long cabinetId, Map<String, Object> settings);

    // ========== Paramètres de notification ==========
    Map<String, Object> getNotificationSettings(Long cabinetId);

    void updateNotificationSettings(Long cabinetId, Map<String, Object> settings);

    // ========== Paramètres de sécurité ==========
    Map<String, Object> getSecuritySettings(Long cabinetId);

    void updateSecuritySettings(Long cabinetId, Map<String, Object> settings);

    // ========== Sauvegarde/Restauration ==========
    void backupConfiguration(Long cabinetId, String backupPath);

    void restoreConfiguration(Long cabinetId, String backupPath);

    // ========== Réinitialisation ==========
    void resetToDefaults(Long cabinetId);

    // ========== Export/Import ==========
    String exportConfiguration(Long cabinetId);

    void importConfiguration(Long cabinetId, String configJson);
}
