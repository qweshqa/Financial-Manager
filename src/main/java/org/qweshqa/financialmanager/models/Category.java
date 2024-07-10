package org.qweshqa.financialmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.qweshqa.financialmanager.utils.enums.CategoryType;

import java.util.List;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank(message = "This field shouldn't be blank")
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "balance")
    private float balance = 0;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CategoryType categoryType;

    @OneToMany(mappedBy = "category")
    private List<Operation> operationList;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Category() {
    }

    public Category(int id, String name, float balance, List<Operation> operationList, User user) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.operationList = operationList;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @NotBlank(message = "This field shouldn't be blank") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "This field shouldn't be blank") String name) {
        this.name = name;
    }

    @NotNull
    public float getBalance() {
        return balance;
    }

    public void setBalance(@NotNull float balance) {
        this.balance = balance;
    }

    public @NotNull CategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(@NotNull CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public List<Operation> getFinanceList() {
        return operationList;
    }

    public void setFinanceList(List<Operation> operationList) {
        this.operationList = operationList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
