CREATE DATABASE ratwitter;
USE ratwitter;

CREATE TABLE usuario (
    id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE posts (
    id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
    content TEXT NOT NULL,
    user_id INTEGER NOT NULL,
    like_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE likes (
    id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
    user_id INTEGER NOT NULL,
    post_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

DELIMITER $$

CREATE TRIGGER somar_like
AFTER INSERT ON likes
FOR EACH ROW
BEGIN
    UPDATE posts 
    SET like_count = (SELECT COUNT(*) FROM likes WHERE post_id = NEW.post_id)
    WHERE id = NEW.post_id;
END;
$$


CREATE TRIGGER remover_like
AFTER DELETE ON likes
FOR EACH ROW
BEGIN
    UPDATE posts 
    SET like_count = (SELECT COUNT(*) FROM likes WHERE post_id = OLD.post_id)
    WHERE id = OLD.post_id;
END;
$$

DELIMITER ;