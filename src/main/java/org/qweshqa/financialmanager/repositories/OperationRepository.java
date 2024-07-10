package org.qweshqa.financialmanager.repositories;

import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.utils.enums.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Integer> {
    List<Operation> findAllByTypeAndUser(OperationType type, User user);

    List<Operation> findAllByCategoryAndUser(Category category, User user);

    List<Operation> findAllByDateAndTypeAndUser(LocalDate date, OperationType type, User user);

    List<Operation> findAllByMonthAndTypeAndUser(Month month, OperationType type, User user);
}
