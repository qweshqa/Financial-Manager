package org.qweshqa.financialmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;
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

    @NotNull(message = "Name must not be blank")
    @Size(max = 50, message = "Name length must be smaller than 50")
    @Column(name = "name")
    private String name;

    @NotNull(message = "Amount must not be blank")
    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "comment")
    private String comment;

    @NotNull
    @Column(name = "date")
    // set date by default
    private LocalDate date = LocalDate.now();

    // set month by default
    private Month month = LocalDate.now().getMonth();;

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
