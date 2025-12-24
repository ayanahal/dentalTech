package ma.dentalTech.service.modules.cabinet.impl;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.service.modules.cabinet.api.CabinetMedicalService;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.users.Staff;
import ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository;
import java.util.List;
import java.util.Optional;

public class CabinetMedicalServiceImpl implements CabinetMedicalService {

    private final CabinetMedicalRepository cabinetRepository;

    public CabinetMedicalServiceImpl(CabinetMedicalRepository cabinetRepository) {
        this.cabinetRepository = cabinetRepository;
    }

    // ========== CRUD Operations ==========

    @Override
    public CabinetMedical createCabinet(CabinetMedical cabinet) {
        // Validation
        if (cabinet == null) {
            throw new IllegalArgumentException("Le cabinet ne peut pas être null");
        }

        if (cabinet.getNom() == null || cabinet.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du cabinet est obligatoire");
        }

        if (cabinet.getEmail() == null || cabinet.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email du cabinet est obligatoire");
        }

        // Vérifier si le cabinet existe déjà
        Optional<CabinetMedical> existingCabinet = cabinetRepository.findByNom(cabinet.getNom());
        if (existingCabinet.isPresent()) {
            throw new IllegalArgumentException("Un cabinet avec ce nom existe déjà");
        }

        existingCabinet = cabinetRepository.findByEmail(cabinet.getEmail());
        if (existingCabinet.isPresent()) {
            throw new IllegalArgumentException("Un cabinet avec cet email existe déjà");
        }

        // Sauvegarder le cabinet
        return cabinetRepository.save(cabinet);
    }

