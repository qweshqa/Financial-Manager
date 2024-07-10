package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Account;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.AccountRepository;
import org.qweshqa.financialmanager.utils.enums.AccountType;
import org.qweshqa.financialmanager.utils.exceptions.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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

    public List<Account> findAllUserAccountsByArchive(User user, boolean isArchive){
        return accountRepository.findAllByOwnerAndArchived(user, isArchive);
    }

    public List<Account> findAllUserAccountsByTypeAndArchive(User user, AccountType accountType, boolean isArchived){
        return accountRepository.findAllByTypeAndOwnerAndArchived(accountType, user, isArchived);
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
    public void archiveAccount(Account account){
        account.setArchived(true);
    }

    @Transactional
    public void unzipAccount(Account account){
        account.setArchived(false);
    }
}
