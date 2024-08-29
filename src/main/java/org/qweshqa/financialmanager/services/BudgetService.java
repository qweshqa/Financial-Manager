package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Budget;
import org.qweshqa.financialmanager.repositories.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    private final TransactionTemplate transactionTemplate;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, TransactionTemplate transactionTemplate) {
        this.budgetRepository = budgetRepository;
        this.transactionTemplate = transactionTemplate;
    }

    public List<Budget> findAllByUserId(int user_id){
        return transactionTemplate.execute(status -> {
            try {
                return budgetRepository.findAllByUserId(user_id);
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }

    public void save(Budget budget){
        transactionTemplate.execute(status -> {
            try {
                return budgetRepository.save(budget);
            } catch (Exception e){
                status.setRollbackOnly();
                throw e;
            }
        });

    }
}
