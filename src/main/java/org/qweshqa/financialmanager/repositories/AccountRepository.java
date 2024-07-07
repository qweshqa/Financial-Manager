package org.qweshqa.financialmanager.repositories;

import org.qweshqa.financialmanager.models.Account;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.utils.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findAllByOwner(User owner);

    List<Account> findAllByTypeAndOwner(AccountType type, User owner);
}
