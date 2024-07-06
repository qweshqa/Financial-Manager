package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Account;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.AccountRepository;
import org.qweshqa.financialmanager.utils.AccountType;
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

    public Optional<Account> findById(int id){
        return accountRepository.findById(id);
    }

    public List<Account> findAllUserAccountsByType(User user, AccountType accountType){
        return accountRepository.findAllByTypeAndOwner(accountType, user);
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
}
