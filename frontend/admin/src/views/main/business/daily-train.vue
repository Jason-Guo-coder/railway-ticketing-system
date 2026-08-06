<template>
  <div class="daily-train-page">
    <header class="page-heading">
      <h1>每日车次管理</h1>
      <div class="toolbar-actions">
        <a-button
          aria-label="刷新每日车次列表"
          title="刷新列表"
          type="text"
          @click="loadDailyTrains()"
        >
          <ReloadOutlined />
        </a-button>
        <a-button type="primary" @click="openAddModal">
          <PlusOutlined />
          新增每日车次
        </a-button>
      </div>
    </header>

    <section class="daily-train-table">
      <div class="query-bar">
        <a-date-picker
          v-model:value="query.date"
          allow-clear
          placeholder="全部日期"
          value-format="YYYY-MM-DD"
        />
        <TrainSelect
          v-model="query.code"
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
        :data-source="dailyTrains"
        :loading="loading"
        :pagination="pagination"
        :row-key="(record) => record.id"
        :scroll="{ x: 920 }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'type'">
            {{ trainTypeName(record.type) }}
          </template>
          <template
            v-else-if="column.key === 'startTime' || column.key === 'endTime'"
          >
            {{ formatTime(record[column.dataIndex]) }}
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
                @confirm="removeDailyTrain(record.id)"
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
      :title="dailyTrain.id ? '编辑每日车次' : '新增每日车次'"
      cancel-text="取消"
      ok-text="保存"
      width="620px"
      @cancel="resetForm"
      @ok="save"
    >
      <a-form
        ref="formRef"
        :label-col="{ span: 6 }"
        :model="dailyTrain"
        :rules="rules"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="日期" name="date">
          <a-date-picker
            v-model:value="dailyTrain.date"
            placeholder="请选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </a-form-item>
        <a-form-item label="车次编号" name="code">
          <TrainSelect
            v-model="dailyTrain.code"
            @change="selectTrain"
          />
        </a-form-item>
        <a-form-item label="车次类型" name="type">
          <a-select
            v-model:value="dailyTrain.type"
            placeholder="请选择车次类型"
          >
            <a-select-option
              v-for="item in trainTypes"
              :key="item.code"
              :value="item.code"
            >
              {{ item.description }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="始发站" name="start">
          <StationSelect
            v-model="dailyTrain.start"
            placeholder="请选择始发站"
            @change="selectStart"
          />
        </a-form-item>
        <a-form-item label="始发站拼音" name="startPinyin">
          <a-input v-model:value="dailyTrain.startPinyin" disabled />
        </a-form-item>
        <a-form-item label="出发时间" name="startTime">
          <a-time-picker
            v-model:value="dailyTrain.startTime"
            format="HH:mm"
            placeholder="请选择出发时间"
            style="width: 100%"
            value-format="HH:mm:ss"
          />
        </a-form-item>
        <a-form-item label="终点站" name="end">
          <StationSelect
            v-model="dailyTrain.end"
            placeholder="请选择终点站"
            @change="selectEnd"
          />
        </a-form-item>
        <a-form-item label="终点站拼音" name="endPinyin">
          <a-input v-model:value="dailyTrain.endPinyin" disabled />
        </a-form-item>
        <a-form-item label="到站时间" name="endTime">
          <a-time-picker
            v-model:value="dailyTrain.endTime"
            format="HH:mm"
            placeholder="请选择到站时间"
            style="width: 100%"
            value-format="HH:mm:ss"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { notification } from 'ant-design-vue'
import {
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons-vue'
import StationSelect from '@/components/station-select.vue'
import TrainSelect from '@/components/train-select.vue'
import {
  deleteDailyTrain,
  queryDailyTrainList,
  saveDailyTrain,
  updateDailyTrain,
} from '@/api/daily-train'

const trainTypes = [
  { code: 'G', description: '高铁' },
  { code: 'D', description: '动车' },
  { code: 'K', description: '快速' },
]
const formRef = ref()
const modalVisible = ref(false)
const saving = ref(false)
const loading = ref(false)
const dailyTrains = ref([])
const query = reactive({
  date: undefined,
  code: undefined,
})
const pagination = reactive({
  current: 1,
  pageSize: 10,
  pageSizeOptions: ['10', '20', '50'],
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 个每日车次`,
  total: 0,
})
const dailyTrain = reactive({
  date: undefined,
  code: undefined,
  type: undefined,
  start: undefined,
  startPinyin: '',
  startTime: undefined,
  end: undefined,
  endPinyin: '',
  endTime: undefined,
})
const rules = {
  date: [{ required: true, message: '请选择日期' }],
  code: [{ required: true, message: '请选择车次' }],
  type: [{ required: true, message: '请选择车次类型' }],
  start: [{ required: true, message: '请选择始发站' }],
  startPinyin: [{ required: true, message: '始发站拼音不能为空' }],
  startTime: [{ required: true, message: '请选择出发时间' }],
  end: [{ required: true, message: '请选择终点站' }],
  endPinyin: [{ required: true, message: '终点站拼音不能为空' }],
  endTime: [{ required: true, message: '请选择到站时间' }],
}
const columns = [
  { title: '日期', dataIndex: 'date', key: 'date', width: 130 },
  { title: '车次编号', dataIndex: 'code', key: 'code', width: 120 },
  { title: '类型', dataIndex: 'type', key: 'type', width: 90 },
  { title: '始发站', dataIndex: 'start', key: 'start', width: 130 },
  {
    title: '出发时间',
    dataIndex: 'startTime',
    key: 'startTime',
    width: 110,
  },
  { title: '终点站', dataIndex: 'end', key: 'end', width: 130 },
  {
    title: '到站时间',
    dataIndex: 'endTime',
    key: 'endTime',
    width: 110,
  },
  { title: '操作', key: 'operation', fixed: 'right', width: 140 },
]

function trainTypeName(code) {
  return trainTypes.find((item) => item.code === code)?.description || '-'
}

function formatTime(value) {
  return value ? value.slice(0, 5) : '-'
}

async function loadDailyTrains(
  page = pagination.current,
  pageSize = pagination.pageSize,
) {
  loading.value = true
  try {
    const data = await queryDailyTrainList({
      page,
      size: pageSize,
      date: query.date || undefined,
      code: query.code || undefined,
    })
    if (data.success) {
      dailyTrains.value = data.content?.list || []
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
  loadDailyTrains(1, pagination.pageSize)
}

function handleTableChange(tablePagination) {
  loadDailyTrains(tablePagination.current, tablePagination.pageSize)
}

function clearTrainFields() {
  dailyTrain.type = undefined
  dailyTrain.start = undefined
  dailyTrain.startPinyin = ''
  dailyTrain.startTime = undefined
  dailyTrain.end = undefined
  dailyTrain.endPinyin = ''
  dailyTrain.endTime = undefined
}

function selectTrain(train) {
  if (!train) {
    clearTrainFields()
    return
  }

  dailyTrain.type = train.type
  dailyTrain.start = train.start
  dailyTrain.startPinyin = train.startPinyin
  dailyTrain.startTime = train.startTime
  dailyTrain.end = train.end
  dailyTrain.endPinyin = train.endPinyin
  dailyTrain.endTime = train.endTime
}

function selectStart(station) {
  dailyTrain.startPinyin = station?.namePinyin || ''
}

function selectEnd(station) {
  dailyTrain.endPinyin = station?.namePinyin || ''
}

function resetForm() {
  dailyTrain.id = undefined
  dailyTrain.date = undefined
  dailyTrain.code = undefined
  clearTrainFields()
  formRef.value?.clearValidate()
}

function openAddModal() {
  resetForm()
  dailyTrain.date = query.date
  modalVisible.value = true
}

function openEditModal(record) {
  dailyTrain.id = record.id
  dailyTrain.date = record.date
  dailyTrain.code = record.code
  dailyTrain.type = record.type
  dailyTrain.start = record.start
  dailyTrain.startPinyin = record.startPinyin
  dailyTrain.startTime = record.startTime
  dailyTrain.end = record.end
  dailyTrain.endPinyin = record.endPinyin
  dailyTrain.endTime = record.endTime
  modalVisible.value = true
}

async function removeDailyTrain(id) {
  try {
    const data = await deleteDailyTrain(id)
    if (data.success) {
      notification.success({ description: '删除成功' })
      const lastPage = Math.max(
        1,
        Math.ceil((pagination.total - 1) / pagination.pageSize),
      )
      await loadDailyTrains(Math.min(pagination.current, lastPage))
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
    const data = dailyTrain.id
      ? await updateDailyTrain(dailyTrain)
      : await saveDailyTrain(dailyTrain)
    if (data.success) {
      notification.success({
        description: dailyTrain.id ? '编辑成功' : '新增成功',
      })
      modalVisible.value = false
      resetForm()
      await loadDailyTrains()
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
  loadDailyTrains(1, pagination.pageSize)
})
</script>

<style scoped>
.daily-train-page {
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

.daily-train-table {
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

.daily-train-page :deep(.ant-picker) {
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

  .daily-train-table {
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
