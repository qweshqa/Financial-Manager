package org.qweshqa.financialmanager.models;

import jakarta.persistence.*;
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

    @NotBlank
    @Size(min = 3, max = 30)
    @Column(name = "username")
    private String username;

    @Size(max = 50)
    @Column(name = "userDisplayName")
    private String userDisplayName;

    @NotBlank(message = "Password field is required to fill")
    @Size(min = 8, message = "Password length must be at least 8")
    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Finance> userFinances;

    public User(){

    }

    public User(int id, String username, String userDisplayName, String password, List<Finance> userFinances) {
        this.id = id;
        this.username = username;
        this.userDisplayName = userDisplayName;
        this.password = password;
        this.userFinances = userFinances;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @NotBlank @Size(min = 3, max = 30) String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank @Size(min = 3, max = 30) String username) {
        this.username = username;
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
}
