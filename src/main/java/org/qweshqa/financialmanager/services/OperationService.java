package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.OperationRepository;
import org.qweshqa.financialmanager.utils.exceptions.OperationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    public List<Operation> findAllByMonthAndUser(LocalDate dateWithMonth, User user){
        List<Operation> operations = new ArrayList<>();

        for(int i = 1; i <= dateWithMonth.getMonth().maxLength(); i++){
            operations.addAll(operationRepository.findAllByDateAndUser(dateWithMonth.withDayOfMonth(i), user));
        }
        return operations;
    }

    public List<Operation> findAllByYearAndUser(int year, User user){
        return operationRepository.findAllByYearAndUser(year, user);
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
