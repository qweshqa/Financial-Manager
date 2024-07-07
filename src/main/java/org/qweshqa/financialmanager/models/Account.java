package org.qweshqa.financialmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;
import org.qweshqa.financialmanager.utils.AccountType;

import java.util.List;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Size(max = 35)
    @NotBlank(message = "This field should not be blank")
    @Column(name = "name")
    private String name;

    @Size(max = 70)
    @ColumnDefault(value = "'Without description.'")
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "balance")
    private float balance = 0;

    @NotNull
    @Column(name = "archived")
    private boolean archived = false;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AccountType type;

    @OneToMany
    private List<Finance> accountLinkedFinances;

    public Account() {
    }

    public Account(int id, String name, String description, float balance, boolean archived, User owner, AccountType type, List<Finance> accountLinkedFinances) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.balance = balance;
        this.archived = archived;
        this.owner = owner;
        this.type = type;
        this.accountLinkedFinances = accountLinkedFinances;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @Size(max = 35) @NotBlank(message = "This field shouldn't be blank") String getName() {
        return name;
    }

    public void setName(@Size(max = 35) @NotBlank(message = "This field shouldn't be blank") String name) {
        this.name = name;
    }

    public @Size(max = 70) String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 70) String description) {
        this.description = description;
    }

    public @NotNull float getBalance() {
        return balance;
    }

    public void setBalance(@NotNull float balance){
        this.balance = balance;
    }

    public void plusBalance(float amount) {
        this.balance += amount;
    }

    public void minusBalance(float amount) {
        this.balance -= amount;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType accountType) {
        this.type = accountType;
    }

    public List<Finance> getAccountLinkedFinances() {
        return accountLinkedFinances;
    }

    public void addAccountLinkedFinance(Finance accountLinkedFinance) {
        this.accountLinkedFinances.add(accountLinkedFinance);
    }
}
