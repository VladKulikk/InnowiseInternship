package com.innowise.internship.authentificationservice.repository;

import com.innowise.internship.authentificationservice.model.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {
    Optional<UserCredentials> findByLogin(String login);
}
