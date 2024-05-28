package org.qweshqa.financialmanager.repositories;

import org.qweshqa.financialmanager.models.Spending;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public interface SpendingRepository extends JpaRepository<Spending, Integer> {
    List<Spending> findAllByDate(LocalDate date);

    List<Spending> findAllByMonth(Month month);
}
