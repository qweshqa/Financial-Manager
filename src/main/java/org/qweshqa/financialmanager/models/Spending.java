package org.qweshqa.financialmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "categories")
public class Spending{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull
    @Size(max = 50)
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "quantity")
    private BigDecimal quantity;

    @NotNull
    @Size(max = 20)
    @Column(name = "currency")
    private String currency;

    @NotNull
    @Column(name = "date")
    private LocalDate date;

    public Spending() {
    }

    public Spending(int id, String name, BigDecimal quantity, String currency, LocalDate date) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.currency = currency;
        this.date = date;
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
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
}
