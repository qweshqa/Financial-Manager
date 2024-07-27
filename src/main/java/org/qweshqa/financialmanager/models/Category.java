package org.qweshqa.financialmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;
import org.qweshqa.financialmanager.utils.enums.CategoryType;

import java.time.LocalDate;
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
    @ColumnDefault("false")
    @Column(name = "archived")
    private boolean archived = false;

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

    public Category(int id, String name, List<Operation> operationList, User user) {
        this.id = id;
        this.name = name;
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

    public float getBalance() {
        return (float) operationList.stream()
                .filter(operation -> operation.getDate().isBefore(LocalDate.now()) || operation.getDate().isEqual(LocalDate.now()))
                .mapToDouble(Operation::getAmount).sum();
    }

    @NotNull
    public boolean isArchived() {
        return archived;
    }

    public void setArchived(@NotNull boolean archived) {
        this.archived = archived;
    }

    public @NotNull CategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(@NotNull CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public List<Operation> getOperationList() {
        return operationList;
    }

    public void setOperationList(List<Operation> operationList) {
        this.operationList = operationList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
