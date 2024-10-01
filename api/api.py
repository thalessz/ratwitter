import mysql.connector as mysql
from flask import Flask, jsonify
from dotenv import load_dotenv

app = Flask(__name__)

@app.route('/login', methods=['POST'])
def login():
    data = request.json
    username = data.get('username')
    password = data.get('password')
    
    

if __name__ == '__main__':
    app.run(debug=True)