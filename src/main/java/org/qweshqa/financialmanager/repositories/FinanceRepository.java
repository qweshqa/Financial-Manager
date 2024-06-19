package org.qweshqa.financialmanager.repositories;

import org.qweshqa.financialmanager.models.Finance;
import org.qweshqa.financialmanager.utils.FinanceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public interface FinanceRepository extends JpaRepository<Finance, Integer> {
    List<Finance> findAllByType(FinanceType type);

    List<Finance> findAllByDate(LocalDate date);

    List<Finance> findAllByDateAndType(LocalDate date, FinanceType type);

    List<Finance> findAllByMonthAndType(Month month, FinanceType type);
}
