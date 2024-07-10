package org.qweshqa.financialmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.qweshqa.financialmanager.utils.enums.OperationType;

import java.time.LocalDate;
import java.time.Month;

@Entity
@Table(name = "operation")
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank(message = "Name must not be blank")
    @Size(max = 50, message = "Name length must be smaller than 50")
    @Column(name = "name")
    private String name;

    @NotNull(message = "Amount must not be blank")
    @Min(value = 0, message = "Amount minimum value is 0")
    @Column(name = "amount")
    private float amount;

    @Column(name = "comment")
    private String comment;

    @NotNull
    @Column(name = "date")
    private LocalDate date = LocalDate.now();

    private Month month = LocalDate.now().getMonth();;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private OperationType type;

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

    public Operation(int id, String name, float amount, String comment, LocalDate date, Month month, OperationType type, User user, Account involvedAccount, Category category) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.comment = comment;
        this.date = date;
        this.month = month;
        this.type = type;
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

    public @NotNull @Size(max = 50) String getName() {
        return name;
    }

    public void setName(@NotNull @Size(max = 50) String name) {
        this.name = name;
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

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) { this.month = month; }

    public @NotNull OperationType getType() {
        return type;
    }

    public void setType(@NotNull OperationType type) {
        this.type = type;
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
