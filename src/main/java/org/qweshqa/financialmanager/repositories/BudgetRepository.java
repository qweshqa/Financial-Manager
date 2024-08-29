package org.qweshqa.financialmanager.repositories;

import org.qweshqa.financialmanager.models.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    Optional<Budget> findByUuid(String uuid);

    List<Budget> findAllByUserId(int user_id);
}
