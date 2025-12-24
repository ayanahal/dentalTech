package ma.dentalTech.service.modules.cabinet.api;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.users.Staff;
import java.util.List;
import java.util.Optional;

public interface CabinetMedicalService {

    // ========== CRUD Operations ==========
    CabinetMedical createCabinet(CabinetMedical cabinet);

    Optional<AgendaMensuel> getCabinetById(Long id);

    List<CabinetMedical> getAllCabinets();

    AgendaMensuel updateCabinet(Long id, CabinetMedical cabinet);

    void deleteCabinet(Long id);

    // ========== Search Operations ==========
    Optional<CabinetMedical> getCabinetByNom(String nom);

    Optional<CabinetMedical> getCabinetByEmail(String email);

    List<CabinetMedical> searchCabinetsByNomOrAdresse(String keyword);

    // ========== Validation & Checks ==========
    boolean cabinetExistsById(Long id);

    boolean cabinetExistsByNom(String nom);

    boolean cabinetExistsByEmail(String email);

    // ========== Statistics ==========
    long countCabinets();

    List<CabinetMedical> getCabinetsPaginated(int page, int size);

    // ========== Staff Management ==========
    void addStaffToCabinet(Long cabinetId, Long staffId);

    void removeStaffFromCabinet(Long cabinetId, Long staffId);

    void removeAllStaffFromCabinet(Long cabinetId);

    List<Staff> getCabinetStaff(Long cabinetId);

    List<CabinetMedical> getCabinetsByStaff(Long staffId);
}
