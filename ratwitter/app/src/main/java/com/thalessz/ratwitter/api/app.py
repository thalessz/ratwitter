from flask import Flask, request, jsonify
import mysql.connector as mysql

# Configuração do banco de dados
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '',
    'database': 'ratwitter'
}

app = Flask(__name__)

def get_db_connection():
    """Estabelece uma conexão com o banco de dados."""
    return mysql.connect(**db_config)

@app.route('/login', methods=['POST'])
def login():
    data = request.json
    username = data.get('username')
    password = data.get('password')

    try:
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)
        query = "SELECT * FROM usuario WHERE username = %s AND password = %s"
        cursor.execute(query, (username, password))
        result = cursor.fetchone()

        if result is None:
            return jsonify({'message': 'Login inválido'}), 401
        return jsonify(result), 200

    except mysql.Error as error:
        return jsonify({'message': str(error)}), 500

    finally:
        if conn.is_connected():
            cursor.close()
            conn.close()

@app.route('/cadastro', methods=['POST'])
def cadastro():
    data = request.json
    nome = data.get('nome')
    username = data.get('username')
    password = data.get('password')
    email = data.get('email')

    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        query = "INSERT INTO usuario (nome, username, password, email) VALUES (%s, %s, %s, %s)"
        cursor.execute(query, (nome, username, password, email))
        conn.commit()
        return jsonify({'message': 'Cadastro realizado com sucesso'}), 201

    except mysql.Error as error:
        return jsonify({'message': str(error)}), 500

    finally:
        if conn.is_connected():
            cursor.close()
            conn.close()

@app.route('/fetch_posts', methods=['GET'])
def fetch_posts():
    try:
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)
        query = "SELECT * FROM posts ORDER BY created_at DESC"
        cursor.execute(query)
        result = cursor.fetchall()
        return jsonify(result), 200

    except mysql.Error as error:
        return jsonify({'message': str(error)}), 500

    finally:
        if conn.is_connected():
            cursor.close()
            conn.close()

@app.route('/add_post', methods=['POST'])
def add_post():
    data = request.json
    content = data.get('content')
    user_id = data.get('user_id')

    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        query = "INSERT INTO posts (content, user_id) VALUES (%s, %s)"
        cursor.execute(query, (content, user_id))
        conn.commit()
        return jsonify({'message': 'Post adicionado com sucesso'}), 201

    except mysql.Error as error:
        return jsonify({'message': str(error)}), 500

    finally:
        if conn.is_connected():
            cursor.close()
            conn.close()

@app.route('/like_post/<int:post_id>', methods=['POST'])
def like_post(post_id):
    try:
        conn = get_db_connection()
        cursor = conn.cursor()

        query = "UPDATE posts SET like_count = like_count + 1 WHERE id = %s"
        cursor.execute(query, (post_id,))

        if cursor.rowcount == 0:
            return jsonify({'message': 'Post não encontrado'}), 404

        conn.commit()
        return jsonify({'message': 'Post curtido com sucesso'}), 200

    except mysql.Error as error:
        return jsonify({'message': str(error)}), 500

    finally:
        if conn.is_connected():
            cursor.close()
            conn.close()

@app.route('/docs', methods=['GET'])
def docs():
    documentation = {
        "API": "Ratwitter",
        "Endpoints": {
            "/login": {
                "method": "POST",
                "description": "Realiza o login do usuário.",
                "body": {
                    "username": "string",
                    "password": "string"
                }
            },
            "/cadastro": {
                "method": "POST",
                "description": "Cadastra um novo usuário.",
                "body": {
                    "nome": "string",
                    "username": "string",
                    "password": "string",
                    "email": "string"
                }
            },
            "/fetch_posts": {
                "method": "GET",
                "description": "Retorna todos os posts ordenados pela data de criação."
            },
            "/add_post": {
                "method": "POST",
                "description": "Adiciona um novo post.",
                "body": {
                    "content": "string",
                    "user_id": "integer"
                }
            },
            "/like_post/<post_id>": {
                "method": "POST",
                "description": "Incrementa a contagem de likes para um post específico."
            }
        }
    }
    
    return jsonify(documentation), 200

if __name__ == '__main__':
    app.run(debug=True)
