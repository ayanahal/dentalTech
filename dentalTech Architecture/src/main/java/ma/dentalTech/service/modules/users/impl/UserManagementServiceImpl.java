package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.users.*;
import ma.dentalTech.repository.modules.users.api.*;
import ma.dentalTech.service.modules.users.api.UserManagementService;
import ma.dentalTech.service.modules.users.dto.*;
import ma.dentalTech.service.modules.users.utils.PasswordUtils;
import ma.dentalTech.service.modules.users.utils.UserMapper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserManagementServiceImpl implements UserManagementService {

    private final UtilisateurRepository utilisateurRepository;
    private final StaffRepository staffRepository;
    private final AdminRepository adminRepository;
    private final MedecinRepository medecinRepository;
    private final SecretaireRepository secretaireRepository;
    private final RoleRepository roleRepository;

    public UserManagementServiceImpl(
            UtilisateurRepository utilisateurRepository,
            StaffRepository staffRepository,
            AdminRepository adminRepository,
            MedecinRepository medecinRepository,
            SecretaireRepository secretaireRepository,
            RoleRepository roleRepository
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.staffRepository = staffRepository;
        this.adminRepository = adminRepository;
        this.medecinRepository = medecinRepository;
        this.secretaireRepository = secretaireRepository;
        this.roleRepository = roleRepository;
    }

    // =========================
    // Création des comptes
    // =========================

    @Override
    public UserAccountDto createAdmin(CreateAdminRequest r) {
        Admin admin = new Admin();
        populateBaseUser(admin, r);
        populateStaff(admin, r.salaire(), r.prime(), r.dateRecrutement(), r.soldeConge());

        adminRepository.create(admin);
        assignRoleToUser(admin.getId(), RoleType.ADMIN);

        return buildDto(admin);
    }

    @Override
    public UserAccountDto createMedecin(CreateMedecinRequest r) {
        Medecin m = new Medecin();
        populateBaseUser(m, r);
        populateStaff(m, r.salaire(), r.prime(), r.dateRecrutement(), r.soldeConge());
        m.setSpecialite(r.specialite());

        medecinRepository.create(m);
        assignRoleToUser(m.getId(), RoleType.MEDECIN);

        return buildDto(m);
    }

    @Override
    public UserAccountDto createSecretaire(CreateSecretaireRequest r) {
        Secretaire s = new Secretaire();
        populateBaseUser(s, r);
        populateStaff(s, r.salaire(), r.prime(), r.dateRecrutement(), r.soldeConge());
        s.setNumCNSS(r.numCNSS());
        s.setCommission(r.commission());

        secretaireRepository.create(s);
        assignRoleToUser(s.getId(), RoleType.SECRETAIRE);

        return buildDto(s);
    }

    // =========================
    // Consultation
    // =========================

    @Override
    public UserAccountDto getUserById(Long id) {
        Utilisateur u = utilisateurRepository.findById(id);
        return buildDto(u);
    }

    @Override
    public List<UserAccountDto> getAllUsers() {
        return utilisateurRepository.findAll()
                .stream()
                .map(this::buildDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserAccountDto> searchUsersByKeyword(String keyword) {
        return utilisateurRepository.searchByNom(keyword)
                .stream()
                .map(this::buildDto)
                .collect(Collectors.toList());
    }

    // =========================
    // Mise à jour
    // =========================

    @Override
    public UserAccountDto updateUserProfile(UpdateUserProfileRequest r) {
        Utilisateur u = utilisateurRepository.findById(r.id());
        u.setNom(r.nom());
        u.setEmail(r.email());
        u.setAdresse(r.adresse());
        u.setTel(r.tel());
        u.setSexe(r.sexe());
        u.setDateNaissance(r.dateNaissance());

        utilisateurRepository.update(u);
        return buildDto(u);
    }

    // =========================
    // Rôles
    // =========================

    @Override
    public void assignRoleToUser(Long utilisateurId, RoleType roleType) {
        Role role = roleRepository.findByType(roleType)
                .orElseThrow(() -> new IllegalArgumentException("Rôle introuvable"));
        roleRepository.assignRoleToUser(utilisateurId, role.getId());
    }

    @Override
    public void removeRoleFromUser(Long utilisateurId, RoleType roleType) {
        Role role = roleRepository.findByType(roleType)
                .orElseThrow(() -> new IllegalArgumentException("Rôle introuvable"));
        roleRepository.removeRoleFromUser(utilisateurId, role.getId());
    }

    // =========================
    // Méthodes internes
    // =========================

    private void populateBaseUser(Utilisateur u, Object r) {
        if (r instanceof CreateAdminRequest a) {
            u.setNom(a.nom());
            u.setEmail(a.email());
            u.setAdresse(a.adresse());
            u.setCin(a.cin());
            u.setTel(a.tel());
            u.setSexe(a.sexe());
            u.setLogin(a.login());
            u.setMotDePasse(PasswordUtils.hash(a.motDePasse()));
            u.setDateNaissance(a.dateNaissance());
        }
        if (r instanceof CreateMedecinRequest m) {
            u.setNom(m.nom());
            u.setEmail(m.email());
            u.setAdresse(m.adresse());
            u.setCin(m.cin());
            u.setTel(m.tel());
            u.setSexe(m.sexe());
            u.setLogin(m.login());
            u.setMotDePasse(PasswordUtils.hash(m.motDePasse()));
            u.setDateNaissance(m.dateNaissance());
        }
        if (r instanceof CreateSecretaireRequest s) {
            u.setNom(s.nom());
            u.setEmail(s.email());
            u.setAdresse(s.adresse());
            u.setCin(s.cin());
            u.setTel(s.tel());
            u.setSexe(s.sexe());
            u.setLogin(s.login());
            u.setMotDePasse(PasswordUtils.hash(s.motDePasse()));
            u.setDateNaissance(s.dateNaissance());
        }
    }

    private void populateStaff(Staff s, Double salaire, Double prime, java.time.LocalDate d, Integer solde) {
        s.setSalaire(salaire);
        s.setPrime(prime);
        s.setDateRecrutement(d);
        s.setSoldeConge(solde);
    }

    private UserAccountDto buildDto(Utilisateur u) {
        Set<RoleType> roles = roleRepository.findRolesByUtilisateurId(u.getId())
                .stream()
                .map(Role::getType)
                .collect(Collectors.toSet());

        Set<String> privileges = roles.stream()
                .flatMap(rt -> roleRepository.findByType(rt)
                        .map(r -> roleRepository.getPrivileges(r.getId()).stream())
                        .orElseGet(Stream::empty))
                .collect(Collectors.toSet());


        return UserMapper.toDto(u, roles, privileges);
    }
}
