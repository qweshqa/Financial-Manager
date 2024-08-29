package org.qweshqa.financialmanager.repositories;

import org.qweshqa.financialmanager.models.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    List<Budget> findAllByUserId(int user_id);
}
