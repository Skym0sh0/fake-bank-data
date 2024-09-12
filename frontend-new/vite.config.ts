import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        vue(),
        vuetify({autoImport: true})
    ],
    server: {
        proxy: {
            '/dev-proxy': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                secure: false,
                ws: true,
                rewrite: (path) => path.replace(/^\/dev-proxy/, '')
            },
        },
        cors: false
    },
    build: {
        outDir: 'build/dist'
    }
})
