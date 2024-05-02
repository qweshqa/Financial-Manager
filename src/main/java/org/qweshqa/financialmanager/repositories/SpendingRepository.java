package org.qweshqa.financialmanager.repositories;

import org.qweshqa.financialmanager.models.Spending;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpendingRepository extends JpaRepository<Spending, Integer> {
}
