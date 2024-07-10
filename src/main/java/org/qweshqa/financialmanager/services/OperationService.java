package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.OperationRepository;
import org.qweshqa.financialmanager.utils.enums.CategoryType;
import org.qweshqa.financialmanager.utils.enums.OperationType;
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

    public Optional<Operation> findById(int id){
        return operationRepository.findById(id);
    }

    public List<Operation> findAll(){
        return operationRepository.findAll();
    }

    public List<Operation> findAllByType(OperationType type, User user){
        return operationRepository.findAllByTypeAndUser(type, user);
    }

    public List<Operation> findAllByCategory(Category category, User user){
        return operationRepository.findAllByCategoryAndUser(category, user);
    }

    public List<Operation> findAllByDateAndType(LocalDate date, OperationType type, User user){
        return operationRepository.findAllByDateAndTypeAndUser(date, type, user);
    }

    public List<Operation> findAllByMonthAndType(Month month, OperationType operationType, User user){
        return operationRepository.findAllByMonthAndTypeAndUser(month, operationType, user);
    }

    public List<Operation> findAllByWeekAndType(LocalDate date, OperationType operationType, User user){
        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);

        List<Operation> operations = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            List<Operation> financesInDay = operationRepository.findAllByDateAndTypeAndUser(startOfWeek.plusDays(i), operationType, user);
            operations.addAll(financesInDay);
        }

        return operations;
    }

    public float getDailyOperationTotalByType(OperationType type, User user){
        List<Operation> operations = operationRepository.findAllByDateAndTypeAndUser(LocalDate.now(), type, user);

        return (float) operations.stream().mapToDouble(Operation::getAmount).sum();
    }

    public float getWeeklyOperationTotalByType(OperationType type, User user){
        LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);

        List<Operation> operations = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            List<Operation> financesInDay = operationRepository.findAllByDateAndTypeAndUser(startOfWeek.plusDays(i), type, user);
            operations.addAll(financesInDay);
        }

        return (float) operations.stream().mapToDouble(Operation::getAmount).sum();
    }

    public float getMonthlyOperationTotalByType(OperationType type, User user){
        List<Operation> operations = operationRepository.findAllByMonthAndTypeAndUser(LocalDate.now().getMonth(), type, user);

        return (float) operations.stream().mapToDouble(Operation::getAmount).sum();
    }

    public float getAllTimeOperationTotalByType(OperationType type, User user){
        List<Operation> operations = operationRepository.findAllByTypeAndUser(type, user);

        return (float) operations.stream().mapToDouble(Operation::getAmount).sum();
    }

    @Transactional
    public void processOperationSetup(Operation operation, User user, OperationType type){

        operation.setUser(user);
        operation.setType(type);
        if(operation.getComment().isEmpty()){
            operation.setComment("No comment.");
        }

        if(operation.getCategory().getCategoryType() == CategoryType.INCOME){
            operation.getInvolvedAccount().plusBalance(operation.getAmount());
            operation.getCategory().plusBalance(operation.getAmount());
        }
        else{
            operation.getInvolvedAccount().minusBalance(operation.getAmount());
            operation.getCategory().minusBalance(operation.getAmount());
        }

    }

    @Transactional
    public void save(Operation operation){
        operationRepository.save(operation);
        operation.getInvolvedAccount().addAccountLinkedFinance(operation);
    }

    @Transactional
    public void processOperationEdit(Operation operation, Operation operationToUpdate){

        if(operation.getCategory().getCategoryType() == CategoryType.EXPENSE){
            operation.getInvolvedAccount().plusBalance(operationToUpdate.getAmount());
            operation.getInvolvedAccount().minusBalance(operation.getAmount());

            operation.getCategory().plusBalance(operationToUpdate.getAmount());
            operation.getCategory().minusBalance(operation.getAmount());
        }
        else {
            operationToUpdate.getInvolvedAccount().plusBalance(operation.getAmount());
            operationToUpdate.getInvolvedAccount().minusBalance(operationToUpdate.getAmount());

            operation.getCategory().plusBalance(operation.getAmount());
            operation.getCategory().minusBalance(operationToUpdate.getAmount());
        }
    }

    @Transactional
    public void update(int operationToUpdateId, Operation updatedOperation){
        updatedOperation.setId(operationToUpdateId);
        operationRepository.save(updatedOperation);
    }

    @Transactional
    public void processOperationDelete(Operation operation){

        if(operation.getType() == OperationType.INCOME){
            operation.getInvolvedAccount().minusBalance(operation.getAmount());
            operation.getCategory().minusBalance(operation.getAmount());
        }
        else{
            operation.getInvolvedAccount().plusBalance(operation.getAmount());
            operation.getCategory().plusBalance(operation.getAmount());
        }

    }

    @Transactional
    public void delete(int id){
        operationRepository.deleteById(id);
    }
}
