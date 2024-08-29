package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Budget;
import org.qweshqa.financialmanager.repositories.BudgetRepository;
import org.qweshqa.financialmanager.utils.exceptions.BudgetNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    private final TransactionTemplate transactionTemplate;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, TransactionTemplate transactionTemplate) {
        this.budgetRepository = budgetRepository;
        this.transactionTemplate = transactionTemplate;
    }

    public Budget findByUuid(String uuid){
        return transactionTemplate.execute(status -> {
            try {
                Optional<Budget> budget = budgetRepository.findByUuid(uuid);

                if(budget.isEmpty()){
                    throw new BudgetNotFoundException("Budget with this UUID doesn't exist");
                }
                return budget.get();

            } catch (Exception e){
                status.setRollbackOnly();
                throw e;
            }
        });

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

    public void update(Budget budget, Budget updatedBudget){
        updatedBudget.setUuid(budget.getUuid());

        transactionTemplate.execute(status -> {
           try{
               budgetRepository.delete(budget);

               return budgetRepository.save(updatedBudget);

           } catch (Exception e){
               status.setRollbackOnly();
               throw e;
           }
        });
    }
}
