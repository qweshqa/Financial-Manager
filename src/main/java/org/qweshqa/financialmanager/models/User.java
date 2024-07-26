package org.qweshqa.financialmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Email
    @NotBlank(message = "Email field is required to fill")
    @Column(name = "email")
    private String email;

    @NotNull
    @Column(name = "register_date")
    private LocalDate registerDate = LocalDate.now();

    @NotBlank(message = "Password field is required to fill")
    @Size(min = 8, message = "Password length must be at least 8")
    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Operation> userOperations;

    @OneToMany(mappedBy = "owner")
    private List<Account> userAccounts;

    @OneToMany(mappedBy = "user")
    private List<Category> userCategories;

    @OneToOne(mappedBy = "user")
    private Setting setting;

    public User(){

    }

    public User(int id, String email, String password, List<Operation> userOperations, List<Account> userAccounts, List<Category> userCategories, Setting setting) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.userOperations = userOperations;
        this.userAccounts = userAccounts;
        this.userCategories = userCategories;
        this.setting = setting;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @Email @NotBlank(message = "Email field is required to fill") String getEmail() {
        return email;
    }

    public void setEmail(@Email @NotBlank(message = "Email field is required to fill") String email) {
        this.email = email;
    }

    public @NotNull LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(@NotNull LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    public @NotBlank(message = "Password field is required to fill") @Size(min = 8, message = "Password length must be at least 8") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password field is required to fill") @Size(min = 8, message = "Password length must be at least 8") String password) {
        this.password = password;
    }

    public List<Operation> getUserOperations() {
        return userOperations;
    }

    public void setUserOperations(List<Operation> userOperations) {
        this.userOperations = userOperations;
    }

    public List<Account> getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(List<Account> userAccounts) {
        this.userAccounts = userAccounts;
    }

    public List<Category> getUserCategories() {
        return userCategories;
    }

    public void setUserCategories(List<Category> userCategories) {
        this.userCategories = userCategories;
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public float getBalance() {
        return (float) userAccounts.stream().mapToDouble(Account::getBalance).sum();
    }
}
