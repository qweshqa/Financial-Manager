package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Finance;
import org.qweshqa.financialmanager.repositories.FinanceRepository;
import org.qweshqa.financialmanager.utils.FinanceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
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

    public Optional<Finance> findById(int id){
        return financeRepository.findById(id);
    }

    public List<Finance> findAll(){
        return financeRepository.findAll();
    }

    public List<Finance> findAllByType(FinanceType type){
        return financeRepository.findAllByType(type);
    }

    public List<Finance> findAllByDateAndType(LocalDate date, FinanceType type){
        return financeRepository.findAllByDateAndType(date, type);
    }

    public List<Finance> findAllByMonthAndType(Month month, FinanceType financeType){
        return financeRepository.findAllByMonthAndType(month, financeType);
    }

    public List<Finance> findAllByWeekAndType(LocalDate date, FinanceType financeType){
        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);

        List<Finance> finances = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            List<Finance> financesInDay = financeRepository.findAllByDateAndType(startOfWeek.plusDays(i), financeType);
            finances.addAll(financesInDay);
        }

        return finances;
    }

    public BigDecimal getDailyFinanceTotalByType(FinanceType type){
        List<Finance> finances = financeRepository.findAllByDateAndType(LocalDate.now(), type);

        return finances.stream().map(Finance::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getWeeklyFinanceTotalByType(FinanceType type){
        LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);

        List<Finance> finances = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            List<Finance> financesInDay = financeRepository.findAllByDateAndType(startOfWeek.plusDays(i), type);
            finances.addAll(financesInDay);
        }

        return finances.stream().map(Finance::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getMonthlyFinanceTotalByType(FinanceType type){
        List<Finance> finances = financeRepository.findAllByMonthAndType(LocalDate.now().getMonth(), type);

        return finances.stream().map(Finance::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getAllTimeFinanceTotalByType(FinanceType type){
        List<Finance> finances = financeRepository.findAllByType(type);

        return finances.stream().map(Finance::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Optional<Finance> findBiggestExpenseOrIncome(FinanceType type){
        return financeRepository.findAllByType(type).stream()
                .max(Comparator.comparing(Finance::getAmount));
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
