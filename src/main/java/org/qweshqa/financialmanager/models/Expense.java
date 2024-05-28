package org.qweshqa.financialmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

@Entity
@Table(name = "spending")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull
    @Size(max = 50)
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "amount")
    private BigDecimal amount;

    @NotBlank
    @Column(name = "currency")
    private String currency;

    @NotNull
    @Column(name = "date")
    private LocalDate date;

    private Month month;

    public Expense() {
    }

    public Expense(int id, String name, BigDecimal amount, String currency, LocalDate date) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.month = date.getMonth();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal quantity) {
        this.amount = quantity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public String getAmountToDisplay() {
        return amount + " " + currency;
    }
}
