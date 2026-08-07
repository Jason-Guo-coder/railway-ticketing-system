<template>
  <div class="daily-train-carriage-page">
    <header class="page-heading">
      <h1>每日车厢管理</h1>
      <div class="toolbar-actions">
        <a-button
          aria-label="刷新每日车厢列表"
          title="刷新列表"
          type="text"
          @click="loadDailyTrainCarriages()"
        >
          <ReloadOutlined />
        </a-button>
        <a-button type="primary" @click="openAddModal">
          <PlusOutlined />
          新增每日车厢
        </a-button>
      </div>
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

    <section class="daily-train-carriage-table">
      <a-table
        :columns="columns"
        :data-source="dailyTrainCarriages"
        :loading="loading"
        :pagination="pagination"
        :row-key="(record) => record.id"
        :scroll="{ x: 980 }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'seatType'">
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
                @confirm="removeDailyTrainCarriage(record.id)"
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
      :title="dailyTrainCarriage.id ? '编辑每日车厢' : '新增每日车厢'"
      cancel-text="取消"
      ok-text="保存"
      @cancel="resetForm"
      @ok="save"
    >
      <a-form
        ref="formRef"
        :label-col="{ span: 6 }"
        :model="dailyTrainCarriage"
        :rules="rules"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="日期" name="date">
          <a-date-picker
            v-model:value="dailyTrainCarriage.date"
            placeholder="请选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </a-form-item>
        <a-form-item label="车次编号" name="trainCode">
          <TrainSelect v-model="dailyTrainCarriage.trainCode" />
        </a-form-item>
        <a-form-item label="厢序" name="index">
          <a-input-number
            v-model:value="dailyTrainCarriage.index"
            :min="1"
            :precision="0"
            placeholder="请输入厢序"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="座位类型" name="seatType">
          <a-select
            v-model:value="dailyTrainCarriage.seatType"
            placeholder="请选择座位类型"
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
        <a-form-item label="排数" name="rowCount">
          <a-input-number
            v-model:value="dailyTrainCarriage.rowCount"
            :max="99"
            :min="1"
            :precision="0"
            placeholder="请输入排数"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="列数">
          <a-input :value="calculatedColCount" disabled />
        </a-form-item>
        <a-form-item label="座位数">
          <a-input :value="calculatedSeatCount" disabled />
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
  deleteDailyTrainCarriage,
  queryDailyTrainCarriageList,
  saveDailyTrainCarriage,
  updateDailyTrainCarriage,
} from '@/api/daily-train-carriage'

const seatTypes = [
  { code: '1', description: '一等座', colCount: 4 },
  { code: '2', description: '二等座', colCount: 5 },
  { code: '3', description: '软卧', colCount: 4 },
  { code: '4', description: '硬卧', colCount: 6 },
]
const formRef = ref()
const modalVisible = ref(false)
const saving = ref(false)
const loading = ref(false)
const dailyTrainCarriages = ref([])
const query = reactive({
  date: undefined,
  trainCode: undefined,
})
const pagination = reactive({
  current: 1,
  pageSize: 10,
  pageSizeOptions: ['10', '20', '50'],
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 个每日车厢`,
  total: 0,
})
const dailyTrainCarriage = reactive({
  date: undefined,
  trainCode: undefined,
  index: undefined,
  seatType: undefined,
  rowCount: undefined,
})
const rules = {
  date: [{ required: true, message: '请选择日期' }],
  trainCode: [{ required: true, message: '请选择车次' }],
  index: [{ required: true, message: '请输入厢序' }],
  seatType: [{ required: true, message: '请选择座位类型' }],
  rowCount: [{ required: true, message: '请输入排数' }],
}
const columns = [
  { title: '日期', dataIndex: 'date', key: 'date', width: 130 },
  { title: '车次编号', dataIndex: 'trainCode', key: 'trainCode', width: 130 },
  { title: '厢序', dataIndex: 'index', key: 'index', width: 90 },
  { title: '座位类型', dataIndex: 'seatType', key: 'seatType', width: 120 },
  { title: '座位数', dataIndex: 'seatCount', key: 'seatCount', width: 100 },
  { title: '排数', dataIndex: 'rowCount', key: 'rowCount', width: 90 },
  { title: '列数', dataIndex: 'colCount', key: 'colCount', width: 90 },
  { title: '操作', key: 'operation', fixed: 'right', width: 140 },
]
const selectedSeatType = computed(
  () => seatTypes.find(
    (item) => item.code === dailyTrainCarriage.seatType,
  ),
)
const calculatedColCount = computed(
  () => selectedSeatType.value?.colCount || '',
)
const calculatedSeatCount = computed(() => {
  if (!calculatedColCount.value || !dailyTrainCarriage.rowCount) {
    return ''
  }
  return calculatedColCount.value * dailyTrainCarriage.rowCount
})

function seatTypeName(code) {
  return seatTypes.find((item) => item.code === code)?.description || '-'
}

async function loadDailyTrainCarriages(
  page = pagination.current,
  pageSize = pagination.pageSize,
) {
  loading.value = true
  try {
    const data = await queryDailyTrainCarriageList({
      page,
      size: pageSize,
      date: query.date || undefined,
      trainCode: query.trainCode || undefined,
    })
    if (data.success) {
      dailyTrainCarriages.value = data.content?.list || []
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
  loadDailyTrainCarriages(1, pagination.pageSize)
}

function handleTableChange(tablePagination) {
  loadDailyTrainCarriages(
    tablePagination.current,
    tablePagination.pageSize,
  )
}

function resetForm() {
  dailyTrainCarriage.id = undefined
  dailyTrainCarriage.date = undefined
  dailyTrainCarriage.trainCode = undefined
  dailyTrainCarriage.index = undefined
  dailyTrainCarriage.seatType = undefined
  dailyTrainCarriage.rowCount = undefined
  formRef.value?.clearValidate()
}

function openAddModal() {
  resetForm()
  dailyTrainCarriage.date = query.date
  dailyTrainCarriage.trainCode = query.trainCode
  modalVisible.value = true
}

function openEditModal(record) {
  dailyTrainCarriage.id = record.id
  dailyTrainCarriage.date = record.date
  dailyTrainCarriage.trainCode = record.trainCode
  dailyTrainCarriage.index = record.index
  dailyTrainCarriage.seatType = record.seatType
  dailyTrainCarriage.rowCount = record.rowCount
  modalVisible.value = true
}

async function removeDailyTrainCarriage(id) {
  try {
    const data = await deleteDailyTrainCarriage(id)
    if (data.success) {
      notification.success({ description: '删除成功' })
      const lastPage = Math.max(
        1,
        Math.ceil((pagination.total - 1) / pagination.pageSize),
      )
      await loadDailyTrainCarriages(
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
    const data = dailyTrainCarriage.id
      ? await updateDailyTrainCarriage(dailyTrainCarriage)
      : await saveDailyTrainCarriage(dailyTrainCarriage)
    if (data.success) {
      notification.success({
        description: dailyTrainCarriage.id ? '编辑成功' : '新增成功',
      })
      modalVisible.value = false
      resetForm()
      await loadDailyTrainCarriages()
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
  loadDailyTrainCarriages(1, pagination.pageSize)
})
</script>

<style scoped>
.daily-train-carriage-page {
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
  flex-wrap: wrap;
}

.query-bar :deep(.ant-picker) {
  width: 180px;
}

.daily-train-carriage-table {
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

  .query-bar :deep(.ant-picker),
  .query-bar :deep(.ant-select) {
    width: 100% !important;
  }
}
</style>
