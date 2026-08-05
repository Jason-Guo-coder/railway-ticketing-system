<template>
  <main class="login-page">
    <section class="login-panel">
      <header class="login-heading">
        <ControlOutlined />
        <h1>管理控台登录</h1>
      </header>

      <a-form :model="loginForm" layout="vertical" @finish="login">
        <a-form-item
          label="管理员账号"
          name="username"
          :rules="[{ required: true, message: '请输入管理员账号' }]"
        >
          <a-input
            v-model:value="loginForm.username"
            autocomplete="username"
            placeholder="请输入管理员账号"
            size="large"
          >
            <template #prefix><UserOutlined /></template>
          </a-input>
        </a-form-item>

        <a-form-item
          label="管理员密码"
          name="password"
          :rules="[{ required: true, message: '请输入管理员密码' }]"
        >
          <a-input-password
            v-model:value="loginForm.password"
            autocomplete="current-password"
            placeholder="请输入管理员密码"
            size="large"
          >
            <template #prefix><LockOutlined /></template>
          </a-input-password>
        </a-form-item>

        <a-button
          block
          html-type="submit"
          :loading="submitting"
          size="large"
          type="primary"
        >
          <LoginOutlined />
          登录
        </a-button>
      </a-form>
    </section>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { notification } from 'ant-design-vue'
import {
  ControlOutlined,
  LockOutlined,
  LoginOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { loginAdmin } from '@/api/admin'

const route = useRoute()
const router = useRouter()
const store = useStore()
const submitting = ref(false)
const loginForm = reactive({
  username: 'admin',
  password: '',
})

async function login() {
  submitting.value = true
  try {
    const data = await loginAdmin(loginForm)
    if (data.success) {
      store.commit('setAdmin', data.content)
      const redirect = typeof route.query.redirect === 'string'
        ? route.query.redirect
        : '/welcome'
      await router.push(redirect)
    } else {
      notification.error({ description: data.message || '登录失败' })
    }
  } catch (error) {
    notification.error({
      description: error.response?.data?.message || '登录失败，请稍后再试',
    })
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: grid;
  min-height: 100vh;
  padding: 24px;
  place-items: center;
  background: #eef2f1;
}

.login-panel {
  width: min(100%, 400px);
  padding: 32px;
  border: 1px solid #d7dfdc;
  border-top: 3px solid #d1493f;
  border-radius: 6px;
  background: #ffffff;
}

.login-heading {
  display: flex;
  margin-bottom: 28px;
  gap: 12px;
  align-items: center;
  color: #173f3b;
}

.login-heading > :first-child {
  font-size: 28px;
}

.login-heading h1 {
  margin: 0;
  color: #17201e;
  font-size: 24px;
}

.login-panel :deep(.ant-btn-primary) {
  border-color: #147d72;
  background: #147d72;
}

@media (max-width: 480px) {
  .login-page {
    padding: 16px;
  }

  .login-panel {
    padding: 24px 20px;
  }
}
</style>
