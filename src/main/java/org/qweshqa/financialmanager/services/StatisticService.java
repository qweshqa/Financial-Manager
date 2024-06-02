package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Expense;
import org.qweshqa.financialmanager.models.Income;
import org.qweshqa.financialmanager.repositories.ExpenseRepository;
import org.qweshqa.financialmanager.repositories.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StatisticService {

    private final ExpenseRepository expenseRepository;

    private final IncomeRepository incomeRepository;

    @Autowired
    public StatisticService(ExpenseRepository expenseRepository, IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
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

    public BigDecimal getGeneralIncomeTotal(){
        BigDecimal generalIncomeTotal = BigDecimal.ZERO;

        List<Income> incomeList = incomeRepository.findAll();

        for (Income income : incomeList){
            generalIncomeTotal = generalIncomeTotal.add(income.getAmount());
        }

        return generalIncomeTotal;
    }
}
