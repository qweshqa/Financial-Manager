package org.qweshqa.financialmanager.repositories;

import org.qweshqa.financialmanager.models.Account;
import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Integer> {
    List<Operation> findAllByUser(User user);

    List<Operation> findAllByCategoryAndUser(Category category, User user);

    List<Operation> findAllByDateAndUser(LocalDate date, User user);

    List<Operation> findAllByMonthAndUser(Month month, User user);

    List<Operation> findAllByInvolvedAccountAndUser(Account involvedAccount, User user);
}
