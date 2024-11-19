const express=require('express');
const router=express.Router();
const postController=require('../controllers/postController');

router.get('/fetch_posts',postController.fetchPosts);
router.post('/add_post',postController.addPost);
router.post('/like_post/:post_id',postController.likePost);
router.delete('/unlike_post/:post_id',postController.unlikePost);

module.exports=router;