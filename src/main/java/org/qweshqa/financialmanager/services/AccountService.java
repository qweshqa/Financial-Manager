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

@Service
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public List<Account> createAccountsSetsByDefault(User owner){
        List<Account> accounts = new ArrayList<>();

        Account cashFinancialAccount = new Account();
        cashFinancialAccount.setOwner(owner);
        cashFinancialAccount.setAccountType(AccountType.FINANCIAL);
        cashFinancialAccount.setName("Cash");

        accounts.add(cashFinancialAccount);
        accountRepository.save(cashFinancialAccount);

        Account cardFinancialAccount = new Account();
        cardFinancialAccount.setOwner(owner);
        cardFinancialAccount.setAccountType(AccountType.FINANCIAL);
        cardFinancialAccount.setName("Card");

        accounts.add(cardFinancialAccount);
        accountRepository.save(cardFinancialAccount);

        Account savingsAccount = new Account();
        savingsAccount.setOwner(owner);
        savingsAccount.setAccountType(AccountType.SAVINGS);
        savingsAccount.setName("For a dream");

        accounts.add(savingsAccount);
        accountRepository.save(savingsAccount);

        return accounts;
    }

    @Transactional
    public void save(Account account, User owner){
        account.setOwner(owner);
        accountRepository.save(account);
    }
}
