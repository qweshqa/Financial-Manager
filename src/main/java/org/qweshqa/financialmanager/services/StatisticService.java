package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Spending;
import org.qweshqa.financialmanager.repositories.SpendingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StatisticService {
    private final SpendingRepository spendingRepository;

    @Autowired
    public StatisticService(SpendingRepository spendingRepository) {
        this.spendingRepository = spendingRepository;
    }

    public BigDecimal getGeneralSpendingTotal(){
        BigDecimal generalSpendingTotal = BigDecimal.ZERO;

        List<Spending> spendingList = spendingRepository.findAll();

        for (Spending spending: spendingList){
            generalSpendingTotal = generalSpendingTotal.add(spending.getAmount());
        }

        return generalSpendingTotal;
    }
}
