package com.bankapp.repository;

import com.bankapp.entity.Transaction;
import com.bankapp.interfaces.TransactionProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<TransactionProjection> findAllBy(Pageable pageable);

}
