<template>
  <div class="daily-train-seat-page">
    <header class="page-heading">
      <h1>每日座位管理</h1>
      <a-button
        aria-label="刷新每日座位列表"
        title="刷新列表"
        type="text"
        @click="loadDailyTrainSeats()"
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
        width="220px"
      />
      <a-button type="primary" @click="search">
        <SearchOutlined />
        查询
      </a-button>
    </div>

    <section class="daily-train-seat-table">
      <a-table
        :columns="columns"
        :data-source="dailyTrainSeats"
        :loading="loading"
        :pagination="pagination"
        :row-key="(record) => record.id"
        :scroll="{ x: 1010 }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'seatNumber'">
            {{ record.row }}{{ record.col }}
          </template>
          <template v-else-if="column.key === 'col'">
            {{ seatColumnName(record.col, record.seatType) }}
          </template>
          <template v-else-if="column.key === 'seatType'">
            {{ seatTypeName(record.seatType) }}
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
import TrainSelect from '@/components/train-select.vue'
import { queryDailyTrainSeatList } from '@/api/daily-train-seat'

const seatTypes = [
  { code: '1', description: '一等座', columns: ['A', 'C', 'D', 'F'] },
  { code: '2', description: '二等座', columns: ['A', 'B', 'C', 'D', 'F'] },
  { code: '3', description: '软卧', columns: ['A', 'B', 'C', 'D'] },
  { code: '4', description: '硬卧', columns: ['A', 'B', 'C', 'D', 'E', 'F'] },
]
const loading = ref(false)
const dailyTrainSeats = ref([])
const query = reactive({
  date: undefined,
  trainCode: undefined,
})
const pagination = reactive({
  current: 1,
  pageSize: 10,
  pageSizeOptions: ['10', '20', '50'],
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 个每日座位`,
  total: 0,
})
const columns = [
  { title: '日期', dataIndex: 'date', key: 'date', width: 130 },
  { title: '车次编号', dataIndex: 'trainCode', key: 'trainCode', width: 130 },
  { title: '厢序', dataIndex: 'carriageIndex', key: 'carriageIndex', width: 90 },
  { title: '座位号', key: 'seatNumber', width: 100 },
  { title: '排号', dataIndex: 'row', key: 'row', width: 80 },
  { title: '列号', dataIndex: 'col', key: 'col', width: 80 },
  { title: '座位类型', dataIndex: 'seatType', key: 'seatType', width: 120 },
  {
    title: '同车厢座序',
    dataIndex: 'carriageSeatIndex',
    key: 'carriageSeatIndex',
    width: 130,
  },
  { title: '售卖情况', dataIndex: 'sell', key: 'sell', width: 150 },
]

function seatColumnName(code, seatType) {
  const columns = seatTypes.find(
    (item) => item.code === seatType,
  )?.columns
  return columns?.find((item) => item === code) || '-'
}

function seatTypeName(code) {
  return seatTypes.find((item) => item.code === code)?.description || '-'
}

async function loadDailyTrainSeats(
  page = pagination.current,
  pageSize = pagination.pageSize,
) {
  loading.value = true
  try {
    const data = await queryDailyTrainSeatList({
      page,
      size: pageSize,
      date: query.date || undefined,
      trainCode: query.trainCode || undefined,
    })
    if (data.success) {
      dailyTrainSeats.value = data.content?.list || []
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
  loadDailyTrainSeats(1, pagination.pageSize)
}

function handleTableChange(tablePagination) {
  loadDailyTrainSeats(tablePagination.current, tablePagination.pageSize)
}

onMounted(() => {
  loadDailyTrainSeats(1, pagination.pageSize)
})
</script>

<style scoped>
.daily-train-seat-page {
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
  width: 180px;
}

.daily-train-seat-table {
  width: 100%;
}

@media (max-width: 640px) {
  .query-bar {
    align-items: stretch;
    flex-direction: column;
  }

  .query-bar :deep(.ant-picker),
  .query-bar :deep(.ant-select) {
    width: 100% !important;
  }
}
</style>
