<template>
  <div class="ticket-page">
    <header class="page-heading">
      <h1>余票查询</h1>
    </header>

    <a-form class="query-bar" layout="vertical" @finish="search">
      <a-form-item label="出发日期" required>
        <a-date-picker
          v-model:value="query.date"
          :disabled-date="disabledDate"
          placeholder="请选择出发日期"
          value-format="YYYY-MM-DD"
        />
      </a-form-item>
      <a-form-item label="出发地" required>
        <StationSelect
          v-model="query.start"
          placeholder="请选择出发地"
        />
      </a-form-item>
      <SwapRightOutlined class="direction-icon" />
      <a-form-item label="目的地" required>
        <StationSelect
          v-model="query.end"
          placeholder="请选择目的地"
        />
      </a-form-item>
      <a-button :loading="loading" html-type="submit" type="primary">
        <SearchOutlined />
        查询
      </a-button>
    </a-form>

    <section class="result-section">
      <div class="result-heading">
        <h2>查询结果</h2>
        <span v-if="searched">共 {{ pagination.total }} 个车次区间</span>
      </div>

      <a-table
        v-if="searched"
        :columns="columns"
        :data-source="tickets"
        :loading="loading"
        :pagination="pagination"
        :row-key="(record) => record.id"
        :scroll="{ x: 920 }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'operation'">
            <a-button type="primary" @click="toOrder(record)">
              <ShoppingCartOutlined />
              预订
            </a-button>
          </template>
          <template v-else-if="column.key === 'station'">
            <div class="cell-primary">{{ record.start }}</div>
            <div class="cell-secondary">{{ record.end }}</div>
          </template>
          <template v-else-if="column.key === 'time'">
            <div class="cell-primary">{{ formatTime(record.startTime) }}</div>
            <div class="cell-secondary">{{ formatTime(record.endTime) }}</div>
          </template>
          <template v-else-if="column.key === 'duration'">
            <div class="cell-primary">
              {{ durationText(record.startTime, record.endTime) }}
            </div>
            <div class="cell-secondary">
              {{ arrivalDay(record.startTime, record.endTime) }}
            </div>
          </template>
          <template v-else-if="seatColumnKeys.includes(column.key)">
            <template v-if="record[column.key] >= 0">
              <div class="seat-count">{{ record[column.key] }} 张</div>
              <div class="seat-price">
                ￥{{ formatPrice(record[`${column.key}Price`]) }}
              </div>
            </template>
            <span v-else class="unavailable">--</span>
          </template>
        </template>
      </a-table>

      <a-empty v-else description="请选择出发日期和车站后查询" />
    </section>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { notification } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import {
  SearchOutlined,
  ShoppingCartOutlined,
  SwapRightOutlined,
} from '@ant-design/icons-vue'
import StationSelect from '@/components/station-select.vue'
import { queryTickets } from '@/api/ticket'
import {
  getSession,
  SESSION_ORDER,
  SESSION_TICKET_QUERY,
  setSession,
} from '@/utils/session-storage'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const searched = ref(false)
const tickets = ref([])
const seatColumnKeys = ['ydz', 'edz', 'rw', 'yw']
const cachedQuery = getSession(SESSION_TICKET_QUERY, {})
const query = reactive({
  date: route.query.date || cachedQuery.date || undefined,
  start: route.query.departure || cachedQuery.start || undefined,
  end: route.query.arrival || cachedQuery.end || undefined,
})
const pagination = reactive({
  current: 1,
  pageSize: 10,
  pageSizeOptions: ['10', '20', '50'],
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 个车次区间`,
  total: 0,
})
const columns = [
  { title: '车次', dataIndex: 'trainCode', key: 'trainCode', width: 82 },
  { title: '出发 / 到达', key: 'station', width: 120 },
  { title: '时间', key: 'time', width: 96 },
  { title: '历时', key: 'duration', width: 96 },
  { title: '一等座', key: 'ydz', width: 102 },
  { title: '二等座', key: 'edz', width: 102 },
  { title: '软卧', key: 'rw', width: 102 },
  { title: '硬卧', key: 'yw', width: 102 },
  { title: '操作', key: 'operation', width: 96 },
]

function validateQuery() {
  if (!query.date) {
    notification.warning({ description: '请选择出发日期' })
    return false
  }
  if (!query.start) {
    notification.warning({ description: '请选择出发地' })
    return false
  }
  if (!query.end) {
    notification.warning({ description: '请选择目的地' })
    return false
  }
  if (query.start === query.end) {
    notification.warning({ description: '出发地和目的地不能相同' })
    return false
  }
  return true
}

async function loadTickets(
  page = pagination.current,
  pageSize = pagination.pageSize,
) {
  if (!validateQuery()) {
    return
  }

  setSession(SESSION_TICKET_QUERY, {
    date: query.date,
    start: query.start,
    end: query.end,
  })
  searched.value = true
  loading.value = true
  try {
    const data = await queryTickets({
      page,
      size: pageSize,
      date: query.date,
      start: query.start,
      end: query.end,
    })
    if (data.success) {
      tickets.value = data.content?.list || []
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

function search() {
  loadTickets(1, pagination.pageSize)
}

function handleTableChange(tablePagination) {
  loadTickets(tablePagination.current, tablePagination.pageSize)
}

function toOrder(record) {
  setSession(SESSION_ORDER, record)
  router.push('/order')
}

function formatTime(value) {
  return value ? value.slice(0, 5) : '-'
}

function formatPrice(value) {
  const price = Number(value)
  return Number.isFinite(price) ? price.toFixed(2) : '-'
}

function timeToSeconds(value) {
  if (!value) {
    return null
  }
  const [hour, minute, second = 0] = value.split(':').map(Number)
  return hour * 3600 + minute * 60 + second
}

function durationSeconds(startTime, endTime) {
  const start = timeToSeconds(startTime)
  const end = timeToSeconds(endTime)
  if (start === null || end === null) {
    return null
  }
  return end <= start ? end + 24 * 3600 - start : end - start
}

function durationText(startTime, endTime) {
  const seconds = durationSeconds(startTime, endTime)
  if (seconds === null) {
    return '-'
  }
  const hour = Math.floor(seconds / 3600)
  const minute = Math.floor(seconds % 3600 / 60)
  return `${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`
}

function arrivalDay(startTime, endTime) {
  const start = timeToSeconds(startTime)
  const end = timeToSeconds(endTime)
  if (start === null || end === null) {
    return '-'
  }
  return end <= start ? '次日到达' : '当日到达'
}

function disabledDate(date) {
  return date && date.endOf('day').valueOf() < Date.now()
}

onMounted(() => {
  if (query.date && query.start && query.end) {
    loadTickets(1, pagination.pageSize)
  }
})
</script>

<style scoped>
.ticket-page {
  color: #1f2927;
}

.page-heading {
  margin-bottom: 18px;
}

.page-heading h1,
.result-heading h2 {
  margin: 0;
  color: #17211f;
  font-weight: 700;
}

.page-heading h1 {
  font-size: 24px;
}

.query-bar {
  display: grid;
  padding: 18px 0 22px;
  border-top: 1px solid #dce3e1;
  border-bottom: 1px solid #dce3e1;
  align-items: end;
  gap: 12px;
  grid-template-columns: 180px minmax(180px, 1fr) 24px minmax(180px, 1fr) 96px;
}

.query-bar :deep(.ant-form-item) {
  margin: 0;
}

.query-bar :deep(.ant-picker) {
  width: 100%;
}

.query-bar .ant-btn-primary {
  border-color: #147d72;
  background: #147d72;
}

.direction-icon {
  margin-bottom: 9px;
  color: #73807d;
  font-size: 18px;
  text-align: center;
}

.result-section {
  margin-top: 24px;
}

.result-heading {
  display: flex;
  margin-bottom: 16px;
  align-items: center;
  justify-content: space-between;
}

.result-heading h2 {
  font-size: 17px;
}

.result-heading span,
.cell-secondary,
.seat-price,
.unavailable {
  color: #6a7774;
}

.cell-primary,
.cell-secondary,
.seat-count,
.seat-price {
  line-height: 22px;
  white-space: nowrap;
}

.seat-price {
  font-variant-numeric: tabular-nums;
}

@media (max-width: 880px) {
  .query-bar {
    grid-template-columns: 1fr 1fr;
  }

  .direction-icon {
    display: none;
  }
}

@media (max-width: 600px) {
  .query-bar {
    grid-template-columns: 1fr;
  }

  .query-bar .ant-btn-primary {
    width: 100%;
  }
}
</style>
