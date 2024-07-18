package models;

public class User {

    private String email;
    private String password;
    private String name;

    // Вместо конструктора с параметрами, используется билдер. Методы принимают параметр и возвращают себя же.
    public User withEmail(String email) {
        this.email = email;
        return this;
    }

    public User withPassword(String password) {
        this.password = password;
        return this;
    }

    public User withName(String name) {
        this.name = name;
        return this;
    }

    // Геттеры и Сеттеры:
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
