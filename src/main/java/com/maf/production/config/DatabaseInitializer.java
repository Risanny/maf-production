package com.maf.production.config;

import com.maf.production.model.Role;
import com.maf.production.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Инициализация ролей, если они отсутствуют
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, Role.RoleType.ROLE_ADMIN));
            roleRepository.save(new Role(null, Role.RoleType.ROLE_MANAGER));
            roleRepository.save(new Role(null, Role.RoleType.ROLE_CLIENT));

            System.out.println("Роли инициализированы в базе данных.");
        }
    }
}