import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3001,
    host: true,
    proxy: {
      '/auth': {
        target: 'http://user-management-service:8082',
        changeOrigin: true,
      }
    }
  }
})
