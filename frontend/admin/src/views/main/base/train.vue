<template>
  <div class="train-page">
    <header class="page-heading">
      <h1>车次管理</h1>
      <div class="toolbar-actions">
        <a-button
          aria-label="刷新车次列表"
          title="刷新列表"
          type="text"
          @click="loadTrains()"
        >
          <ReloadOutlined />
        </a-button>
        <a-button type="primary" @click="openAddModal">
          <PlusOutlined />
          新增车次
        </a-button>
      </div>
    </header>

    <section class="train-table">
      <a-table
        :columns="columns"
        :data-source="trains"
        :loading="loading"
        :pagination="pagination"
        :row-key="(record) => record.id"
        :scroll="{ x: 900 }"
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
                @confirm="removeTrain(record.id)"
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
      :title="train.id ? '编辑车次' : '新增车次'"
      cancel-text="取消"
      ok-text="保存"
      @cancel="resetForm"
      @ok="save"
    >
      <a-form
        ref="formRef"
        :label-col="{ span: 6 }"
        :model="train"
        :rules="rules"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="车次编号" name="code">
          <a-input
            v-model:value="train.code"
            :disabled="Boolean(train.id)"
            :maxlength="20"
            placeholder="请输入车次编号"
          />
        </a-form-item>
        <a-form-item label="车次类型" name="type">
          <a-select
            v-model:value="train.type"
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
          <a-select
            v-model:value="train.start"
            option-filter-prop="label"
            placeholder="请选择始发站"
            show-search
            @change="selectStart"
          >
            <a-select-option
              v-for="item in stationOptions"
              :key="item.id"
              :label="`${item.name} ${item.namePinyin}`"
              :value="item.name"
            >
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="始发站拼音" name="startPinyin">
          <a-input v-model:value="train.startPinyin" disabled />
        </a-form-item>
        <a-form-item label="出发时间" name="startTime">
          <a-time-picker
            v-model:value="train.startTime"
            format="HH:mm"
            placeholder="请选择出发时间"
            value-format="HH:mm:ss"
          />
        </a-form-item>
        <a-form-item label="终点站" name="end">
          <a-select
            v-model:value="train.end"
            option-filter-prop="label"
            placeholder="请选择终点站"
            show-search
            @change="selectEnd"
          >
            <a-select-option
              v-for="item in stationOptions"
              :key="item.id"
              :label="`${item.name} ${item.namePinyin}`"
              :value="item.name"
            >
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="终点站拼音" name="endPinyin">
          <a-input v-model:value="train.endPinyin" disabled />
        </a-form-item>
        <a-form-item label="到站时间" name="endTime">
          <a-time-picker
            v-model:value="train.endTime"
            format="HH:mm"
            placeholder="请选择到站时间"
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
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { queryStationList } from '@/api/station'
import {
  deleteTrain,
  queryTrainList,
  saveTrain,
  updateTrain,
} from '@/api/train'

