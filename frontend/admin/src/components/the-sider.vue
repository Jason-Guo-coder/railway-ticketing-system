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
    <a-menu
      v-model:openKeys="openKeys"
      v-model:selectedKeys="activeKeys"
      mode="inline"
    >
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
      <a-sub-menu key="base">
        <template #title>
          <DatabaseOutlined />
          <span>基础数据</span>
        </template>
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
        <a-menu-item key="/base/train-station">
          <router-link to="/base/train-station">
            <NodeIndexOutlined />
            <span>车次车站</span>
          </router-link>
        </a-menu-item>
        <a-menu-item key="/base/train-carriage">
          <router-link to="/base/train-carriage">
            <AppstoreOutlined />
            <span>车厢管理</span>
          </router-link>
        </a-menu-item>
        <a-menu-item key="/base/train-seat">
          <router-link to="/base/train-seat">
            <TableOutlined />
            <span>座位管理</span>
          </router-link>
        </a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="business">
        <template #title>
          <ProfileOutlined />
          <span>业务管理</span>
        </template>
        <a-menu-item key="/business/daily-train">
          <router-link to="/business/daily-train">
            <CalendarOutlined />
            <span>每日车次</span>
          </router-link>
        </a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="batch">
        <template #title>
          <ClockCircleOutlined />
          <span>跑批管理</span>
        </template>
        <a-menu-item key="/batch/job">
          <router-link to="/batch/job">
            <ClockCircleOutlined />
            <span>定时任务</span>
          </router-link>
        </a-menu-item>
      </a-sub-menu>
    </a-menu>
  </a-layout-sider>
</template>

<script setup>
import { ref, watch } from 'vue'
import {
  AppstoreOutlined,
  CalendarOutlined,
  CarOutlined,
  ClockCircleOutlined,
  DatabaseOutlined,
  EnvironmentOutlined,
  HomeOutlined,
  InfoCircleOutlined,
  NodeIndexOutlined,
  ProfileOutlined,
  TableOutlined,
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
const openKeys = ref(['base', 'business', 'batch'])
const emit = defineEmits(['breakpoint'])

watch(
  () => route.path,
  (path) => {
    activeKeys.value = [path]
    const section = path.split('/')[1]
    if (['base', 'business', 'batch'].includes(section)
        && !openKeys.value.includes(section)) {
      openKeys.value = [...openKeys.value, section]
    }
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
