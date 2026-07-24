package com.bankapp.repository;


import com.bankapp.entity.Card;
import com.bankapp.interfaces.CardProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<CardProjection> findProjectionByCardId(Long id);
}