    @Override
    public Optional<AgendaMensuel> getCabinetById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID du cabinet invalide");
        }

        return cabinetRepository.findById(id);
    }

    @Override
    public List<CabinetMedical> getAllCabinets() {
        return cabinetRepository.findAll();
    }

    @Override
    public AgendaMensuel updateCabinet(Long id, CabinetMedical cabinet) {
        // Validation
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID du cabinet invalide");
        }

        if (cabinet == null) {
            throw new IllegalArgumentException("Le cabinet ne peut pas être null");
        }

        // Vérifier l'existence
        Optional<AgendaMensuel> existingCabinet = cabinetRepository.findById(id);
        if (existingCabinet.isEmpty()) {
            throw new IllegalArgumentException("Cabinet non trouvé avec l'ID: " + id);
        }

        // Vérifier les doublons (nom)
        if (cabinet.getNom() != null && !cabinet.getNom().trim().isEmpty()) {
            Optional<CabinetMedical> duplicate = cabinetRepository.findByNom(cabinet.getNom());
            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new IllegalArgumentException("Un autre cabinet avec ce nom existe déjà");
            }
        }

        // Vérifier les doublons (email)
        if (cabinet.getEmail() != null && !cabinet.getEmail().trim().isEmpty()) {
            Optional<CabinetMedical> duplicate = cabinetRepository.findByEmail(cabinet.getEmail());
            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new IllegalArgumentException("Un autre cabinet avec cet email existe déjà");
            }
        }

        // Mettre à jour
        AgendaMensuel toUpdate = existingCabinet.get();
        updateCabinetFields(toUpdate, cabinet);

        return cabinetRepository.save(toUpdate);
    }

    private void updateCabinetFields(AgendaMensuel toUpdate, CabinetMedical cabinet) {
    }

    @Override
    public void deleteCabinet(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID du cabinet invalide");
        }

        if (!cabinetRepository.existsById(id)) {
            throw new IllegalArgumentException("Cabinet non trouvé avec l'ID: " + id);
        }

        cabinetRepository.deleteById(id);
    }

    // ========== Search Operations ==========

    @Override
    public Optional<CabinetMedical> getCabinetByNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }

        return cabinetRepository.findByNom(nom);
    }

    @Override
    public Optional<CabinetMedical> getCabinetByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }

        return cabinetRepository.findByEmail(email);
    }

    @Override
    public List<CabinetMedical> searchCabinetsByNomOrAdresse(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot-clé de recherche ne peut pas être vide");
        }

        return cabinetRepository.searchByNomOrAdresse(keyword);
    }

    // ========== Validation & Checks ==========

    @Override
    public boolean cabinetExistsById(Long id) {
        if (id == null || id <= 0) {
            return false;
        }

        return cabinetRepository.existsById(id);
    }

    @Override
    public boolean cabinetExistsByNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return false;
        }

        return cabinetRepository.findByNom(nom).isPresent();
    }

    @Override
    public boolean cabinetExistsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return cabinetRepository.findByEmail(email).isPresent();
    }

    // ========== Statistics ==========

    @Override
    public long countCabinets() {
        return cabinetRepository.count();
    }

    @Override
    public List<CabinetMedical> getCabinetsPaginated(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Le numéro de page ne peut pas être négatif");
        }

        if (size <= 0) {
            throw new IllegalArgumentException("La taille de la page doit être positive");
        }

        int offset = page * size;
        return cabinetRepository.findPage(size, offset);
    }

    // ========== Staff Management ==========

    @Override
    public void addStaffToCabinet(Long cabinetId, Long staffId) {
        validateCabinetAndStaffIds(cabinetId, staffId);

        // Vérifier que le cabinet existe
        if (!cabinetRepository.existsById(cabinetId)) {
            throw new IllegalArgumentException("Cabinet non trouvé avec l'ID: " + cabinetId);
        }

        cabinetRepository.addStaffToCabinet(cabinetId, staffId);
    }

    @Override
    public void removeStaffFromCabinet(Long cabinetId, Long staffId) {
        validateCabinetAndStaffIds(cabinetId, staffId);

        cabinetRepository.removeStaffFromCabinet(cabinetId, staffId);
    }

    @Override
    public void removeAllStaffFromCabinet(Long cabinetId) {
        if (cabinetId == null || cabinetId <= 0) {
            throw new IllegalArgumentException("ID du cabinet invalide");
        }

        cabinetRepository.removeAllStaffFromCabinet(cabinetId);
    }

    @Override
    public List<Staff> getCabinetStaff(Long cabinetId) {
        if (cabinetId == null || cabinetId <= 0) {
            throw new IllegalArgumentException("ID du cabinet invalide");
        }

        return cabinetRepository.getStaffOfCabinet(cabinetId);
    }

    @Override
    public List<CabinetMedical> getCabinetsByStaff(Long staffId) {
        if (staffId == null || staffId <= 0) {
            throw new IllegalArgumentException("ID du staff invalide");
        }

        return cabinetRepository.getCabinetsOfStaff(staffId);
    }

    // ========== Méthodes Privées ==========

    private void validateCabinetAndStaffIds(Long cabinetId, Long staffId) {
        if (cabinetId == null || cabinetId <= 0) {
            throw new IllegalArgumentException("ID du cabinet invalide");
        }

        if (staffId == null || staffId <= 0) {
            throw new IllegalArgumentException("ID du staff invalide");
        }
    }

    private void updateCabinetFields(CabinetMedical toUpdate, CabinetMedical newData) {
        if (newData.getNom() != null && !newData.getNom().trim().isEmpty()) {
            toUpdate.setNom(newData.getNom());
        }

        if (newData.getEmail() != null && !newData.getEmail().trim().isEmpty()) {
            toUpdate.setEmail(newData.getEmail());
        }

        if (newData.getLogo() != null) {
            toUpdate.setLogo(newData.getLogo());
        }

        if (newData.getAdresse() != null) {
            toUpdate.setAdresse(newData.getAdresse());
        }

        if (newData.getCin() != null) {
            toUpdate.setCin(newData.getCin());
        }

        if (newData.getTel1() != null) {
            toUpdate.setTel1(newData.getTel1());
        }

        if (newData.getTel2() != null) {
            toUpdate.setTel2(newData.getTel2());
        }

        if (newData.getSiteWeb() != null) {
            toUpdate.setSiteWeb(newData.getSiteWeb());
        }

        if (newData.getInstagram() != null) {
            toUpdate.setInstagram(newData.getInstagram());
        }

        if (newData.getFacebook() != null) {
            toUpdate.setFacebook(newData.getFacebook());
        }

        if (newData.getDescription() != null) {
            toUpdate.setDescription(newData.getDescription());
        }
    }
}
