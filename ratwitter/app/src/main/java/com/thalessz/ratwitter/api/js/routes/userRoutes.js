const express = require('express');
const router = express.router();
const userController=require('../controllers/userController');

router.post('/login', userController.login);
router.post('/cadastro', userController.cadastro);
router.get('/fetch_user:id', userController.fetchByID);
router.get('/fetch_uid/:username', userController.fetchID);

module.exports = router;

