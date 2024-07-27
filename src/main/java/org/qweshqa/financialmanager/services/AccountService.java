package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Account;
import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.AccountRepository;
import org.qweshqa.financialmanager.repositories.OperationRepository;
import org.qweshqa.financialmanager.utils.enums.AccountType;
import org.qweshqa.financialmanager.utils.enums.CategoryType;
import org.qweshqa.financialmanager.utils.exceptions.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;

    private final OperationRepository operationRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    public Account findById(int id){
        Optional<Account> account = accountRepository.findById(id);

        if(account.isEmpty()){
            throw new AccountNotFoundException("Account with id " + id + " doesn't exist.");
        }

        return account.get();
    }


    public List<Account> findAllByUser(User user){
        return accountRepository.findAllByOwner(user);
    }

    public List<Account> findAllByUserAndArchive(User user, boolean isArchive){
        return accountRepository.findAllByOwnerAndArchived(user, isArchive);
    }

    public List<Account> findAllByUserAndArchiveAndType(User user, boolean isArchived, AccountType accountType){
        return accountRepository.findAllByOwnerAndArchivedAndType(user, isArchived, accountType);
    }


    public List<Operation> findAllOperationsByUser(Account account, User user){
        return operationRepository.findAllByInvolvedAccountAndUser(account, user);
    }

    public List<Operation> findAllOperationsByUserAndDate(Account account, User user, LocalDate date){
        return operationRepository.findAllByDateAndUserAndInvolvedAccount(date, user, account);
    }

    public List<Operation> findAllOperationsByUserAndMonth(Account account, User user, LocalDate dateWithMonth){
        List<Operation> operations = new ArrayList<>();

        for(int i = 1; i <= dateWithMonth.getMonth().maxLength(); i++){
            operations.addAll(operationRepository.findAllByDateAndUserAndInvolvedAccount(dateWithMonth.withDayOfMonth(i), user, account));
        }
        return operations;
    }

    public List<Operation> findAllOperationsByUserAndYear(Account account, User user, int year){
        return operationRepository.findAllByYearAndUserAndInvolvedAccount(year, user, account);
    }


    @Transactional
    public List<Account> createAccountsSetsByDefault(User owner){
        List<Account> accounts = new ArrayList<>();

        Account cashFinancialAccount = new Account();
        cashFinancialAccount.setOwner(owner);
        cashFinancialAccount.setType(AccountType.FINANCIAL);
        cashFinancialAccount.setName("Cash");
        cashFinancialAccount.setDescription("");

        accounts.add(cashFinancialAccount);
        accountRepository.save(cashFinancialAccount);

        Account cardFinancialAccount = new Account();
        cardFinancialAccount.setOwner(owner);
        cardFinancialAccount.setType(AccountType.FINANCIAL);
        cardFinancialAccount.setName("Card");
        cardFinancialAccount.setDescription("");

        accounts.add(cardFinancialAccount);
        accountRepository.save(cardFinancialAccount);

        Account savingsAccount = new Account();
        savingsAccount.setOwner(owner);
        savingsAccount.setType(AccountType.SAVINGS);
        savingsAccount.setName("For a dream");
        savingsAccount.setDescription("");

        accounts.add(savingsAccount);
        accountRepository.save(savingsAccount);

        return accounts;
    }

    @Transactional
    public void update(int accountIdToUpdate, Account updatedAccount){
        updatedAccount.setId(accountIdToUpdate);
        accountRepository.save(updatedAccount);
    }

    @Transactional
    public void save(Account account){
        accountRepository.save(account);
    }

    @Transactional
    public void delete(Account account){
        accountRepository.delete(account);
    }

    @Transactional
    public void replenish(Account fromAccount, Account toAccount, float amount){
        fromAccount.minusBalance(amount);
        toAccount.plusBalance(amount);
    }

    @Transactional
    public void replenish(Category fromCategory, Account toAccount, float amount, User user){
        Operation operation = new Operation();

        operation.setCategory(fromCategory);
        operation.setInvolvedAccount(toAccount);
        operation.setUser(user);
        operation.setAmount(amount);
        operation.setComment("");

        if(operation.getCategory().getCategoryType() == CategoryType.EXPENSE){
            operation.getInvolvedAccount().minusBalance(operation.getAmount());
        }
        else{
            operation.getInvolvedAccount().plusBalance(operation.getAmount());
        }

        operationRepository.save(operation);
    }

    @Transactional
    public void archiveAccount(Account account){
        account.setArchived(true);
    }

    @Transactional
    public void unzipAccount(Account account){
        account.setArchived(false);
    }
}
