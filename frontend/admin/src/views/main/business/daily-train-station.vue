<template>
  <div class="daily-train-station-page">
    <header class="page-heading">
      <h1>每日车站管理</h1>
      <div class="toolbar-actions">
        <a-button
          aria-label="刷新每日车站列表"
          title="刷新列表"
          type="text"
          @click="loadDailyTrainStations()"
        >
          <ReloadOutlined />
        </a-button>
        <a-button type="primary" @click="openAddModal">
          <PlusOutlined />
          新增每日车站
        </a-button>
      </div>
    </header>

    <section class="daily-train-station-table">
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

      <a-table
        :columns="columns"
        :data-source="dailyTrainStations"
        :loading="loading"
        :pagination="pagination"
        :row-key="(record) => record.id"
        :scroll="{ x: 1240 }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="timeColumns.includes(column.key)">
            {{ formatTime(record[column.dataIndex]) }}
          </template>
          <template v-else-if="column.key === 'km'">
            {{ formatKm(record.km) }}
          </template>
          <template v-else-if="column.key === 'operation'">
            <div class="row-actions">
              <a-button type="link" @click="openEditModal(record)">
                编辑
              </a-button>
              <a-popconfirm
                cancel-text="取消"
                ok-text="确认"
                title="删除后不可恢复，确认删除吗？"
                @confirm="removeDailyTrainStation(record.id)"
              >
                <a-button danger type="link">删除</a-button>
              </a-popconfirm>
            </div>
          </template>
        </template>
      </a-table>
    </section>

    <a-modal
      v-model:visible="modalVisible"
      :confirm-loading="saving"
      :title="dailyTrainStation.id ? '编辑每日车站' : '新增每日车站'"
      cancel-text="取消"
      ok-text="保存"
      width="620px"
      @cancel="resetForm"
      @ok="save"
    >
      <a-form
        ref="formRef"
        :label-col="{ span: 6 }"
        :model="dailyTrainStation"
        :rules="rules"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="日期" name="date">
          <a-date-picker
            v-model:value="dailyTrainStation.date"
            placeholder="请选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </a-form-item>
        <a-form-item label="车次编号" name="trainCode">
          <TrainSelect v-model="dailyTrainStation.trainCode" />
        </a-form-item>
        <a-form-item label="站序" name="index">
          <a-input-number
            v-model:value="dailyTrainStation.index"
            :min="1"
            :precision="0"
            placeholder="请输入站序"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="站名" name="name">
          <StationSelect
            v-model="dailyTrainStation.name"
            @change="selectStation"
          />
        </a-form-item>
        <a-form-item label="站名拼音" name="namePinyin">
          <a-input v-model:value="dailyTrainStation.namePinyin" disabled />
        </a-form-item>
        <a-form-item label="进站时间" name="inTime">
          <a-time-picker
            v-model:value="dailyTrainStation.inTime"
            allow-clear
            format="HH:mm"
            placeholder="首站可不填"
            style="width: 100%"
            value-format="HH:mm:ss"
          />
        </a-form-item>
        <a-form-item label="出站时间" name="outTime">
          <a-time-picker
            v-model:value="dailyTrainStation.outTime"
            allow-clear
            format="HH:mm"
            placeholder="末站可不填"
            style="width: 100%"
            value-format="HH:mm:ss"
          />
        </a-form-item>
        <a-form-item label="停站时长" name="stopTime">
          <a-input
            v-model:value="dailyTrainStation.stopTime"
            disabled
            placeholder="根据进出站时间自动计算"
          />
        </a-form-item>
        <a-form-item label="里程（公里）" name="km">
          <a-input-number
            v-model:value="dailyTrainStation.km"
            :max="999999.99"
            :min="0"
            :precision="2"
            placeholder="请输入里程"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { notification } from 'ant-design-vue'
