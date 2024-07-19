package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.OperationRepository;
import org.qweshqa.financialmanager.utils.enums.CategoryType;
import org.qweshqa.financialmanager.utils.exceptions.OperationNotFoundException;
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
public class OperationService {

    private final OperationRepository operationRepository;

    @Autowired
    public OperationService(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    public Operation findById(int id){
        Optional<Operation> operation = operationRepository.findById(id);

        if(operation.isEmpty()){
            throw new OperationNotFoundException("Operation with id " + id + " doesn't exist.");
        }

        return operation.get();
    }

    public List<Operation> findAllByUser(User user){
        return operationRepository.findAllByUser(user);
    }

    public List<Operation> findAllByCategory(Category category, User user){
        return operationRepository.findAllByCategoryAndUser(category, user);
    }

    public List<Operation> findAllByDateAndUser(LocalDate date, User user){
        return operationRepository.findAllByDateAndUser(date, user);
    }

    public List<Operation> findAllByWeekAndUser(LocalDate date, User user){
        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);

        List<Operation> operations = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            List<Operation> financesInDay = operationRepository.findAllByDateAndUser(startOfWeek.plusDays(i), user);
            operations.addAll(financesInDay);
        }

        return operations;
    }

    public List<Operation> findAllByMonthAndUser(Month month, User user){
        return operationRepository.findAllByMonthAndUser(month.getValue(), user);
    }

    public List<Operation> findAllByYearAndUser(int year, User user){
        return operationRepository.findAllByYearAndUser(year, user);
    }

    @Transactional
    public void processOperationSetup(Operation operation){
        if(operation.getComment().isEmpty()){
            operation.setComment("No comment.");
        }
    }

    @Transactional
    public void save(Operation operation){
        operationRepository.save(operation);
        operation.getInvolvedAccount().addAccountLinkedFinance(operation);
    }

    @Transactional
    public void update(int operationToUpdateId, Operation updatedOperation){
        updatedOperation.setId(operationToUpdateId);
        operationRepository.save(updatedOperation);
    }

    @Transactional
    public void delete(int id){
        operationRepository.deleteById(id);
    }
}
