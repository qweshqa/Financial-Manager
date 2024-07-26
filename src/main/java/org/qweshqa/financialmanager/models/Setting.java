package org.qweshqa.financialmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "setting")
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull
    @Column(name = "currency_unit")
    private String currencyUnit;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Setting() {
    }

    public Setting(int id, String currencyUnit, User user) {
        this.id = id;
        this.currencyUnit = currencyUnit;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @NotNull String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(@NotNull String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
