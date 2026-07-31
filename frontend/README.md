# 铁路票务系统前端

技术栈：Vue 3、Vite、Vue Router、Vuex、Axios、Ant Design Vue。

## 启动

```bash
npm install
npm run dev
```

开发地址：`http://127.0.0.1:9000`

开发环境下，`/member/**` 请求会由 Vite 代理到
`http://127.0.0.1:8000` 的网关服务。

## 与老师 Vue CLI 项目的对应关系

| Vue CLI | Vue 3 + Vite |
| --- | --- |
| `vue-cli-service serve --mode dev --port 9000` | `vite --host 0.0.0.0`，端口在 `vite.config.js` 中配置 |
| `vue.config.js` | `vite.config.js` |
| `process.env.VUE_APP_SERVER` | `import.meta.env.VITE_API_BASE_URL` |
| `.env.dev` | `.env.development` |
| `.env.prod` | `.env.production` |
| `public/index.html` | 根目录 `index.html` |

`src/main.js`、`App.vue`、`router/`、`store/` 和 Vue 单文件组件的写法与
老师的 Vue 3 项目保持一致。
