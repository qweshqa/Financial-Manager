package org.qweshqa.financialmanager.repositories;

import org.qweshqa.financialmanager.models.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Integer> {
    List<Income> findAllByDate(LocalDate localDate);
}
