module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:88',
        changeOrigin: true,
      }
    }
  }
};
