package com.tota.ecommercebackend.model.dao;

import com.tota.ecommercebackend.model.LocalUser;
import com.tota.ecommercebackend.model.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationTokenDAO extends ListCrudRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    void deleteByUser(LocalUser user);
    List<VerificationToken> findByUser_IdOrderByIdDesc(long id);
}

