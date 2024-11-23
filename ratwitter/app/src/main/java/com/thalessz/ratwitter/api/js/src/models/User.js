// modelo de usuário, faz o crud 
const db = require('../config/db');

// meu deus do céu, programação orientada a objetos dentro do javaScript. alguém me mata.
const User = {
    login: (username, password, callback) => {
        const query = "SELECT nome,username,password,email FROM usuario WHERE username = ? AND password = ?";
        db.query(query, [username,password], callback);
    },
    create: (userData,callback) =>{
        const query = "INSERT INTO usuario (nome,username,password,email) VALUES (?,?,?,?)";
        db.query(query, [userData.nome, userData.username, userData.password, userData.email], callback);
    },
    fetchByID: (id,callback) => {
        const query = "SELECT * from usuario WHERE id = ?";
        db.query(query, [id], callback);
    },
    fetchByUsername: (username,callback) => { // gambiarra da porra
        const query = "SELECT id from usuario WHERE username = ? LIMIT 1";
        db.query(query, [username], callback);
    }
};

module.exports = User;