package com.thalessz.ratwitter.models;

public class User {
    private int id;
    private String nome;
    private String username;
    private String password;
    private String email;
    private String created_at;

    public User(int id, String nome, String username, String password, String email, String created_at) {
        this.id = id;
        this.nome = nome;
        this.username = username;
        this.password = password;
        this.email = email;
        this.created_at = created_at;
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

    public String getCreatedAt() {
        return created_at;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

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

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }
}
