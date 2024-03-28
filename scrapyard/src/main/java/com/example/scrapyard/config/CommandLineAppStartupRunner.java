package com.example.scrapyard.config;

import com.example.scrapyard.model.Role;
import com.example.scrapyard.model.UserEntity;
import com.example.scrapyard.repository.RoleRepository;
import com.example.scrapyard.repository.UserRepository;
import com.example.scrapyard.service.CustomUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
    final CustomUserDetailsServiceImpl userDetailsService;
    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final PasswordEncoder passwordEncoder;

    @Value(value = "${accounts.admin.login:testadmin}")
    String adminLogin;
    String adminRoleName = "ADMIN";

    @Value(value = "${accounts.admin.password:testadminsecret}")
    String adminPassword;
    String userRoleName = "USER";

    public CommandLineAppStartupRunner(CustomUserDetailsServiceImpl userDetailsService, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        addRoles();
    }

    private void addRoles(){
        boolean userRoleExists = roleRepository.findByName(userRoleName).isPresent();
        boolean adminRoleExisists = roleRepository.findByName(adminRoleName).isPresent();

        Role userRole;
        Role adminRole;

        if (!adminRoleExisists)
            adminRole = roleRepository.save(Role.builder().name(adminRoleName).build());
        else
            adminRole = roleRepository.findByName(adminRoleName).get();

        if (!userRoleExists)
            userRole = roleRepository.save(Role.builder().name(userRoleName).build());
        else
            userRole = roleRepository.findByName(userRoleName).get();

        if (userRepository.findByUsername(adminLogin).isEmpty()) {
            List<Role> roles = Arrays.asList(userRole, adminRole);
            userRepository.save(UserEntity.builder()
                    .username(adminLogin)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .roles(roles).build());
        }
    }
}