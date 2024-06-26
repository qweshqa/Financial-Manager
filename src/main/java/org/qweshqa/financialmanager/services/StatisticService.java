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

    public float getGeneralSpendingTotal(){
        List<Finance> expenseList = financeRepository.findAllByType(FinanceType.EXPENSE);

        return (float) expenseList.stream().mapToDouble(Finance::getAmount).sum();
    }

    public float getGeneralIncomeTotal(){
        List<Finance> incomeList = financeRepository.findAllByType(FinanceType.INCOME);

        return (float) incomeList.stream().mapToDouble(Finance::getAmount).sum();
    }
}
