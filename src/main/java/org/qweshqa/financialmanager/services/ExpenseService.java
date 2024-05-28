package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Expense;
import org.qweshqa.financialmanager.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> index(){
        return expenseRepository.findAll();
    }

    public Optional<Expense> findBiggestExpense(){
        List<Expense> expenseList = expenseRepository.findAll();

        return expenseList.stream().max(Comparator.comparing(Expense::getAmount));
    }

    public List<Expense> index(LocalDate date){
        return expenseRepository.findAllByDate(date);
    }

    public List<Expense> index(Month month){
        return expenseRepository.findAllByMonth(month);
    }

    public BigDecimal getExpensesTotalByDate(LocalDate date){
        BigDecimal spending_total = BigDecimal.ZERO;
        List<Expense> expenseList = expenseRepository.findAllByDate(date);

        for(Expense expense : expenseList){
            spending_total =  spending_total.add(expense.getAmount());
        }

        return spending_total;
    }

    @Transactional
    public void save(Expense expense){
        expenseRepository.save(expense);
    }

    @Transactional
    public void delete(int id){
        expenseRepository.deleteById(id);
    }
}
