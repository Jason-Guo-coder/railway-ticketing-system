<template>
  <a-layout-sider
    class="console-sider"
    :collapsed="collapsed"
    :collapsed-width="0"
    :trigger="null"
    breakpoint="lg"
    width="216"
    @breakpoint="emit('update:collapsed', $event)"
  >
    <a-menu
      v-model:selectedKeys="activeKeys"
      mode="inline"
      @click="handleMenuClick"
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
  </a-layout-sider>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { HomeOutlined, SearchOutlined, TeamOutlined } from '@ant-design/icons-vue'

defineProps({
  collapsed: {
    type: Boolean,
    required: true,
  },
})

const emit = defineEmits(['update:collapsed'])
const route = useRoute()
const activeKeys = ref([])

watch(
  () => route.path,
  (path) => {
    activeKeys.value = [path]
  },
  { immediate: true },
)

function handleMenuClick() {
  if (window.innerWidth < 992) {
    emit('update:collapsed', true)
  }
}
</script>

<style scoped>
.console-sider {
  border-right: 1px solid #dce3e1;
  background: #ffffff;
}

.console-sider :deep(.ant-layout-sider-children),
.console-sider :deep(.ant-menu) {
  height: 100%;
  background: #ffffff;
}

.console-sider :deep(.ant-menu) {
  padding-top: 12px;
  border-right: 0;
}

.console-sider :deep(.ant-menu-item) {
  width: auto;
  height: 44px;
  margin: 4px 10px;
  line-height: 44px;
  border-radius: 6px;
}

.console-sider :deep(.ant-menu-item-selected) {
  color: #126f66;
  background: #e8f3f1;
}
</style>
