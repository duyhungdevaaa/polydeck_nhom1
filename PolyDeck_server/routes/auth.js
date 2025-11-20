const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');

// Đăng ký
router.post('/register', authController.register);

// Đăng nhập
router.post('/login', authController.login);

// Đăng nhập bằng Google
router.post('/google', authController.googleLogin);

module.exports = router;
