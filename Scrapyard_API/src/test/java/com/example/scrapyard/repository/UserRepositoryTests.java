package com.example.scrapyard.repository;

import com.example.scrapyard.model.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTests {
    UserEntity userEntity1 = UserEntity.builder()
            .username("aaa123@a.com.uk")
            .passwordHash("A123")
            .build();

    UserEntity userEntity2 = UserEntity.builder()
            .username("bbb123@a.com.uk")
            .passwordHash("B123")
            .build();

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void givenAUser_whenSave_repoHasUser(){
        UUID savedId = userRepository.save(userEntity1).getId();
        UserEntity savedEntity = userRepository.getReferenceById(savedId);
        Assertions.assertThat(savedEntity.getUsername()).isEqualTo(userEntity1.getUsername());
        Assertions.assertThat(savedEntity.getPasswordHash()).isEqualTo(userEntity1.getPasswordHash());
    }

    @Test
    void givenAUser_whenGetByUsername_repoReturnsUser(){
        userRepository.save(userEntity1);
        Optional<UserEntity> foundByUsername = userRepository.findByUsername(userEntity1.getUsername());
        Assertions.assertThat(foundByUsername).isNotEmpty();
        Assertions.assertThat(foundByUsername.get().getUsername()).isEqualTo(userEntity1.getUsername());
        Assertions.assertThat(foundByUsername.get().getId()).isEqualTo(userEntity1.getId());
        Assertions.assertThat(foundByUsername.get().getPasswordHash()).isEqualTo(userEntity1.getPasswordHash());
    }


}

