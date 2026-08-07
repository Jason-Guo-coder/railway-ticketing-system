<template>
  <div class="daily-train-ticket-page">
    <header class="page-heading">
      <h1>余票管理</h1>
      <a-button
        aria-label="刷新余票列表"
        title="刷新列表"
        type="text"
        @click="loadTickets()"
      >
        <ReloadOutlined />
      </a-button>
    </header>

    <div class="query-bar">
      <a-date-picker
        v-model:value="query.date"
        allow-clear
        placeholder="全部日期"
        value-format="YYYY-MM-DD"
      />
      <TrainSelect
        v-model="query.trainCode"
        placeholder="全部车次"
        width="180px"
      />
      <StationSelect
        v-model="query.start"
        placeholder="全部出发站"
        width="180px"
      />
      <StationSelect
        v-model="query.end"
        placeholder="全部到达站"
        width="180px"
      />
      <a-button type="primary" @click="search">
        <SearchOutlined />
        查询
      </a-button>
    </div>

    <section class="ticket-table">
      <a-table
        :columns="columns"
        :data-source="tickets"
        :loading="loading"
        :pagination="pagination"
        :row-key="(record) => record.id"
        :scroll="{ x: 900 }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'station'">
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
    </section>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { notification } from 'ant-design-vue'
import {
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons-vue'
import StationSelect from '@/components/station-select.vue'
import TrainSelect from '@/components/train-select.vue'
import { queryDailyTrainTicketList } from '@/api/daily-train-ticket'

const loading = ref(false)
const tickets = ref([])
const seatColumnKeys = ['ydz', 'edz', 'rw', 'yw']
const query = reactive({
  date: undefined,
  trainCode: undefined,
  start: undefined,
  end: undefined,
})
const pagination = reactive({
  current: 1,
  pageSize: 10,
  pageSizeOptions: ['10', '20', '50'],
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条余票信息`,
  total: 0,
})
const columns = [
  { title: '日期', dataIndex: 'date', key: 'date', width: 112 },
  { title: '车次', dataIndex: 'trainCode', key: 'trainCode', width: 82 },
  { title: '出发 / 到达', key: 'station', width: 120 },
  { title: '时间', key: 'time', width: 96 },
  { title: '历时', key: 'duration', width: 96 },
  { title: '一等座', key: 'ydz', width: 96 },
  { title: '二等座', key: 'edz', width: 96 },
  { title: '软卧', key: 'rw', width: 96 },
  { title: '硬卧', key: 'yw', width: 96 },
]

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

async function loadTickets(
  page = pagination.current,
  pageSize = pagination.pageSize,
) {
  loading.value = true
  try {
    const data = await queryDailyTrainTicketList({
      page,
      size: pageSize,
      date: query.date || undefined,
      trainCode: query.trainCode || undefined,
      start: query.start || undefined,
      end: query.end || undefined,
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

onMounted(() => {
  loadTickets(1, pagination.pageSize)
})
</script>

<style scoped>
.daily-train-ticket-page {
  color: #1f2927;
}

.page-heading,
.query-bar {
  display: flex;
  align-items: center;
}

.page-heading {
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-heading h1 {
  margin: 0;
  font-size: 22px;
  line-height: 32px;
}

.query-bar {
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.query-bar :deep(.ant-picker) {
  width: 160px;
}

.ticket-table {
  width: 100%;
}

.cell-primary,
.cell-secondary,
.seat-count,
.seat-price {
  line-height: 22px;
  white-space: nowrap;
}

.cell-secondary,
.seat-price,
.unavailable {
  color: #6b7280;
}

.seat-price {
  font-variant-numeric: tabular-nums;
}

@media (max-width: 640px) {
  .query-bar {
    align-items: stretch;
    flex-direction: column;
  }

  .query-bar > *,
  .query-bar :deep(.ant-picker),
  .query-bar :deep(.ant-select) {
    width: 100% !important;
  }
}
</style>
