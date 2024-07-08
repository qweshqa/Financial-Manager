package org.qweshqa.financialmanager.repositories;

import org.qweshqa.financialmanager.models.Account;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.utils.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findAllByOwner(User owner);

    List<Account> findAllByOwnerAndArchived(User owner, boolean isArchived);

    List<Account> findAllByTypeAndOwnerAndArchived(AccountType type, User owner, boolean isArchived);
}
