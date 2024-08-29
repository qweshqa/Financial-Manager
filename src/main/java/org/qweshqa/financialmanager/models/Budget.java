package org.qweshqa.financialmanager.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @Column(name = "uuid")
    private String uuid = java.util.UUID.randomUUID().toString();

    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private double amount;

    @Column(name = "current_amount")
    private double currentAmount;

    @NotNull
    @Column(name = "user_id")
    private int userId;

    @NotNull
    @Column(name = "category_id")
    private int categoryId;

    @Column(name = "on_all_expenses")
    private boolean onAllExpenses;

    public Budget() {
    }

    public Budget(String uuid, String description, double amount, double currentAmount, int userId, int categoryId, boolean onAllExpenses) {
        this.uuid = uuid;
        this.description = description;
        this.amount = amount;
        this.currentAmount = currentAmount;
        this.userId = userId;
        this.categoryId = categoryId;
        this.onAllExpenses = onAllExpenses;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isOnAllExpenses() {
        return onAllExpenses;
    }

    public void setOnAllExpenses(boolean onAllExpenses) {
        this.onAllExpenses = onAllExpenses;
    }
}