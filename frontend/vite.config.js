import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    server: {
      port: 9000,
      strictPort: true,
      proxy: env.API_PROXY_TARGET
        ? {
            '/member': {
              target: env.API_PROXY_TARGET,
              changeOrigin: true,
            },
          }
        : {},
    },
  }
})