import {
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons-vue'
import StationSelect from '@/components/station-select.vue'
import TrainSelect from '@/components/train-select.vue'
import {
  deleteDailyTrainStation,
  queryDailyTrainStationList,
  saveDailyTrainStation,
  updateDailyTrainStation,
} from '@/api/daily-train-station'

const timeColumns = ['inTime', 'outTime', 'stopTime']
const formRef = ref()
const modalVisible = ref(false)
const saving = ref(false)
const loading = ref(false)
const dailyTrainStations = ref([])
const query = reactive({
  date: undefined,
  trainCode: undefined,
})
const pagination = reactive({
  current: 1,
  pageSize: 10,
  pageSizeOptions: ['10', '20', '50'],
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 个每日车站`,
  total: 0,
})
const dailyTrainStation = reactive({
  date: undefined,
  trainCode: undefined,
  index: undefined,
  name: undefined,
  namePinyin: '',
  inTime: undefined,
  outTime: undefined,
  stopTime: undefined,
  km: undefined,
})
const rules = {
  date: [{ required: true, message: '请选择日期' }],
  trainCode: [{ required: true, message: '请选择车次' }],
  index: [{ required: true, message: '请输入站序' }],
  name: [{ required: true, message: '请选择车站' }],
  namePinyin: [{ required: true, message: '站名拼音不能为空' }],
  km: [{ required: true, message: '请输入里程' }],
}
const columns = [
  { title: '日期', dataIndex: 'date', key: 'date', width: 130 },
  { title: '车次编号', dataIndex: 'trainCode', key: 'trainCode', width: 120 },
  { title: '站序', dataIndex: 'index', key: 'index', width: 80 },
  { title: '站名', dataIndex: 'name', key: 'name', width: 130 },
  {
    title: '站名拼音',
    dataIndex: 'namePinyin',
    key: 'namePinyin',
    width: 180,
  },
  { title: '进站时间', dataIndex: 'inTime', key: 'inTime', width: 110 },
  { title: '出站时间', dataIndex: 'outTime', key: 'outTime', width: 110 },
  { title: '停站时长', dataIndex: 'stopTime', key: 'stopTime', width: 110 },
  { title: '里程（公里）', dataIndex: 'km', key: 'km', width: 130 },
  { title: '操作', key: 'operation', fixed: 'right', width: 140 },
]

watch(
  () => [dailyTrainStation.inTime, dailyTrainStation.outTime],
  ([inTime, outTime]) => {
    dailyTrainStation.stopTime = calculateStopTime(inTime, outTime)
  },
)

function calculateStopTime(inTime, outTime) {
  if (!inTime || !outTime) {
    return undefined
  }

  //1. 将进出站时间转换为当日秒数
  const toSeconds = (value) => {
    const [hour, minute, second] = value.split(':').map(Number)
    return hour * 3600 + minute * 60 + second
  }
  let seconds = toSeconds(outTime) - toSeconds(inTime)

  //2. 出站时间小于进站时间时按跨天处理
  if (seconds < 0) {
    seconds += 24 * 3600
  }

  //3. 将停站秒数格式化为后端 TIME 字段需要的格式
  const hour = String(Math.floor(seconds / 3600)).padStart(2, '0')
  const minute = String(Math.floor((seconds % 3600) / 60)).padStart(2, '0')
  const second = String(seconds % 60).padStart(2, '0')
  return `${hour}:${minute}:${second}`
}

function formatTime(value) {
  return value ? value.slice(0, 5) : '-'
}

function formatKm(value) {
  return value === null || value === undefined
    ? '-'
    : Number(value).toFixed(2)
}

async function loadDailyTrainStations(
  page = pagination.current,
  pageSize = pagination.pageSize,
) {
  loading.value = true
  try {
    const data = await queryDailyTrainStationList({
      page,
      size: pageSize,
      date: query.date || undefined,
      trainCode: query.trainCode || undefined,
    })
    if (data.success) {
      dailyTrainStations.value = data.content?.list || []
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
  loadDailyTrainStations(1, pagination.pageSize)
}

function handleTableChange(tablePagination) {
  loadDailyTrainStations(
    tablePagination.current,
    tablePagination.pageSize,
  )
}

function selectStation(station) {
  dailyTrainStation.namePinyin = station?.namePinyin || ''
}

function resetForm() {
  dailyTrainStation.id = undefined
  dailyTrainStation.date = undefined
  dailyTrainStation.trainCode = undefined
  dailyTrainStation.index = undefined
  dailyTrainStation.name = undefined
  dailyTrainStation.namePinyin = ''
  dailyTrainStation.inTime = undefined
  dailyTrainStation.outTime = undefined
  dailyTrainStation.stopTime = undefined
  dailyTrainStation.km = undefined
  formRef.value?.clearValidate()
}

function openAddModal() {
  resetForm()
  dailyTrainStation.date = query.date
  dailyTrainStation.trainCode = query.trainCode
  modalVisible.value = true
}

function openEditModal(record) {
  dailyTrainStation.id = record.id
  dailyTrainStation.date = record.date
  dailyTrainStation.trainCode = record.trainCode
  dailyTrainStation.index = record.index
  dailyTrainStation.name = record.name
  dailyTrainStation.namePinyin = record.namePinyin
  dailyTrainStation.inTime = record.inTime
  dailyTrainStation.outTime = record.outTime
  dailyTrainStation.stopTime = record.stopTime
  dailyTrainStation.km = Number(record.km)
  modalVisible.value = true
}

async function removeDailyTrainStation(id) {
  try {
    const data = await deleteDailyTrainStation(id)
    if (data.success) {
      notification.success({ description: '删除成功' })
      const lastPage = Math.max(
        1,
        Math.ceil((pagination.total - 1) / pagination.pageSize),
      )
      await loadDailyTrainStations(
        Math.min(pagination.current, lastPage),
      )
    } else {
      notification.error({ description: data.message || '删除失败' })
    }
  } catch (error) {
    notification.error({
      description: error.response?.data?.message || '删除失败，请稍后再试',
    })
  }
}

async function save() {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  saving.value = true
  try {
    const data = dailyTrainStation.id
      ? await updateDailyTrainStation(dailyTrainStation)
      : await saveDailyTrainStation(dailyTrainStation)
    if (data.success) {
      notification.success({
        description: dailyTrainStation.id ? '编辑成功' : '新增成功',
      })
      modalVisible.value = false
      resetForm()
      await loadDailyTrainStations()
    } else {
      notification.error({ description: data.message || '保存失败' })
    }
  } catch (error) {
    notification.error({
      description: error.response?.data?.message || '保存失败，请稍后再试',
    })
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadDailyTrainStations(1, pagination.pageSize)
})
</script>

<style scoped>
.daily-train-station-page {
  color: #1f2927;
}

.page-heading {
  display: flex;
  min-height: 64px;
  margin-bottom: 20px;
  gap: 16px;
  align-items: center;
  justify-content: space-between;
}

.page-heading h1 {
  margin: 0;
  color: #17211f;
  font-size: 24px;
}

.toolbar-actions,
.row-actions,
.query-bar {
  display: flex;
  gap: 8px;
  align-items: center;
}

.toolbar-actions .ant-btn-primary,
.query-bar .ant-btn-primary {
  border-color: #147d72;
  background: #147d72;
}

.daily-train-station-table {
  min-height: 420px;
  padding: 20px;
  border: 1px solid #dce3e1;
  border-radius: 8px;
  background: #ffffff;
}

.query-bar {
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.daily-train-station-page :deep(.ant-picker),
.daily-train-station-page :deep(.ant-input-number) {
  width: 100%;
}

.query-bar :deep(.ant-picker) {
  width: 180px;
}

@media (max-width: 640px) {
  .page-heading {
    align-items: flex-start;
    flex-direction: column;
  }

  .toolbar-actions {
    width: 100%;
    justify-content: space-between;
  }

  .daily-train-station-table {
    padding: 16px;
  }

  .query-bar > * {
    flex: 1 1 180px;
  }

  .query-bar .ant-btn {
    flex-basis: 100%;
  }

  :deep(.ant-modal) {
    max-width: calc(100vw - 32px);
  }
}
</style>
