package com.bankapp.repository;

import com.bankapp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("select max(a.accountNumber) from Account a")
    Integer findMaxAccountNumber();
}
