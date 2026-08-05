import { createApp } from 'vue'
import Antd from 'ant-design-vue'
import * as Icons from '@ant-design/icons-vue'
import 'ant-design-vue/dist/antd.css'
import App from './App.vue'
import router from './router'
import store from './store'
import './style.css'

const app = createApp(App)

app.use(Antd).use(store).use(router)

Object.entries(Icons).forEach(([name, component]) => {
  app.component(name, component)
})

app.mount('#app')
