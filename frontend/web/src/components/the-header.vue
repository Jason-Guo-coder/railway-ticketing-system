<template>
  <a-layout-header class="console-header">
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

      <router-link class="console-brand" to="/welcome">
        <SwapOutlined class="brand-icon" />
        <span>铁路票务系统</span>
      </router-link>
    </div>

    <a-menu
      v-model:selectedKeys="activeKeys"
      class="header-menu"
      mode="horizontal"
      theme="dark"
    >
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <HomeOutlined />
          <span>欢迎</span>
        </router-link>
      </a-menu-item>
      <a-menu-item key="/ticket">
        <router-link to="/ticket">
          <SearchOutlined />
          <span>车票查询</span>
        </router-link>
      </a-menu-item>
      <a-menu-item key="/passenger">
        <router-link to="/passenger">
          <TeamOutlined />
          <span>乘车人管理</span>
        </router-link>
      </a-menu-item>
    </a-menu>

    <div class="header-account">
      <span class="account-mobile">
        <UserOutlined />
        {{ member.mobile }}
      </span>
      <a-button class="logout-button" type="text" @click="logout">
        <LogoutOutlined />
        <span>退出</span>
      </a-button>
    </div>
  </a-layout-header>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import {
  HomeOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  SearchOutlined,
  SwapOutlined,
  TeamOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'

defineProps({
  collapsed: {
    type: Boolean,
    required: true,
  },
})

const emit = defineEmits(['toggle'])
const route = useRoute()
const router = useRouter()
const store = useStore()
const member = computed(() => store.state.member)
const activeKeys = ref([])

watch(
  () => route.path,
  (path) => {
    activeKeys.value = [path]
  },
  { immediate: true },
)

async function logout() {
  store.commit('clearMember')
  await router.push('/login')
}
</script>

<style scoped>
.console-header {
  z-index: 10;
  display: flex;
  height: 64px;
  padding: 0 20px;
  align-items: center;
  justify-content: space-between;
  background: #163f3b;
  box-shadow: 0 1px 0 rgb(255 255 255 / 12%);
}

.header-left,
.header-account,
.console-brand,
.account-mobile {
  display: flex;
  align-items: center;
}

.header-left {
  min-width: 0;
  gap: 8px;
}

.header-menu {
  min-width: 390px;
  margin: 0 24px;
  border-bottom: 0;
  background: transparent;
  line-height: 64px;
}

.header-menu :deep(.ant-menu-item) {
  padding: 0 16px;
}

.header-menu :deep(.ant-menu-item a) {
  display: flex;
  gap: 7px;
  align-items: center;
}

.menu-trigger,
.logout-button {
  min-width: 40px;
  min-height: 40px;
  color: #ffffff;
}

.menu-trigger:hover,
.menu-trigger:focus,
.logout-button:hover,
.logout-button:focus {
  color: #ffffff;
  background: rgb(255 255 255 / 12%);
}

.console-brand {
  gap: 10px;
  color: #ffffff;
  font-size: 18px;
  font-weight: 700;
  white-space: nowrap;
}

.console-brand:hover {
  color: #ffffff;
}

.brand-icon {
  color: #f47b70;
  font-size: 22px;
}

.header-account {
  gap: 16px;
  color: #e5efed;
}

.account-mobile {
  gap: 7px;
  white-space: nowrap;
}

.logout-button {
  display: inline-flex;
  gap: 6px;
  align-items: center;
}

@media (max-width: 767px) {
  .console-header {
    padding: 0 12px;
  }

  .console-brand {
    font-size: 16px;
  }

  .account-mobile {
    display: none;
  }

  .header-account {
    gap: 4px;
  }
}

@media (max-width: 1023px) {
  .header-menu {
    display: none;
  }
}
</style>
