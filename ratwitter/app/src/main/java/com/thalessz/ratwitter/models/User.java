package com.thalessz.ratwitter.models;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String nome;
    private String username;
    private String password;
    private String email;

    public User( String nome, String username, String password, String email) {
        this.nome = nome;
        this.username = username;
        this.password = password;
        this.email = email;
    }
    public User(){
    }
    // Getters


    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    // Setters

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password; 
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
