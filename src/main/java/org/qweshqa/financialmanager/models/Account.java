package org.qweshqa.financialmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;
import org.qweshqa.financialmanager.utils.AccountType;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Size(max = 35)
    @NotBlank(message = "This field shouldn't be blank")
    @Column(name = "name")
    private String name;

    @Size(max = 70)
    @ColumnDefault(value = "Without description.")
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "balance")
    private float balance = 0;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AccountType accountType;

    public Account() {
    }

    public Account(int id, String name, String description, float balance, User owner, AccountType accountType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.balance = balance;
        this.owner = owner;
        this.accountType = accountType;
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

    public float getBalance() {
        return balance;
    }

    public void plusBalance(float amount) {
        this.balance += amount;
    }

    public void minusBalance(float amount) {
        this.balance -= amount;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public @NotNull AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(@NotNull AccountType accountType) {
        this.accountType = accountType;
    }
}
