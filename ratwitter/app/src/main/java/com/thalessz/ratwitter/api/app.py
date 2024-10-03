from flask import Flask, request, jsonify
import mysql.connector as mysql

db_config = {
    'host':'localhost',
    'user':'root',
    'password':'',
    'database':'ratwitter'
}
app = Flask(__name__)

@app.route('/login', methods=['POST'])
def login():
    data = request.json
    username = data.get('username')
    password = data.get('password')
    
    
    try:
        conn = mysql.connect(**db_config)
        cursor = conn.cursor()
        query = "SELECT * FROM usuario WHERE username = %s AND password = %s"
        cursor.execute(query(username,password))
        result = cur.fetchall()
        if result is None:
            return jsonify({'message':'Login inválido'}), 401
        return jsonify(result), 200
    except mysql.Error as error:
        return jsonify({'message': str(error)}), 500
    finally:
        if conn.is_connected():
            cursor.close()
            conn.close()

@app.route('/cadastro', methods=['POST'])
def cadastro():
    data = request.json()
    nome = data.get('nome')
    username = data.get('username')
    password = data.get('password')
    email = data.get('email')
    
    try:
        conn = mysql.connect(**db_config)
        cursor = conn.cursor()
        query = "INSERT INTO usuario (nome,username,password,email) VALUES (%s,%s,%s,%s)"
        cursor.execute(query, (nome,username,password,email))
        conn.commit()
        return jsonify({'message':'Cadastro realizado com sucesso'}), 201
    except mysql.Error as error:
        return jsonify({'message': str(error)}), 500
    finally:
        if conn.is_connected():
            cursor.close()
            conn.close()

if __name__ == '__main__':
    app.run(debug=True)