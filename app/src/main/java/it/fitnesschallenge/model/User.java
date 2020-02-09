package it.fitnesschallenge.model;

public class User {

    private String username;
    private String nome;
    private String surname;
    private String rule;

    private User(){
        //necessario per fire base
    }

    public User(String username, String nome, String surname, String rule) {
        this.username = username;
        this.nome = nome;
        this.surname = surname;
        this.rule = rule;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}
