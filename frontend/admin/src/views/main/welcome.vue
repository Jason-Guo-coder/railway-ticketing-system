<template>
  <section class="welcome-page">
    <div class="welcome-mark" aria-hidden="true">
      <DashboardOutlined />
    </div>
    <p class="page-label">ADMIN CONSOLE</p>
    <h1>欢迎使用铁路票务管理控台</h1>
    <p class="welcome-date">{{ currentDate }}</p>
  </section>
</template>

<script setup>
import { DashboardOutlined } from '@ant-design/icons-vue'

const currentDate = new Intl.DateTimeFormat('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long',
}).format(new Date())
</script>

<style scoped>
.welcome-page {
  display: flex;
  min-height: calc(100vh - 120px);
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  border-top: 3px solid #d1493f;
}

.welcome-mark {
  display: grid;
  width: 52px;
  height: 52px;
  margin-bottom: 24px;
  place-items: center;
  border-radius: 6px;
  background: #173f3b;
  color: #ffffff;
  font-size: 25px;
}

.page-label {
  margin: 0 0 12px;
  color: #69716f;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0;
}

h1 {
  max-width: 720px;
  margin: 0;
  color: #17201e;
  font-size: 36px;
  line-height: 1.3;
}

.welcome-date {
  margin: 18px 0 0;
  color: #69716f;
  font-size: 15px;
}

@media (max-width: 640px) {
  h1 {
    font-size: 28px;
  }
}
</style>
