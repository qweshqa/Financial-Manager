package org.qweshqa.financialmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.Month;

@Entity
@Table(name = "operation")
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull(message = "Amount must not be blank")
    @Min(value = 0, message = "Amount minimum value is 0")
    @Column(name = "amount")
    private float amount;

    @Column(name = "comment")
    private String comment;

    @NotNull
    @Column(name = "date")
    private LocalDate date = LocalDate.now();

    @NotNull
    @Column(name = "year")
    private int year = LocalDate.now().getYear();

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "involved_account", referencedColumnName = "id")
    private Account involvedAccount;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    public Operation() {
    }

    public Operation(int id, float amount, String comment, LocalDate date, int year,
                     User user, Account involvedAccount, Category category) {
        this.id = id;
        this.amount = amount;
        this.comment = comment;
        this.date = date;
        this.year = year;
        this.user = user;
        this.involvedAccount = involvedAccount;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @NotNull float getAmount() {
        return amount;
    }

    public void setAmount(@NotNull float amount) {
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Account getInvolvedAccount() {
        return involvedAccount;
    }

    public void setInvolvedAccount(Account involvedAccount) {
        this.involvedAccount = involvedAccount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}