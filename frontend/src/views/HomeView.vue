<template>
  <a-layout class="app-shell">
    <a-layout-header class="app-header">
      <span class="brand-mark">铁路票务系统</span>
    </a-layout-header>
    <a-layout-content class="app-content">
      <a-card title="工作台" :bordered="false">
        <a-empty description="暂无数据" />
      </a-card>
    </a-layout-content>
  </a-layout>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
}

.app-header {
  display: flex;
  align-items: center;
  background: #0f766e;
}

.brand-mark {
  color: #ffffff;
  font-size: 18px;
  font-weight: 600;
}

.app-content {
  padding: 32px;
}

@media (max-width: 640px) {
  .app-content {
    padding: 16px;
  }
}
</style>
