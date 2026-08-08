<template>
  <div class="confirm-order-page">
    <header class="page-heading">
      <h1>订单信息</h1>
      <a-button
        aria-label="刷新订单列表"
        title="刷新列表"
        type="text"
        @click="loadOrders()"
      >
        <ReloadOutlined />
      </a-button>
    </header>

    <section class="order-table">
      <a-table
        :columns="columns"
        :data-source="orders"
        :loading="loading"
        :pagination="pagination"
        :row-key="(record) => record.id"
        :scroll="{ x: 1180 }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'tickets'">
            <a-tooltip :title="formatTickets(record.tickets)">
              <span class="tickets-cell">
                {{ formatTickets(record.tickets) }}
              </span>
            </a-tooltip>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="statusMeta(record.status).color">
              {{ statusMeta(record.status).description }}
            </a-tag>
          </template>
        </template>
      </a-table>
    </section>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { notification } from 'ant-design-vue'
import { ReloadOutlined } from '@ant-design/icons-vue'
import { queryConfirmOrderList } from '@/api/confirm-order'

const ORDER_STATUSES = {
  I: { description: '初始', color: 'default' },
  P: { description: '处理中', color: 'processing' },
  S: { description: '成功', color: 'success' },
  F: { description: '失败', color: 'error' },
  E: { description: '无票', color: 'warning' },
  C: { description: '取消', color: 'default' },
}
const UNKNOWN_STATUS = { description: '未知', color: 'default' }
const loading = ref(false)
const orders = ref([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  pageSizeOptions: ['10', '20', '50'],
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条订单`,
  total: 0,
})
const columns = [
  { title: '会员 ID', dataIndex: 'memberId', key: 'memberId', width: 180 },
  { title: '日期', dataIndex: 'date', key: 'date', width: 112 },
  { title: '车次', dataIndex: 'trainCode', key: 'trainCode', width: 82 },
  { title: '出发站', dataIndex: 'start', key: 'start', width: 112 },
  { title: '到达站', dataIndex: 'end', key: 'end', width: 112 },
  {
    title: '余票 ID',
    dataIndex: 'dailyTrainTicketId',
    key: 'dailyTrainTicketId',
    width: 180,
  },
  { title: '车票', dataIndex: 'tickets', key: 'tickets', width: 300 },
  { title: '订单状态', dataIndex: 'status', key: 'status', width: 100 },
]

function statusMeta(status) {
  return ORDER_STATUSES[status] || UNKNOWN_STATUS
}

function formatTickets(value) {
  if (!value) {
    return '-'
  }
  try {
    return JSON.stringify(JSON.parse(value))
  } catch {
    return value
  }
}

async function loadOrders(
  page = pagination.current,
  pageSize = pagination.pageSize,
) {
  loading.value = true
  try {
    const data = await queryConfirmOrderList({
      page,
      size: pageSize,
    })
    if (data.success) {
      orders.value = data.content?.list || []
      pagination.current = page
      pagination.pageSize = pageSize
      pagination.total = data.content?.total || 0
    } else {
      notification.error({ description: data.message || '查询失败' })
    }
  } catch (error) {
    notification.error({
      description: error.response?.data?.message || '查询失败，请稍后再试',
    })
  } finally {
    loading.value = false
  }
}

function handleTableChange(tablePagination) {
  loadOrders(tablePagination.current, tablePagination.pageSize)
}

onMounted(() => {
  loadOrders(1, pagination.pageSize)
})
</script>

<style scoped>
.confirm-order-page {
  color: #1f2927;
}

.page-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-heading h1 {
  margin: 0;
  font-size: 22px;
  line-height: 32px;
}

.order-table {
  width: 100%;
}

.order-table :deep(.ant-table-cell) {
  white-space: nowrap;
}

.tickets-cell {
  display: block;
  overflow: hidden;
  color: #4b5563;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas,
    "Liberation Mono", monospace;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
