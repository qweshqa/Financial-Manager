package org.qweshqa.financialmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Email
    @NotBlank(message = "Email field is required to fill")
    @Column(name = "email")
    private String email;

    @Size(max = 50)
    @Column(name = "user_display_name")
    private String userDisplayName;

    @NotBlank(message = "Password field is required to fill")
    @Size(min = 8, message = "Password length must be at least 8")
    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Finance> userFinances;

    @OneToOne(mappedBy = "user")
    private Setting setting;

    public User(){

    }

    public User(int id, String email, String userDisplayName, String password, List<Finance> userFinances, Setting setting) {
        this.id = id;
        this.email = email;
        this.userDisplayName = userDisplayName;
        this.password = password;
        this.userFinances = userFinances;
        this.setting = setting;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @Email @NotBlank(message = "Email field is required to fill") String getEmail() {
        return email;
    }

    public void setEmail(@Email @NotBlank(message = "Email field is required to fill") String email) {
        this.email = email;
    }

    public @Size(max = 50) String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(@Size(max = 50) String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public @NotBlank(message = "Password field is required to fill") @Size(min = 8, message = "Password length must be at least 8") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password field is required to fill") @Size(min = 8, message = "Password length must be at least 8") String password) {
        this.password = password;
    }

    public List<Finance> getUserFinances() {
        return userFinances;
    }

    public void setUserFinances(List<Finance> userFinances) {
        this.userFinances = userFinances;
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }
}
