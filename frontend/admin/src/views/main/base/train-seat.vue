<template>
  <div class="train-seat-page">
    <header class="page-heading">
      <h1>座位管理</h1>
      <div class="toolbar-actions">
        <a-button
          aria-label="刷新座位列表"
          title="刷新列表"
          type="text"
          @click="loadTrainSeats()"
        >
          <ReloadOutlined />
        </a-button>
        <a-button type="primary" @click="openAddModal">
          <PlusOutlined />
          新增座位
        </a-button>
      </div>
    </header>

    <div class="query-bar">
      <TrainSelect v-model="query.trainCode" width="220px" />
      <a-button type="primary" @click="search">
        <SearchOutlined />
        查询
      </a-button>
    </div>

    <section class="train-seat-table">
      <a-table
        :columns="columns"
        :data-source="trainSeats"
        :loading="loading"
        :pagination="pagination"
        :row-key="(record) => record.id"
        :scroll="{ x: 900 }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'seatNumber'">
            {{ record.row }}{{ record.col }}
          </template>
          <template v-else-if="column.key === 'seatType'">
            {{ seatTypeName(record.seatType) }}
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
                @confirm="removeTrainSeat(record.id)"
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
      :title="trainSeat.id ? '编辑座位' : '新增座位'"
      cancel-text="取消"
      ok-text="保存"
      @cancel="resetForm"
      @ok="save"
    >
      <a-form
        ref="formRef"
        :label-col="{ span: 7 }"
        :model="trainSeat"
        :rules="rules"
        :wrapper-col="{ span: 17 }"
      >
        <a-form-item label="车次编号" name="trainCode">
          <TrainSelect v-model="trainSeat.trainCode" />
        </a-form-item>
        <a-form-item label="厢序" name="carriageIndex">
          <a-input-number
            v-model:value="trainSeat.carriageIndex"
            :min="1"
            placeholder="请输入厢序"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="排号" name="row">
          <a-input
            v-model:value="trainSeat.row"
            :maxlength="2"
            placeholder="例如 01"
            @blur="normalizeRow"
          />
        </a-form-item>
        <a-form-item label="座位类型" name="seatType">
          <a-select
            v-model:value="trainSeat.seatType"
            placeholder="请选择座位类型"
            @change="changeSeatType"
          >
            <a-select-option
              v-for="item in seatTypes"
              :key="item.code"
              :value="item.code"
            >
              {{ item.description }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="列号" name="col">
          <a-select
            v-model:value="trainSeat.col"
            :disabled="!trainSeat.seatType"
            placeholder="请选择列号"
          >
            <a-select-option
              v-for="item in availableColumns"
              :key="item"
              :value="item"
            >
              {{ item }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="同车厢座序" name="carriageSeatIndex">
          <a-input-number
            v-model:value="trainSeat.carriageSeatIndex"
            :min="1"
            placeholder="请输入座序"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { notification } from 'ant-design-vue'
import {
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons-vue'
import TrainSelect from '@/components/train-select.vue'
import {
  deleteTrainSeat,
  queryTrainSeatList,
  saveTrainSeat,
  updateTrainSeat,
} from '@/api/train-seat'

const seatTypes = [
  { code: '1', description: '一等座', columns: ['A', 'C', 'D', 'F'] },
  { code: '2', description: '二等座', columns: ['A', 'B', 'C', 'D', 'F'] },
  { code: '3', description: '软卧', columns: ['A', 'B', 'C', 'D'] },
  { code: '4', description: '硬卧', columns: ['A', 'B', 'C', 'D', 'E', 'F'] },
]
const formRef = ref()
const modalVisible = ref(false)
const saving = ref(false)
const loading = ref(false)
const trainSeats = ref([])
const query = reactive({
  trainCode: undefined,
})
const pagination = reactive({
  current: 1,
  pageSize: 10,
  pageSizeOptions: ['10', '20', '50'],
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 个座位`,
  total: 0,
})
const trainSeat = reactive({
  trainCode: undefined,
  carriageIndex: undefined,
  row: '',
  col: undefined,
  seatType: undefined,
  carriageSeatIndex: undefined,
})
const availableColumns = computed(
  () => seatTypes.find(
    (item) => item.code === trainSeat.seatType,
  )?.columns || [],
)
const rules = {
  trainCode: [{ required: true, message: '请选择车次' }],
  carriageIndex: [{ required: true, message: '请输入厢序' }],
  row: [
    { required: true, message: '请输入排号' },
    {
      pattern: /^(0[1-9]|[1-9][0-9])$/,
      message: '排号必须是01到99之间的两位数字',
    },
  ],
  col: [{ required: true, message: '请选择列号' }],
  seatType: [{ required: true, message: '请选择座位类型' }],
  carriageSeatIndex: [{ required: true, message: '请输入同车厢座序' }],
}
const columns = [
  { title: '车次编号', dataIndex: 'trainCode', key: 'trainCode', width: 130 },
  { title: '厢序', dataIndex: 'carriageIndex', key: 'carriageIndex', width: 90 },
  { title: '座位号', key: 'seatNumber', width: 100 },
  { title: '排号', dataIndex: 'row', key: 'row', width: 90 },
  { title: '列号', dataIndex: 'col', key: 'col', width: 90 },
  { title: '座位类型', dataIndex: 'seatType', key: 'seatType', width: 120 },
  {
    title: '同车厢座序',
    dataIndex: 'carriageSeatIndex',
    key: 'carriageSeatIndex',
    width: 130,
  },
  { title: '操作', key: 'operation', fixed: 'right', width: 140 },
]

function seatTypeName(code) {
  return seatTypes.find((item) => item.code === code)?.description || '-'
}

async function loadTrainSeats(
  page = pagination.current,
  pageSize = pagination.pageSize,
) {
  loading.value = true
  try {
    const data = await queryTrainSeatList({
      page,
      size: pageSize,
      trainCode: query.trainCode,
    })
    if (data.success) {
      trainSeats.value = data.content?.list || []
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
  loadTrainSeats(1, pagination.pageSize)
}

function handleTableChange(tablePagination) {
  loadTrainSeats(tablePagination.current, tablePagination.pageSize)
}

function normalizeRow() {
  if (/^\d{1,2}$/.test(trainSeat.row)) {
    trainSeat.row = trainSeat.row.padStart(2, '0')
  }
}

function changeSeatType() {
  if (!availableColumns.value.includes(trainSeat.col)) {
    trainSeat.col = undefined
  }
}

function resetForm() {
  trainSeat.id = undefined
  trainSeat.trainCode = undefined
  trainSeat.carriageIndex = undefined
  trainSeat.row = ''
  trainSeat.col = undefined
  trainSeat.seatType = undefined
  trainSeat.carriageSeatIndex = undefined
  formRef.value?.clearValidate()
}

function openAddModal() {
  resetForm()
  modalVisible.value = true
}

function openEditModal(record) {
  trainSeat.id = record.id
  trainSeat.trainCode = record.trainCode
  trainSeat.carriageIndex = record.carriageIndex
  trainSeat.row = record.row
  trainSeat.seatType = record.seatType
  trainSeat.col = record.col
  trainSeat.carriageSeatIndex = record.carriageSeatIndex
  modalVisible.value = true
}

async function removeTrainSeat(id) {
  try {
    const data = await deleteTrainSeat(id)
    if (data.success) {
      notification.success({ description: '删除成功' })
      const lastPage = Math.max(
        1,
        Math.ceil((pagination.total - 1) / pagination.pageSize),
      )
      await loadTrainSeats(Math.min(pagination.current, lastPage))
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
  normalizeRow()
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  saving.value = true
  try {
    const data = trainSeat.id
      ? await updateTrainSeat(trainSeat)
      : await saveTrainSeat(trainSeat)
    if (data.success) {
      notification.success({
        description: trainSeat.id ? '编辑成功' : '新增成功',
      })
      modalVisible.value = false
      resetForm()
      await loadTrainSeats()
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
  loadTrainSeats(1, pagination.pageSize)
})
</script>

<style scoped>
.train-seat-page {
  color: #1f2927;
}

.page-heading,
.query-bar,
.toolbar-actions,
.row-actions {
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

.toolbar-actions,
.query-bar {
  gap: 8px;
}

.query-bar {
  margin-bottom: 16px;
}

.train-seat-table {
  width: 100%;
}

.row-actions {
  gap: 4px;
}

@media (max-width: 640px) {
  .page-heading {
    align-items: flex-start;
    gap: 12px;
  }

  .query-bar {
    align-items: stretch;
    flex-direction: column;
  }

  .query-bar :deep(.ant-select) {
    width: 100% !important;
  }
}
</style>
