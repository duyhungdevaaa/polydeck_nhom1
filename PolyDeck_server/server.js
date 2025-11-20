const express = require('express');
const cors = require('cors');
const connectDB = require('./config/database');
require('dotenv').config();

const app = express();

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Import models để đảm bảo chúng được load
require('./models/ChuDe');
require('./models/TuVung');
require('./models/NguoiDung');
require('./models/TienDoHocTap');
require('./models/BaiQuiz');
require('./models/CauHoi');
require('./models/LichSuLamBai');
require('./models/ThongBao');
require('./models/YeuCauHoTro');

// Import middleware
// const errorHandler = require('./middleware/errorHandler');
// const authMiddleware = require('./middleware/auth');

// Routes
app.get('/', (req, res) => {
  res.json({ message: 'PolyDeck API Server is running!' });
});

// Routes
app.use('/api/auth', require('./routes/auth'));
// app.use('/api/chu-de', require('./routes/chuDe'));
// app.use('/api/tu-vung', require('./routes/tuVung'));
// app.use('/api/quiz', require('./routes/quiz'));
// app.use('/api/nguoi-dung', require('./routes/nguoiDung'));
// app.use('/api/thong-bao', require('./routes/thongBao'));
// app.use('/api/ho-tro', require('./routes/hoTro'));
// app.use('/api/admin', require('./routes/admin'));

// Error handler middleware (đặt cuối cùng)
// app.use(errorHandler);

const PORT = process.env.PORT || 3000;

// Khởi động server sau khi kết nối database
const startServer = async () => {
  try {
    // Connect to MongoDB và tạo database/collections
    await connectDB();
    
    // Start server
    app.listen(PORT, () => {
      console.log('═══════════════════════════════════════');
      console.log(`✓ Server đang chạy trên port ${PORT}`);
      console.log(`✓ API: http://localhost:${PORT}`);
      console.log('═══════════════════════════════════════');
      console.log('');
    });
  } catch (error) {
    console.error('Failed to start server:', error);
    process.exit(1);
  }
};

startServer();

