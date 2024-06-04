package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Finance;
import org.qweshqa.financialmanager.repositories.FinanceRepository;
import org.qweshqa.financialmanager.utils.FinanceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StatisticService {

    private final FinanceRepository financeRepository;

    @Autowired
    public StatisticService(FinanceRepository financeRepository) {
        this.financeRepository = financeRepository;
    }

    public BigDecimal getGeneralSpendingTotal(){
        BigDecimal generalSpendingTotal = BigDecimal.ZERO;

        List<Finance> expenseList = financeRepository.findAllByType(FinanceType.EXPENSE);

        for (Finance expense : expenseList){
            generalSpendingTotal = generalSpendingTotal.add(expense.getAmount());
        }

        return generalSpendingTotal;
    }

    public BigDecimal getGeneralIncomeTotal(){
        BigDecimal generalIncomeTotal = BigDecimal.ZERO;

        List<Finance> incomeList = financeRepository.findAllByType(FinanceType.INCOME);

        for (Finance income : incomeList){
            generalIncomeTotal = generalIncomeTotal.add(income.getAmount());
        }

        return generalIncomeTotal;
    }
}
