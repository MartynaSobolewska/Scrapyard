package com.scrapyard.authservice.service.user;

import com.scrapyard.authservice.DAOs.RoleRepository;
import com.scrapyard.authservice.DAOs.UserRepository;
import com.scrapyard.authservice.DAOs.models.Role;
import com.scrapyard.authservice.DAOs.models.UserEntity;
import com.scrapyard.authservice.api.DTOs.RegisterDTO;
import com.scrapyard.authservice.api.exceptions.UsernameExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomUserDetailsServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity registerNewUserAccount(RegisterDTO accountDto) throws UsernameExistsException {

        if (userRepository.findByUsername(accountDto.getUsername()).isEmpty()){
            Optional<Role> presentRole = roleRepository.findByName("USER");
            if (presentRole.isEmpty()){
                roleRepository.save(Role.builder().name("USER").build());
            }
            presentRole = roleRepository.findByName("USER");
            Role userRole = presentRole.get();

            UserEntity newUserEntity = UserEntity.builder()
                    .username(accountDto.getUsername())
                    .passwordHash(passwordEncoder.encode(accountDto.getPassword()))
                    .roles(Collections.singletonList(userRole))
                    .build();
            return userRepository.save(newUserEntity);
        }
        throw UsernameExistsException.createWith(accountDto.getUsername());
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        return new User(user.getUsername(), user.getPasswordHash(), mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(List<Role> roles){
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}