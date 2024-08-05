package org.qweshqa.financialmanager.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.qweshqa.financialmanager.models.Account;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.OperationRepository;
import org.qweshqa.financialmanager.utils.DateWrapper;
import org.qweshqa.financialmanager.utils.RequestSender;
import org.qweshqa.financialmanager.utils.enums.CategoryType;
import org.qweshqa.financialmanager.utils.exceptions.OperationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.http.HttpResponse;
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
    private final RequestSender requestSender;

    @Autowired
    public OperationService(OperationRepository operationRepository, RequestSender requestSender) {
        this.operationRepository = operationRepository;
        this.requestSender = requestSender;
    }

    public Operation findById(int id){
        Optional<Operation> operation = operationRepository.findById(id);

        if(operation.isEmpty()){
            throw new OperationNotFoundException("Operation with id " + id + " doesn't exist.");
        }

        return operation.get();
    }

    public List<Operation> findAll(){
        return operationRepository.findAll();
    }


    public List<Operation> findAllByUser(User user){
        return operationRepository.findAllByUser(user);
    }

    public List<Operation> findAllByUserAndCategoryType(User user, CategoryType type){
        return operationRepository.findAllByUserAndCategoryType(user, type);
    }


    public List<Operation> findAllByUserAndDate(User user, LocalDate date){
        return operationRepository.findAllByDateAndUser(date, user);
    }

    public List<Operation> findAllByUserAndDateAndCategoryType(User user, LocalDate date, CategoryType type){
        return operationRepository.findAllByDateAndUserAndCategoryType(date, user, type);
    }


    public List<Operation> findAllByUserAndMonth(User user, LocalDate dateWithMonth){
        List<Operation> operations = new ArrayList<>();

        for(int i = 1; i <= dateWithMonth.getMonth().maxLength(); i++){
            operations.addAll(operationRepository.findAllByDateAndUser(dateWithMonth.withDayOfMonth(i), user));
        }
        return operations;
    }

    public List<Operation> findAllByUserAndYearAndMonthAndCategoryType(User user, int year, int month, CategoryType type){
        return operationRepository.findAllByYearAndMonthAndUserAndCategoryType(year, month, user, type);
    }


    public List<Operation> findAllByUserAndYear(User user, int year){
        return operationRepository.findAllByYearAndUser(year, user);
    }

    public List<Operation> findAllByUserAndYearAndCategoryType(User user, int year, CategoryType type){
        return operationRepository.findAllByYearAndUserAndCategoryType(year, user, type);
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
    public void prepareForSave(Operation operation) {
        Account account = operation.getInvolvedAccount();
        HttpResponse<String> response = requestSender.sendCurrencyConvertRequest(account.getCurrency(), operation.getUser().getSetting().getCurrencyUnit());

        if(response.statusCode() == 200){
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = null;

            try{
                node = objectMapper.readTree(response.body());
            } catch (JsonProcessingException e){
                e.printStackTrace();
            }

            float currencyValue = (float) node.get("data").get(operation.getUser().getSetting().getCurrencyUnit()).asDouble();

            if(operation.getCategory().getCategoryType() == CategoryType.EXPENSE){
                account.setBalance(account.getBalance() - ( operation.getAmount() * currencyValue));
            }
            else{
                account.setBalance(account.getBalance() + ( operation.getAmount() * currencyValue));
            }
        }
    }

    @Transactional
    public void save(Operation operation){
        operationRepository.save(operation);
    }

    @Transactional
    public void prepareForUpdate(Operation operationToUpdate, Operation updatedOperation) {
        Account account = updatedOperation.getInvolvedAccount();
        HttpResponse<String> response = requestSender.sendCurrencyConvertRequest(account.getCurrency(), operationToUpdate.getUser().getSetting().getCurrencyUnit());

        if(response.statusCode() == 200){
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = null;

            try{
                node = objectMapper.readTree(response.body());
            } catch (JsonProcessingException e){
                e.printStackTrace();
            }

            float currencyValue = (float) node.get("data").get(operationToUpdate.getUser().getSetting().getCurrencyUnit()).asDouble();

            if(updatedOperation.getCategory().getCategoryType() == CategoryType.INCOME){
                account.setBalance(account.getBalance() - ( operationToUpdate.getAmount() * currencyValue));
                account.setBalance(account.getBalance() + ( updatedOperation.getAmount() * currencyValue));
            }
            else{
                account.setBalance(account.getBalance() + ( operationToUpdate.getAmount() * currencyValue));
                account.setBalance(account.getBalance() - ( updatedOperation.getAmount() * currencyValue));
            }
        }
    }

    @Transactional
    public void update(Operation operationToUpdate, Operation updatedOperation){
        updatedOperation.setId(operationToUpdate.getId());

        operationRepository.save(updatedOperation);
    }

    @Transactional
    public void prepareForDelete(Operation operation) {
        Account account = operation.getInvolvedAccount();
        HttpResponse<String> response = requestSender.sendCurrencyConvertRequest(account.getCurrency(), operation.getUser().getSetting().getCurrencyUnit());

        if(response.statusCode() == 200){
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = null;

            try{
                node = objectMapper.readTree(response.body());
            } catch (JsonProcessingException e){
                e.printStackTrace();
            }

            float currencyValue = (float) node.get("data").get(operation.getUser().getSetting().getCurrencyUnit()).asDouble();

            if(operation.getCategory().getCategoryType() == CategoryType.INCOME){
                account.setBalance(account.getBalance() - ( operation.getAmount() * currencyValue));
            }
            else{
                account.setBalance(account.getBalance() + ( operation.getAmount() * currencyValue));
            }
        }
    }

    @Transactional
    public void delete(Operation operation){
        operationRepository.deleteById(operation.getId());
    }
}
