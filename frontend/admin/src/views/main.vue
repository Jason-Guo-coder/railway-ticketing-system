<template>
  <a-layout class="admin-shell">
    <the-header :collapsed="collapsed" @toggle="collapsed = !collapsed" />
    <a-layout class="admin-body">
      <the-sider
        :collapsed="collapsed"
        @breakpoint="collapsed = $event"
      />
      <a-layout-content class="admin-content">
        <router-view />
      </a-layout-content>
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
.admin-shell {
  min-height: 100vh;
}

.admin-body {
  min-height: calc(100vh - 64px);
}

.admin-content {
  min-width: 0;
  padding: 28px 32px;
  background: #f4f6f5;
}

@media (max-width: 640px) {
  .admin-content {
    padding: 20px 16px;
  }
}
</style>
