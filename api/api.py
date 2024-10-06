from fastapi import FastAPI, HTTPException, Depends
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from pydantic import BaseModel
import mysql.connector as mysql
import os
from dotenv import load_dotenv
from typing import List
import uvicorn

load_dotenv()
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '',
    'database': 'ratwitter'
}

app = FastAPI()
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

class User(BaseModel):
    id: int
    username: str
    email: str

class Post(BaseModel):
    id: int
    content: str
    user_id: int  
    likes_count: int  

def get_db_connection():
    return mysql.connect(**db_config)

@app.post("/login", response_model=User)
async def login(form_data: OAuth2PasswordRequestForm = Depends()):
    email = form_data.username
    password = form_data.password
    
    conn = get_db_connection()
    try:
        with conn.cursor() as cur:
            query = 'SELECT ID, USERNAME, EMAIL FROM USUARIO WHERE EMAIL = %s AND PASSWORD = %s LIMIT 1'
            cur.execute(query, (email, password))
            result = cur.fetchone()
            if result:
                return User(id=result[0], username=result[1], email=result[2])
            raise HTTPException(status_code=404, detail="Usuário não encontrado")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        conn.close()

@app.get("/fetch_posts", response_model=List[Post])
async def fetch_posts():
    conn = get_db_connection()
    try:
        with conn.cursor() as cur:
            query = ''' 
                SELECT POST.ID, POST.CONTENT, POST.USER_ID, POST.LIKES_COUNT 
                FROM POST 
                WHERE STATUS = TRUE 
            '''
            cur.execute(query)
            result = cur.fetchall()
            return [Post(id=row[0], content=row[1], user_id=row[2], likes_count=row[3]) for row in result] if result else []
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        conn.close()

@app.post("/cadastro", response_model=str)
async def cadastro(user: User):
    conn = get_db_connection()
    
    try:
        with conn.cursor() as cur:
            query = '''
                INSERT INTO USUARIO (USERNAME, PASSWORD, EMAIL) 
                VALUES (%s, %s, %s)
            '''
            cur.execute(query, (user.username, user.password, user.email))
            conn.commit()
            return "Dados inseridos com sucesso"
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        conn.close()

@app.post("/submit_post", response_model=str)
async def submit_post(post: Post):
    conn = get_db_connection()
    try:
        with conn.cursor() as cur:
            query = "INSERT INTO POST (CONTENT, USER_ID) VALUES (%s, %s)"
            cur.execute(query, (post.content, post.user_id))  # Usando post.user_id para associar ao criador do post
            conn.commit()
            return "Post enviado com sucesso!"
    except Exception as e:
       raise HTTPException(status_code=500, detail=str(e))
    finally:
       conn.close()

@app.post("/like/{post_id}", response_model=str)
async def like(post_id: int):
   conn = get_db_connection()
   try:
       with conn.cursor() as cur:
           query = "UPDATE POST SET LIKES_COUNT = LIKES_COUNT + 1 WHERE ID = %s"
           cur.execute(query, (post_id,))
           if cur.rowcount == 0:
               raise HTTPException(status_code=404, detail="Post não encontrado")
           conn.commit()
           return "Post curtido com sucesso!"
   except Exception as e:
       raise HTTPException(status_code=500, detail=str(e))
   finally:
       conn.close()

if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8000)
