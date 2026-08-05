<template>
  <a-layout class="console-shell">
    <the-header :collapsed="collapsed" @toggle="collapsed = !collapsed" />

    <a-layout class="console-body">
      <the-sider v-model:collapsed="collapsed" />

      <a-layout class="console-main">
        <a-layout-content class="console-content">
          <router-view />
        </a-layout-content>
        <a-layout-footer class="console-footer">
          铁路票务系统
        </a-layout-footer>
      </a-layout>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref } from 'vue'
import TheHeader from '@/components/the-header.vue'
import TheSider from '@/components/the-sider.vue'

const collapsed = ref(false)
</script>

<style scoped>
.console-shell {
  min-height: 100vh;
  background: #f3f5f4;
}

.console-body {
  min-height: calc(100vh - 64px);
}

.console-main {
  min-width: 0;
  background: #f3f5f4;
}

.console-content {
  width: min(1180px, 100%);
  margin: 0 auto;
  padding: 24px;
}

.console-footer {
  padding: 18px 24px 22px;
  color: #73807d;
  background: transparent;
  text-align: center;
}

@media (max-width: 767px) {
  .console-content {
    padding: 16px;
  }
}
</style>
