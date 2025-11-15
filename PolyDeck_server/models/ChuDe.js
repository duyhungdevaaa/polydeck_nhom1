const mongoose = require('mongoose');

const chuDeSchema = new mongoose.Schema({
  ten_chu_de: {
    type: String,
    required: true
  },
  anh_bia: {
    type: String,
    default: null
  }
}, {
  timestamps: true,
  collection: 'chude'
});

// Index để tìm kiếm nhanh
chuDeSchema.index({ ten_chu_de: 1 });

module.exports = mongoose.model('ChuDe', chuDeSchema);

