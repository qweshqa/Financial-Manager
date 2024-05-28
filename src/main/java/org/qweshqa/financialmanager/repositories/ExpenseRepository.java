package org.qweshqa.financialmanager.repositories;

import org.qweshqa.financialmanager.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findAllByDate(LocalDate date);

    List<Expense> findAllByMonth(Month month);
}
