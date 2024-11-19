//Mema coisa do users só que pra posts
const db = require('../config/db');

const Post = {
    fetchAll: (callback) => {
        const query = "SELECT id,content,user_id, created_at FROM posts ORDER BY RAND()"
        db.query(query, callback);
    },
    addPost: (postData, callback)=>{
        const query="INSERT INTO posts (contnet,user_id) VALUES (?,?)";
        db.query(query,[postData.content, postData.user_id], callback);
    },
    likePost: (postId, userId, callback) => {
        const query = "INSERT INTO likes (user_id, post_id) VALUES (?, ?)";
        db.query(query, [userId, postId], callback);
    },
    unlikePost: (postId, userId, callback) => {
        const query = "DELETE FROM likes WHERE user_id = ? AND post_id = ?";
        db.query(query, [userId, postId], callback);
    }
};

module.exports = Post;
