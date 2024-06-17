package org.qweshqa.financialmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.qweshqa.financialmanager.utils.FinanceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

@Entity
@Table(name = "finances")
public class Finance {

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

    @Column(name = "comment")
    private String comment;

    @NotNull
    @Column(name = "date")
    private LocalDate date;

    private Month month;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private FinanceType type;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;

    public Finance() {
    }

    public Finance(int id, String name, BigDecimal amount, String currency, String comment, LocalDate date, FinanceType type) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.currency = currency;
        this.comment = comment;
        this.date = date;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @NotNull @Size(max = 50) String getName() {
        return name;
    }

    public void setName(@NotNull @Size(max = 50) String name) {
        this.name = name;
    }

    public @NotNull BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull BigDecimal amount) {
        this.amount = amount;
    }

    public String getAmountToDisplay() {
        return amount + " " + currency;
    }

    public @NotBlank String getCurrency() {
        return currency;
    }

    public void setCurrency(@NotBlank String currency) {
        this.currency = currency;
    }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public @NotNull LocalDate getDate() {
        return date;
    }

    public void setDate(@NotNull LocalDate date) {
        this.date = date;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) { this.month = month; }

    public @NotNull FinanceType getType() {
        return type;
    }

    public void setType(@NotNull FinanceType type) {
        this.type = type;
    }
}
