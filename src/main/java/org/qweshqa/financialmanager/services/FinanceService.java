package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.Finance;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.FinanceRepository;
import org.qweshqa.financialmanager.utils.enums.FinanceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
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

    public List<Finance> findAllByCategory(Category category, User user){
        return financeRepository.findAllByCategoryAndUser(category, user);
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
    public void processFinanceSetup(Finance finance, User user, FinanceType type){

        finance.setUser(user);
        finance.setType(type);
        if(finance.getComment().isEmpty()){
            finance.setComment("No comment.");
        }

        if(finance.getType() == FinanceType.INCOME){
            finance.getInvolvedAccount().plusBalance(finance.getAmount());
        }
        else finance.getInvolvedAccount().minusBalance(finance.getAmount());

    }

    @Transactional
    public void save(Finance finance){
        financeRepository.save(finance);
        finance.getInvolvedAccount().addAccountLinkedFinance(finance);
    }

    @Transactional
    public void processFinanceEdit(Finance finance, Finance financeToUpdate){

        if(finance.getType() == FinanceType.EXPENSE){
            finance.getInvolvedAccount().plusBalance(financeToUpdate.getAmount());
            finance.getInvolvedAccount().minusBalance(finance.getAmount());
        }
        else {
            financeToUpdate.getInvolvedAccount().plusBalance(finance.getAmount());
            financeToUpdate.getInvolvedAccount().minusBalance(financeToUpdate.getAmount());
        }
    }

    @Transactional
    public void update(int financeToUpdateId, Finance updatedFinance){
        updatedFinance.setId(financeToUpdateId);
        financeRepository.save(updatedFinance);
    }

    @Transactional
    public void processFinanceDelete(Finance finance){

        if(finance.getType() == FinanceType.INCOME){
            finance.getInvolvedAccount().minusBalance(finance.getAmount());
        }
        else finance.getInvolvedAccount().plusBalance(finance.getAmount());

    }

    @Transactional
    public void delete(int id){
        financeRepository.deleteById(id);
    }
}
