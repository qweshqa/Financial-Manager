package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Finance;
import org.qweshqa.financialmanager.repositories.FinanceRepository;
import org.qweshqa.financialmanager.utils.FinanceType;
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
public class FinanceService {

    private final FinanceRepository financeRepository;

    @Autowired
    public FinanceService(FinanceRepository financeRepository) {
        this.financeRepository = financeRepository;
    }

    public List<Finance> findAll(){
        return financeRepository.findAll();
    }

    public List<Finance> findAllByType(FinanceType type){
        return financeRepository.findAllByType(type);
    }

    public List<Finance> findAllByDate(LocalDate date){
        return financeRepository.findAllByDate(date);
    }

    public List<Finance> findAllByMonth(Month month){
        return financeRepository.findAllByMonth(month);
    }

    public Optional<Finance> findBiggestExpenseOrIncome(FinanceType type){
        return financeRepository.findAllByType(type).stream()
                .max(Comparator.comparing(Finance::getAmount));
    }

    public BigDecimal getFinanceAmountTotalByDate(LocalDate date){
        return financeRepository.findAllByDateAndType(date, FinanceType.INCOME)
                .stream().map(Finance::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)

                .subtract(financeRepository.findAllByDateAndType(date, FinanceType.EXPENSE)
                        .stream().map(Finance::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
    }



    @Transactional
    public void save(Finance finance){
        financeRepository.save(finance);
    }

    @Transactional
    public void delete(int id){
        financeRepository.deleteById(id);
    }
}
