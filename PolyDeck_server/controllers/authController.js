const NguoiDung = require('../models/NguoiDung');
const bcrypt = require('bcryptjs');
const crypto = require('crypto');

// Đăng ký người dùng mới
const register = async (req, res) => {
  try {
    const { ho_ten, email, mat_khau } = req.body;

    // Validate input
    if (!ho_ten || !email || !mat_khau) {
      return res.status(400).json({
        success: false,
        message: 'Vui lòng điền đầy đủ thông tin'
      });
    }

    // Validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      return res.status(400).json({
        success: false,
        message: 'Email không hợp lệ'
      });
    }

    // Validate password length
    if (mat_khau.length < 6) {
      return res.status(400).json({
        success: false,
        message: 'Mật khẩu phải có ít nhất 6 ký tự'
      });
    }

    // Kiểm tra email đã tồn tại chưa
    const existingUser = await NguoiDung.findOne({ email: email.toLowerCase() });
    if (existingUser) {
      return res.status(400).json({
        success: false,
        message: 'Email đã được sử dụng'
      });
    }

    // Hash password
    const salt = await bcrypt.genSalt(10);
    const mat_khau_hash = await bcrypt.hash(mat_khau, salt);

    // Tạo mã người dùng (dùng timestamp + random để đảm bảo unique)
    const ma_nguoi_dung = `USER_${Date.now()}_${Math.random().toString(36).substring(2, 9)}`;

    // Tạo người dùng mới
    const newUser = new NguoiDung({
      ma_nguoi_dung,
      ho_ten,
      email: email.toLowerCase(),
      mat_khau_hash,
      cap_do: 1,
      diem_tich_luy: 0,
      chuoi_ngay_hoc: 0,
      vai_tro: 'student',
      trang_thai: 'active'
    });

    await newUser.save();

    // Trả về thông tin người dùng (không trả về password)
    res.status(201).json({
      success: true,
      message: 'Đăng ký thành công',
      data: {
        ma_nguoi_dung: newUser.ma_nguoi_dung,
        ho_ten: newUser.ho_ten,
        email: newUser.email,
        cap_do: newUser.cap_do,
        diem_tich_luy: newUser.diem_tich_luy,
        chuoi_ngay_hoc: newUser.chuoi_ngay_hoc
      }
    });
  } catch (error) {
    console.error('Register error:', error);
    res.status(500).json({
      success: false,
      message: 'Lỗi server khi đăng ký',
      error: error.message
    });
  }
};

// Đăng nhập
const login = async (req, res) => {
  try {
    const { email, mat_khau } = req.body;

    // Validate input
    if (!email || !mat_khau) {
      return res.status(400).json({
        success: false,
        message: 'Vui lòng nhập email và mật khẩu'
      });
    }

    // Tìm người dùng
    const user = await NguoiDung.findOne({ email: email.toLowerCase() });
    if (!user) {
      return res.status(401).json({
        success: false,
        message: 'Email hoặc mật khẩu không đúng'
      });
    }

    // Kiểm tra trạng thái tài khoản
    if (user.trang_thai !== 'active') {
      return res.status(403).json({
        success: false,
        message: 'Tài khoản đã bị khóa'
      });
    }

    // Kiểm tra mật khẩu
    const isPasswordValid = await bcrypt.compare(mat_khau, user.mat_khau_hash);
    if (!isPasswordValid) {
      return res.status(401).json({
        success: false,
        message: 'Email hoặc mật khẩu không đúng'
      });
    }

    // Trả về thông tin người dùng (không trả về password)
    res.status(200).json({
      success: true,
      message: 'Đăng nhập thành công',
      data: {
        ma_nguoi_dung: user.ma_nguoi_dung,
        ho_ten: user.ho_ten,
        email: user.email,
        cap_do: user.cap_do,
        diem_tich_luy: user.diem_tich_luy,
        chuoi_ngay_hoc: user.chuoi_ngay_hoc,
        vai_tro: user.vai_tro,
        link_anh_dai_dien: user.link_anh_dai_dien
      }
    });
  } catch (error) {
    console.error('Login error:', error);
    res.status(500).json({
      success: false,
      message: 'Lỗi server khi đăng nhập',
      error: error.message
    });
  }
};

// Đăng nhập bằng Google
const googleLogin = async (req, res) => {
  try {
    const { id_token } = req.body; // Token từ Google Sign-In

    if (!id_token) {
      return res.status(400).json({
        success: false,
        message: 'Token không hợp lệ'
      });
    }

    // Verify token với Google
    const { OAuth2Client } = require('google-auth-library');
    const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);

    let ticket;
    try {
      ticket = await client.verifyIdToken({
        idToken: id_token,
        audience: process.env.GOOGLE_CLIENT_ID
      });
    } catch (error) {
      return res.status(401).json({
        success: false,
        message: 'Token Google không hợp lệ'
      });
    }

    const payload = ticket.getPayload();
    const { sub: google_id, email, name, picture } = payload;

    // Tìm user theo google_id hoặc email
    let user = await NguoiDung.findOne({
      $or: [
        { google_id: google_id },
        { email: email.toLowerCase() }
      ]
    });

    if (user) {
      // User đã tồn tại - cập nhật google_id nếu chưa có
      if (!user.google_id) {
        user.google_id = google_id;
        await user.save();
      }
    } else {
      // Tạo user mới
      const ma_nguoi_dung = `USER_${Date.now()}_${Math.random().toString(36).substring(2, 9)}`;
      user = new NguoiDung({
        ma_nguoi_dung,
        ho_ten: name,
        email: email.toLowerCase(),
        google_id: google_id,
        link_anh_dai_dien: picture,
        mat_khau_hash: '', // Không cần password cho Google login
        cap_do: 1,
        diem_tich_luy: 0,
        chuoi_ngay_hoc: 0,
        vai_tro: 'student',
        trang_thai: 'active'
      });
      await user.save();
    }

    // Trả về thông tin user
    res.status(200).json({
      success: true,
      message: 'Đăng nhập Google thành công',
      data: {
        ma_nguoi_dung: user.ma_nguoi_dung,
        ho_ten: user.ho_ten,
        email: user.email,
        cap_do: user.cap_do,
        diem_tich_luy: user.diem_tich_luy,
        chuoi_ngay_hoc: user.chuoi_ngay_hoc,
        vai_tro: user.vai_tro,
        link_anh_dai_dien: user.link_anh_dai_dien
      }
    });
  } catch (error) {
    console.error('Google login error:', error);
    res.status(500).json({
      success: false,
      message: 'Lỗi server khi đăng nhập Google',
      error: error.message
    });
  }
};

module.exports = {
  register,
  login,
  googleLogin
};
