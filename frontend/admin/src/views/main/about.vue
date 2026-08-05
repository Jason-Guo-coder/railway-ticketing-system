<template>
  <section class="about-page">
    <p class="page-label">ABOUT</p>
    <h1>铁路票务系统</h1>
    <a-descriptions class="system-info" :column="1" bordered>
      <a-descriptions-item label="应用">管理控台</a-descriptions-item>
      <a-descriptions-item label="前端">Vue 3 + Vite</a-descriptions-item>
      <a-descriptions-item label="组件库">Ant Design Vue</a-descriptions-item>
    </a-descriptions>
  </section>
</template>

<style scoped>
.about-page {
  max-width: 720px;
}

.page-label {
  margin: 0 0 8px;
  color: #69716f;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0;
}

h1 {
  margin: 0 0 24px;
  color: #17201e;
  font-size: 28px;
}

.system-info {
  background: #ffffff;
}
</style>
