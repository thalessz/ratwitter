const express=require('express');
const router=express.Router();
const postController=require('../controllers/postController');

router.get('/fetch_posts',postController.fetchPosts);
router.get('/fetch_by_uid/:uid', postController.fetchPostByUid);
router.get('/check_if_liked/:post_id', postController.check_if_liked);
router.post('/add_post',postController.addPost);
router.post('/like_post/:post_id',postController.likePost);
router.delete('/unlike_post/:post_id',postController.unlikePost);

module.exports=router;