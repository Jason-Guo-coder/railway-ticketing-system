<template>
  <a-layout-header class="admin-header">
    <div class="header-left">
      <a-button
        class="menu-trigger"
        type="text"
        :aria-label="collapsed ? '展开导航' : '收起导航'"
        @click="emit('toggle')"
      >
        <MenuUnfoldOutlined v-if="collapsed" />
        <MenuFoldOutlined v-else />
      </a-button>

      <router-link class="admin-brand" to="/welcome">
        <ControlOutlined />
        <span>铁路票务管理控台</span>
      </router-link>
    </div>

    <div class="header-status">
      <SafetyCertificateOutlined />
      <span>{{ store.state.admin.username || '管理员' }}</span>
      <a-button
        class="logout-button"
        type="text"
        aria-label="退出登录"
        title="退出登录"
        @click="logout"
      >
        <LogoutOutlined />
      </a-button>
    </div>
  </a-layout-header>
</template>

<script setup>
import {
  ControlOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  SafetyCertificateOutlined,
} from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

const router = useRouter()
const store = useStore()

function logout() {
  store.commit('clearAdmin')
  router.push('/login')
}

defineProps({
  collapsed: {
    type: Boolean,
    required: true,
  },
})

const emit = defineEmits(['toggle'])
</script>

<style scoped>
.admin-header {
  z-index: 10;
  display: flex;
  height: 64px;
  padding: 0 20px;
  align-items: center;
  justify-content: space-between;
  background: #173f3b;
  box-shadow: 0 1px 0 rgb(255 255 255 / 12%);
}

.header-left,
.admin-brand,
.header-status {
  display: flex;
  align-items: center;
}

.header-left {
  min-width: 0;
  gap: 8px;
}

.menu-trigger {
  width: 40px;
  height: 40px;
  color: #ffffff;
}

.admin-brand {
  gap: 10px;
  color: #ffffff;
  font-size: 18px;
  font-weight: 600;
}

.admin-brand:hover {
  color: #ffffff;
}

.header-status {
  gap: 8px;
  color: rgb(255 255 255 / 82%);
}

.logout-button {
  width: 36px;
  height: 36px;
  color: rgb(255 255 255 / 82%);
}

.logout-button:hover,
.logout-button:focus {
  color: #ffffff;
}

@media (max-width: 640px) {
  .admin-header {
    padding: 0 12px;
  }

  .header-status {
    display: none;
  }
}
</style>
