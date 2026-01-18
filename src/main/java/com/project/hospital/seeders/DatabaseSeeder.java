package com.project.hospital.seeders;

import com.project.hospital.models.Permission;
import com.project.hospital.models.Person;
import com.project.hospital.models.Role;
import com.project.hospital.models.User;
import com.project.hospital.repositorys.PermissionRepository;
import com.project.hospital.repositorys.RoleRepository;
import com.project.hospital.repositorys.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting database seeding...");

        seedPermissions();
        seedRoles();
        seedAdminUser();

        log.info("Database seeding completed!");
    }

    private void seedAdminUser() {
        log.info("Seeding admin user...");

        String adminEmail = "admin@hospital.com";
        String adminPassword = "admin123";

        if (userRepository.existsByEmailAddress(adminEmail)) {
            log.info("Admin user already exists. Skipping admin user seeding.");
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        if (adminRole == null) {
            log.warn("ADMIN role not found. Skipping admin user creation.");
            return;
        }

        User admin = new User();
        Person person = new Person();
        admin.setFullName("Admin");
        admin.setEmailAddress(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setIsVerified(true);
        admin.setRole(adminRole);
        admin.setPerson(person);
        userRepository.save(admin);
        log.info("Created default admin user: {} (password: {})", adminEmail, adminPassword);
    }

    private void seedPermissions() {
        log.info("Seeding permissions...");

        // Check if permissions already exist
        if (permissionRepository.count() > 0) {
            log.info("Permissions already exist. Skipping permission seeding.");
            return;
        }

        String[] models = {
                "Appointment",
                "Booking",
                "Permission",
                "Role",
                "User",
                "Room",
                "TreatmentType"
        };

        String[] actions = {"create", "update", "delete", "view"};

        List<Permission> permissions = new ArrayList<>();

        for (String model : models) {
            for (String action : actions) {
                Permission permission = Permission.builder()
                        .action(model.toLowerCase() + ":" + action)
                        .build();
                permissions.add(permission);
            }
        }

        permissionRepository.saveAll(permissions);
        log.info("Created {} permissions", permissions.size());
    }

    private void seedRoles() {
        log.info("Seeding roles...");

        // Check if roles already exist
        if (roleRepository.count() > 0) {
            log.info("Roles already exist. Skipping role seeding.");
            return;
        }

        // Get all permissions
        List<Permission> allPermissions = permissionRepository.findAll();
        Map<String, Permission> permissionMap = new HashMap<>();
        for (Permission p : allPermissions) {
            permissionMap.put(p.getAction(), p);
        }

        // Create Admin Role - has all permissions
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole.setPermissions(new HashSet<>(allPermissions));
        roleRepository.save(adminRole);
        log.info("Created ADMIN role with {} permissions", allPermissions.size());

        // Create Doctor Role
        Role doctorRole = new Role();
        doctorRole.setName("DOCTOR");
        Set<Permission> doctorPermissions = new HashSet<>();

        // Doctor permissions
        addPermissionsForModel(doctorPermissions, permissionMap, "appointment",
                Arrays.asList("create", "update", "view"));
        addPermissionsForModel(doctorPermissions, permissionMap, "booking",
                Arrays.asList("view", "update"));
        addPermissionsForModel(doctorPermissions, permissionMap, "user",
                Arrays.asList("view"));
        addPermissionsForModel(doctorPermissions, permissionMap, "room",
                Arrays.asList("view"));
        addPermissionsForModel(doctorPermissions, permissionMap, "treatmenttype",
                Arrays.asList("view", "create", "update"));

        doctorRole.setPermissions(doctorPermissions);
        roleRepository.save(doctorRole);
        log.info("Created DOCTOR role with {} permissions", doctorPermissions.size());

        // Create Patient Role
        Role patientRole = new Role();
        patientRole.setName("PATIENT");
        Set<Permission> patientPermissions = new HashSet<>();

        // Patient permissions - limited access
        addPermissionsForModel(patientPermissions, permissionMap, "appointment",
                Arrays.asList("create", "view"));
        addPermissionsForModel(patientPermissions, permissionMap, "booking",
                Arrays.asList("create", "view"));
        addPermissionsForModel(patientPermissions, permissionMap, "room",
                Arrays.asList("view"));
        addPermissionsForModel(patientPermissions, permissionMap, "treatmenttype",
                Arrays.asList("view"));


        patientRole.setPermissions(patientPermissions);
        roleRepository.save(patientRole);
        log.info("Created PATIENT role with {} permissions", patientPermissions.size());
    }

    private void addPermissionsForModel(Set<Permission> permissions,
                                        Map<String, Permission> permissionMap,
                                        String model,
                                        List<String> actions) {
        for (String action : actions) {
            String key = model + ":" + action;
            Permission permission = permissionMap.get(key);
            if (permission != null) {
                permissions.add(permission);
            }
        }
    }
}