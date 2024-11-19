//configuração do banco de dados
const mysql = require('mysql2');
const dotenv = require('dotenv');

dotenv.config();

const db = mysql.createConnection({
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'ratwitter'
});

db.connect(err => {
    if (err) throw err;
    console.log('conexão com banco de dados feita')
})

module.exports = db;