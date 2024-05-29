package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Income;
import org.qweshqa.financialmanager.repositories.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class IncomeService {
    private final IncomeRepository incomeRepository;

    @Autowired
    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public List<Income> index(){
        return incomeRepository.findAll();
    }

    public List<Income> index(LocalDate date){
        return incomeRepository.findAllByDate(date);
    }

    public BigDecimal getIncomeTotalByDate(LocalDate date){
        BigDecimal earning_total = BigDecimal.ZERO;
        List<Income> incomeList = incomeRepository.findAllByDate(date);

        for(Income income : incomeList){
            earning_total =  earning_total.add(income.getAmount());
        }

        return earning_total;
    }

    public void save(Income income){
        incomeRepository.save(income);
    }

    public void delete(int id){
        incomeRepository.deleteById(id);
    }
}
