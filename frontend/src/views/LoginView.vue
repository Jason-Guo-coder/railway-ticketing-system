<template>
  <div class="auth-page">
    <main class="auth-panel">
      <header class="auth-heading">
        <RocketTwoTone two-tone-color="#c2413b" class="auth-logo" />
        <p>铁路票务系统</p>
        <h1>会员登录</h1>
      </header>

      <a-form :model="loginForm" layout="vertical" @finish="handleLogin">
        <a-form-item
          label="手机号"
          name="mobile"
          :rules="mobileRules"
        >
          <a-input
            v-model:value="loginForm.mobile"
            aria-label="手机号"
            autocomplete="tel"
            :maxlength="11"
            placeholder="请输入手机号"
            size="large"
          >
            <template #prefix><MobileOutlined /></template>
          </a-input>
        </a-form-item>

        <a-form-item
          label="验证码"
          name="code"
          :rules="[{ required: true, message: '请输入验证码' }]"
        >
          <a-input
            v-model:value="loginForm.code"
            aria-label="验证码"
            autocomplete="one-time-code"
            :maxlength="6"
            placeholder="请输入验证码"
            size="large"
          >
            <template #prefix><SafetyCertificateOutlined /></template>
            <template #addonAfter>
              <button
                class="code-button"
                type="button"
                :disabled="sendingCode"
                @click="handleSendCode"
              >
                {{ sendingCode ? '发送中' : '获取验证码' }}
              </button>
            </template>
          </a-input>
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

    </main>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { notification } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import {
  LoginOutlined,
  MobileOutlined,
  RocketTwoTone,
  SafetyCertificateOutlined,
} from '@ant-design/icons-vue'
import { loginMember, sendLoginCode } from '@/api/member'

const router = useRouter()
const route = useRoute()
const store = useStore()
const submitting = ref(false)
const sendingCode = ref(false)
const mobilePattern = /^1[3-9]\d{9}$/
const mobileRules = [
  { required: true, message: '请输入手机号' },
  { pattern: mobilePattern, message: '请输入正确的11位手机号' },
]
const loginForm = reactive({
  mobile: '',
  code: '',
})

const errorMessage = (error) =>
  error.response?.data?.message || '请求失败，请稍后再试'

async function handleSendCode() {
  if (!mobilePattern.test(loginForm.mobile)) {
    notification.warning({ description: '请先输入正确的手机号' })
    return
  }

  sendingCode.value = true
  try {
    const data = await sendLoginCode(loginForm.mobile)
    if (data.success) {
      notification.success({ description: '验证码发送成功' })
    } else {
      notification.error({ description: data.message })
    }
  } catch (error) {
    notification.error({ description: errorMessage(error) })
  } finally {
    sendingCode.value = false
  }
}

async function handleLogin() {
  submitting.value = true
  try {
    const data = await loginMember(loginForm)
    if (data.success) {
      store.commit('setMember', data.content)
      notification.success({ description: '登录成功' })
      const redirect = typeof route.query.redirect === 'string'
        ? route.query.redirect
        : '/home'
      await router.push(redirect)
    } else {
      notification.error({ description: data.message })
    }
  } catch (error) {
    notification.error({ description: errorMessage(error) })
  } finally {
    submitting.value = false
  }
}
</script>
