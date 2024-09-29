import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'
import svgLoader from 'vite-svg-loader'
import path from 'path';

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        vue(),
        vuetify({autoImport: true}),
        svgLoader()
    ],
    resolve: {
        alias: {
            '@api': path.resolve(__dirname, 'build/generated-ts/api')
        }
    },
    build: {
        outDir: 'build/dist'
    },
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
    }
})
