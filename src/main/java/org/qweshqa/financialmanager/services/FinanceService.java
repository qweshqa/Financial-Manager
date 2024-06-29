package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Finance;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.FinanceRepository;
import org.qweshqa.financialmanager.repositories.UserRepository;
import org.qweshqa.financialmanager.utils.FinanceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Finance> findAllByType(FinanceType type, User user){
        return financeRepository.findAllByTypeAndUser(type, user);
    }

    public List<Finance> findAllByDateAndType(LocalDate date, FinanceType type, User user){
        return financeRepository.findAllByDateAndTypeAndUser(date, type, user);
    }

    public List<Finance> findAllByMonthAndType(Month month, FinanceType financeType, User user){
        return financeRepository.findAllByMonthAndTypeAndUser(month, financeType, user);
    }

    public List<Finance> findAllByWeekAndType(LocalDate date, FinanceType financeType, User user){
        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);

        List<Finance> finances = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            List<Finance> financesInDay = financeRepository.findAllByDateAndTypeAndUser(startOfWeek.plusDays(i), financeType, user);
            finances.addAll(financesInDay);
        }

        return finances;
    }

    public float getDailyFinanceTotalByType(FinanceType type, User user){
        List<Finance> finances = financeRepository.findAllByDateAndTypeAndUser(LocalDate.now(), type, user);

        return (float) finances.stream().mapToDouble(Finance::getAmount).sum();
    }

    public float getWeeklyFinanceTotalByType(FinanceType type, User user){
        LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);

        List<Finance> finances = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            List<Finance> financesInDay = financeRepository.findAllByDateAndTypeAndUser(startOfWeek.plusDays(i), type, user);
            finances.addAll(financesInDay);
        }

        return (float) finances.stream().mapToDouble(Finance::getAmount).sum();
    }

    public float getMonthlyFinanceTotalByType(FinanceType type, User user){
        List<Finance> finances = financeRepository.findAllByMonthAndTypeAndUser(LocalDate.now().getMonth(), type, user);

        return (float) finances.stream().mapToDouble(Finance::getAmount).sum();
    }

    public float getAllTimeFinanceTotalByType(FinanceType type, User user){
        List<Finance> finances = financeRepository.findAllByTypeAndUser(type, user);

        return (float) finances.stream().mapToDouble(Finance::getAmount).sum();
    }

    @Transactional
    public void save(Finance finance){
        financeRepository.save(finance);
    }

    @Transactional
    public void update(Finance financeToUpdate, Finance updatedFinance){
        financeToUpdate.setName(updatedFinance.getName());
        financeToUpdate.setAmount(updatedFinance.getAmount());
        financeToUpdate.setComment(updatedFinance.getComment());
    }

    @Transactional
    public void delete(int id){
        financeRepository.deleteById(id);
    }
}
