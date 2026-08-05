<template>
  <a-layout-sider
    class="admin-sider"
    :collapsed="collapsed"
    :collapsed-width="64"
    :width="208"
    breakpoint="sm"
    theme="light"
    @breakpoint="emit('breakpoint', $event)"
  >
    <a-menu v-model:selectedKeys="activeKeys" mode="inline">
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <HomeOutlined />
          <span>欢迎</span>
        </router-link>
      </a-menu-item>
      <a-menu-item key="/about">
        <router-link to="/about">
          <InfoCircleOutlined />
          <span>关于</span>
        </router-link>
      </a-menu-item>
      <a-menu-item key="/base/station">
        <router-link to="/base/station">
          <EnvironmentOutlined />
          <span>车站管理</span>
        </router-link>
      </a-menu-item>
      <a-menu-item key="/base/train">
        <router-link to="/base/train">
          <CarOutlined />
          <span>车次管理</span>
        </router-link>
      </a-menu-item>
    </a-menu>
  </a-layout-sider>
</template>

<script setup>
import { ref, watch } from 'vue'
import {
  CarOutlined,
  EnvironmentOutlined,
  HomeOutlined,
  InfoCircleOutlined,
} from '@ant-design/icons-vue'
import { useRoute } from 'vue-router'

defineProps({
  collapsed: {
    type: Boolean,
    required: true,
  },
})

const route = useRoute()
const activeKeys = ref([])
const emit = defineEmits(['breakpoint'])

watch(
  () => route.path,
  (path) => {
    activeKeys.value = [path]
  },
  { immediate: true },
)
</script>

<style scoped>
.admin-sider {
  border-right: 1px solid #e5e7eb;
  background: #ffffff;
}

.admin-sider :deep(.ant-menu) {
  padding-top: 12px;
  border-right: 0;
}
</style>
