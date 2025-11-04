package com.bankapp.repository;

import com.bankapp.entity.User;
import com.bankapp.interfaces.UserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Page<UserProjection> findAllBy(Pageable pageable);

    Optional<User> findByEmail(String email);
}
