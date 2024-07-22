package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.OperationRepository;
import org.qweshqa.financialmanager.utils.DateWrapper;
import org.qweshqa.financialmanager.utils.exceptions.OperationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
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

    public void configureStringDateValues(String year, String month, String day, String period, DateWrapper dateWrapper){
        switch(period){
            case "year":
                if(year.isBlank()){
                    return;
                }

                if (Integer.parseInt(year) < 0) {
                    throw new DateTimeException("Year period error");
                }

            case "month":
                if(month.isBlank()){
                    return;
                }

                if (Integer.parseInt(year) < 0) {
                    throw new DateTimeException("Month period error");
                }

                int yearValueForMonth = year.isBlank() ? dateWrapper.getDate().getYear() : Integer.parseInt(year);
                if(Byte.parseByte(month) > 12){
                    dateWrapper.setDate(dateWrapper.getDate().withYear(yearValueForMonth + 1).withMonth(1));
                    throw new DateTimeException("Month period error");
                }
                if(Byte.parseByte(month) < 1){
                    if(Byte.parseByte(month) == 1 && yearValueForMonth == 0){
                        throw new DateTimeException("Day period error");
                    }
                    dateWrapper.setDate(dateWrapper.getDate().withYear(yearValueForMonth - 1).withMonth(12));
                    throw new DateTimeException("Month period error");
                }

            case "day":
                if(day.isBlank()){
                    return;
                }

                int yearValueForDay = year.isBlank()  ? dateWrapper.getDate().getYear() : Integer.parseInt(year);
                if (yearValueForDay < 0) {
                    throw new DateTimeException("Day period error");
                }

                int monthValue = month.isBlank() ? dateWrapper.getDate().getMonth().getValue() : Month.of(Byte.parseByte(month)).getValue();
                if(Byte.parseByte(month) > 12){
                    dateWrapper.setDate(dateWrapper.getDate().withYear(yearValueForDay + 1).withMonth(1).withDayOfMonth(1));
                    throw new DateTimeException("Day period error");
                }
                if(Byte.parseByte(month) < 1){
                    if(monthValue == 1 && yearValueForDay == 0){
                        throw new DateTimeException("Day period error");
                    }
                    dateWrapper.setDate(dateWrapper.getDate().withYear(yearValueForDay - 1).withMonth(12).withDayOfMonth(31));
                    throw new DateTimeException("Day period error");
                }


                if(Integer.parseInt(day) > Month.of(monthValue).maxLength()){
                    if(monthValue == 12){
                        dateWrapper.setDate(dateWrapper.getDate().withYear(yearValueForDay + 1).withMonth(1).withDayOfMonth(1));
                    }
                    else{
                        dateWrapper.setDate(dateWrapper.getDate().withMonth(monthValue + 1).withDayOfMonth(1));
                    }
                    throw new DateTimeException("Day period error");
                }
                else if(Integer.parseInt(day) < 1){
                    if(monthValue == 1 && yearValueForDay == 0){
                        throw new DateTimeException("Day period error");
                    }
                    if(monthValue == 1){
                        dateWrapper.setDate(dateWrapper.getDate().withYear(yearValueForDay - 1).withMonth(12).withDayOfMonth(31));
                    }
                    else{
                        dateWrapper.setDate(dateWrapper.getDate().withMonth(monthValue - 1).withDayOfMonth(Month.of(monthValue - 1).maxLength()));
                    }
                    throw new DateTimeException("Day period error");
                }
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
