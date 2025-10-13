package com.example.ecpro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ecpro.model.PasswordResetToken;

@Repository
public interface PassResetTokRepo extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByToken(String token);
    List<PasswordResetToken> findByUserId(int i);
}
