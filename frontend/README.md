# 铁路票务系统前端

技术栈：Vue 3、Vite、Vue Router、Vuex、Axios、Ant Design Vue。

## 启动

```bash
npm install
npm run dev
```

开发地址：`http://127.0.0.1:9000`

环境命令：

```bash
npm run dev         # .env.development
npm run dev:test    # .env.test
npm run build:test  # .env.test
npm run build       # .env.production
```

开发和测试环境下，`/member/**` 请求会由 Vite 代理到对应环境文件中
`API_PROXY_TARGET` 配置的网关服务。生产环境默认使用同源 `/`，由部署层
将接口请求反向代理到网关。

## 与老师 Vue CLI 项目的对应关系

| Vue CLI | Vue 3 + Vite |
| --- | --- |
| `vue-cli-service serve --mode dev --port 9000` | `vite --host 0.0.0.0 --mode development` |
| `vue.config.js` | `vite.config.js` |
| `process.env.VUE_APP_SERVER` | `import.meta.env.VITE_API_BASE_URL` |
| `.env.dev` | `.env.development` |
| `.env.test` | `.env.test` |
| `.env.prod` | `.env.production` |
| `public/index.html` | 根目录 `index.html` |

`src/main.js`、`App.vue`、`router/`、`store/` 和 Vue 单文件组件的写法与
老师的 Vue 3 项目保持一致。