const trainTypes = [
  { code: 'G', description: '高铁' },
  { code: 'D', description: '动车' },
  { code: 'K', description: '快速' },
]
const formRef = ref()
const modalVisible = ref(false)
const saving = ref(false)
const loading = ref(false)
const trains = ref([])
const stationOptions = ref([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  pageSizeOptions: ['10', '20', '50'],
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 个车次`,
  total: 0,
})
const train = reactive({
  code: '',
  type: undefined,
  start: undefined,
  startPinyin: '',
  startTime: undefined,
  end: undefined,
  endPinyin: '',
  endTime: undefined,
})
const rules = {
  code: [{ required: true, message: '请输入车次编号' }],
  type: [{ required: true, message: '请选择车次类型' }],
  start: [{ required: true, message: '请选择始发站' }],
  startPinyin: [{ required: true, message: '始发站拼音不能为空' }],
  startTime: [{ required: true, message: '请选择出发时间' }],
  end: [{ required: true, message: '请选择终点站' }],
  endPinyin: [{ required: true, message: '终点站拼音不能为空' }],
  endTime: [{ required: true, message: '请选择到站时间' }],
}
const columns = [
  { title: '车次编号', dataIndex: 'code', key: 'code', width: 130 },
  { title: '类型', dataIndex: 'type', key: 'type', width: 100 },
  { title: '始发站', dataIndex: 'start', key: 'start', width: 140 },
  {
    title: '出发时间',
    dataIndex: 'startTime',
    key: 'startTime',
    width: 130,
  },
  { title: '终点站', dataIndex: 'end', key: 'end', width: 140 },
  { title: '到站时间', dataIndex: 'endTime', key: 'endTime', width: 130 },
  { title: '操作', key: 'operation', fixed: 'right', width: 140 },
]

function trainTypeName(code) {
  return trainTypes.find((item) => item.code === code)?.description || '-'
}

function formatTime(value) {
  return value ? value.slice(0, 5) : '-'
}

async function loadStationOptions() {
  try {
    // ponytail: 学习阶段车站不超过100个；超过后改为服务端关键字搜索。
    const data = await queryStationList({ page: 1, size: 100 })
    if (data.success) {
      stationOptions.value = data.content?.list || []
    }
  } catch (error) {
    notification.error({
      description: error.response?.data?.message || '车站列表加载失败',
    })
  }
}

async function loadTrains(
  page = pagination.current,
  pageSize = pagination.pageSize,
) {
  loading.value = true
  try {
    const data = await queryTrainList({ page, size: pageSize })
    if (data.success) {
      trains.value = data.content?.list || []
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

function selectStart(name) {
  const station = stationOptions.value.find((item) => item.name === name)
  train.startPinyin = station?.namePinyin || ''
}

function selectEnd(name) {
  const station = stationOptions.value.find((item) => item.name === name)
  train.endPinyin = station?.namePinyin || ''
}

function handleTableChange(tablePagination) {
  loadTrains(tablePagination.current, tablePagination.pageSize)
}

function resetForm() {
  train.id = undefined
  train.code = ''
  train.type = undefined
  train.start = undefined
  train.startPinyin = ''
  train.startTime = undefined
  train.end = undefined
  train.endPinyin = ''
  train.endTime = undefined
  formRef.value?.clearValidate()
}

function openAddModal() {
  resetForm()
  modalVisible.value = true
}

function openEditModal(record) {
  train.id = record.id
  train.code = record.code
  train.type = record.type
  train.start = record.start
  train.startPinyin = record.startPinyin
  train.startTime = record.startTime
  train.end = record.end
  train.endPinyin = record.endPinyin
  train.endTime = record.endTime
  modalVisible.value = true
}

async function removeTrain(id) {
  try {
    const data = await deleteTrain(id)
    if (data.success) {
      notification.success({ description: '删除成功' })
      const lastPage = Math.max(
        1,
        Math.ceil((pagination.total - 1) / pagination.pageSize),
      )
      await loadTrains(Math.min(pagination.current, lastPage))
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
    const data = train.id
      ? await updateTrain(train)
      : await saveTrain(train)
    if (data.success) {
      notification.success({
        description: train.id ? '编辑成功' : '新增成功',
      })
      modalVisible.value = false
      resetForm()
      await loadTrains()
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
  loadStationOptions()
  loadTrains(1, pagination.pageSize)
})
</script>

<style scoped>
.train-page {
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
.row-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.toolbar-actions .ant-btn-primary {
  border-color: #147d72;
  background: #147d72;
}

.train-table {
  min-height: 420px;
  padding: 20px;
  border: 1px solid #dce3e1;
  border-radius: 8px;
  background: #ffffff;
}

.train-page :deep(.ant-picker) {
  width: 100%;
}

@media (max-width: 520px) {
  .page-heading {
    align-items: flex-start;
    flex-direction: column;
  }

  .train-table {
    padding: 16px;
  }

  :deep(.ant-modal) {
    max-width: calc(100vw - 32px);
  }
}
</style>
