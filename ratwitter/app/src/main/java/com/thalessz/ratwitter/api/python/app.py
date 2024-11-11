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

    if not username or not password:
        return jsonify({'message': 'Username e password são obrigatórios'}), 400

    try:
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)
        query = "SELECT NOME, USERNAME, EMAIL, PASSWORD FROM usuario WHERE username = %s AND password = %s"
        cursor.execute(query, (username, password))
        result = cursor.fetchone()

        if result is None:
            return jsonify({'message': 'Login inválido'}), 401
        
        return jsonify(result), 200

    except mysql.Error as error:
        return jsonify({'error': str(error)}), 500

    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/cadastro', methods=['POST'])
def cadastro():
    data = request.json
    nome = data.get('nome')
    username = data.get('username')
    password = data.get('password')
    email = data.get('email')

    if not nome or not username or not password or not email:
        return jsonify({'message': 'Todos os campos são obrigatórios'}), 400

    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        query = "INSERT INTO usuario (nome, username, password, email) VALUES (%s, %s, %s, %s)"
        cursor.execute(query, (nome, username, password, email))
        conn.commit()
        return jsonify({'message': 'Cadastro realizado com sucesso'}), 201

    except mysql.Error as error:
        return jsonify({'error': str(error)}), 500

    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/fetch_posts', methods=['GET'])
def fetch_posts():
    try:
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)
        
        query = '''
            SELECT id, content, user_id, created_at 
            FROM posts 
            ORDER BY created_at DESC;
        '''
        cursor.execute(query)
        result = cursor.fetchall()

        if not result:
            return jsonify({'message': 'Nenhum post encontrado'}), 200
        
        posts = []
        for row in result:
            posts.append({
                'id': row['id'],
                'content': row['content'],
                'user_id': row['user_id'],
                'created_at': row['created_at']
            })

        return jsonify(posts), 200

    except mysql.Error as error:
        return jsonify({'error': str(error)}), 500

    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/add_post', methods=['POST'])
def add_post():
    data = request.json
    content = data.get('content')
    user_id = data.get('user_id')

    if not content or not user_id:
        return jsonify({'message': 'Conteúdo e user_id são obrigatórios'}), 400

    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        query = "INSERT INTO posts (content, user_id) VALUES (%s, %s)"
        cursor.execute(query, (content, user_id))
        conn.commit()
        return jsonify({'message': 'Post adicionado com sucesso'}), 201

    except mysql.Error as error:
        return jsonify({'error': str(error)}), 500

    finally:
        if cursor:
            cursor.close()
        if conn:
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
        return jsonify({'error': str(error)}), 500

    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/fetch_user/<int:id>', methods=['GET'])
def fetch_user(id):
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)
        query = "SELECT * FROM USUARIO WHERE ID = %s LIMIT 1"
        
        # Execute a consulta, passando o ID como parâmetro
        cursor.execute(query, (id,))
        result = cursor.fetchone()
        
        if result is None:
            return jsonify({'message': 'Usuário não encontrado'}), 404
        
        return jsonify(result), 200
    except Exception as error:
        return jsonify({'error': str(error)}), 500
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()  

@app.route('/fetch_uid/<username>', methods=['GET'])

def fetch_uid(username):
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)
        query = "SELECT ID FROM USUARIO WHERE USERNAME = %s LIMIT 1"
        
        # Execute a consulta, passando o username como parâmetro
        cursor.execute(query, (username,))
        result = cursor.fetchone()
        
        if result is None:
            return jsonify({'message': 'Usuário não encontrado'}), 404
        
        return jsonify({'user_id': result['ID']}), 200
    except Exception as error:
        return jsonify({'error': str(error)}), 500
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()
        
        
if __name__ == '__main__':
    app.run(debug=True)
