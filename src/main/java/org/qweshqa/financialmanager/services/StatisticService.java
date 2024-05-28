package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Expense;
import org.qweshqa.financialmanager.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StatisticService {
    private final ExpenseRepository expenseRepository;

    @Autowired
    public StatisticService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public BigDecimal getGeneralSpendingTotal(){
        BigDecimal generalSpendingTotal = BigDecimal.ZERO;

        List<Expense> expenseList = expenseRepository.findAll();

        for (Expense expense : expenseList){
            generalSpendingTotal = generalSpendingTotal.add(expense.getAmount());
        }

        return generalSpendingTotal;
    }
}
