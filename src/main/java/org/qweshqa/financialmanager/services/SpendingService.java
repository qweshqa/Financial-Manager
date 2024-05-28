package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Spending;
import org.qweshqa.financialmanager.repositories.SpendingRepository;
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
public class SpendingService {

    private final SpendingRepository spendingRepository;

    @Autowired
    public SpendingService(SpendingRepository spendingRepository) {
        this.spendingRepository = spendingRepository;
    }

    public List<Spending> index(){
        return spendingRepository.findAll();
    }

    public Optional<Spending> findBiggestExpense(){
        List<Spending> spendingList = spendingRepository.findAll();

        return spendingList.stream().max(Comparator.comparing(Spending::getAmount));
    }

    public List<Spending> index(LocalDate date){
        return spendingRepository.findAllByDate(date);
    }

    public List<Spending> index(Month month){
        return spendingRepository.findAllByMonth(month);
    }

    public BigDecimal getSpendingTotalByDate(LocalDate date){
        BigDecimal spending_total = BigDecimal.ZERO;
        List<Spending> spendingList = spendingRepository.findAllByDate(date);

        for(Spending spending: spendingList){
            spending_total =  spending_total.add(spending.getAmount());
        }

        return spending_total;
    }

    @Transactional
    public void save(Spending spending){
        spendingRepository.save(spending);
    }

    @Transactional
    public void delete(int id){
        spendingRepository.deleteById(id);
    }
}
