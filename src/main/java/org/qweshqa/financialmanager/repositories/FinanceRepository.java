package org.qweshqa.financialmanager.repositories;

import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.Finance;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.utils.enums.FinanceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public interface FinanceRepository extends JpaRepository<Finance, Integer> {
    List<Finance> findAllByTypeAndUser(FinanceType type, User user);

    List<Finance> findAllByCategoryAndUser(Category category, User user);

    List<Finance> findAllByDateAndTypeAndUser(LocalDate date, FinanceType type, User user);

    List<Finance> findAllByMonthAndTypeAndUser(Month month, FinanceType type, User user);
}
