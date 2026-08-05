<template>
  <div class="train-carriage-page">
    <header class="page-heading">
      <h1>车厢管理</h1>
      <div class="toolbar-actions">
        <a-button
          aria-label="刷新车厢列表"
          title="刷新列表"
          type="text"
          @click="loadTrainCarriages()"
        >
          <ReloadOutlined />
        </a-button>
        <a-button type="primary" @click="openAddModal">
          <PlusOutlined />
          新增车厢
        </a-button>
      </div>
    </header>

    <div class="query-bar">
      <a-select
        v-model:value="query.trainCode"
        allow-clear
        option-filter-prop="label"
        placeholder="请选择车次"
        show-search
        style="width: 220px"
      >
        <a-select-option
          v-for="item in trainOptions"
          :key="item.id"
          :label="item.code"
          :value="item.code"
        >
          {{ item.code }}
        </a-select-option>
      </a-select>
      <a-button type="primary" @click="search">
        <SearchOutlined />
        查询
      </a-button>
    </div>

    <section class="train-carriage-table">
      <a-table
        :columns="columns"
        :data-source="trainCarriages"
        :loading="loading"
        :pagination="pagination"
        :row-key="(record) => record.id"
        :scroll="{ x: 850 }"
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
                @confirm="removeTrainCarriage(record.id)"
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
      :title="trainCarriage.id ? '编辑车厢' : '新增车厢'"
      cancel-text="取消"
      ok-text="保存"
      @cancel="resetForm"
      @ok="save"
    >
      <a-form
        ref="formRef"
        :label-col="{ span: 6 }"
        :model="trainCarriage"
        :rules="rules"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="车次编号" name="trainCode">
          <a-select
            v-model:value="trainCarriage.trainCode"
            option-filter-prop="label"
            placeholder="请选择车次"
            show-search
          >
            <a-select-option
              v-for="item in trainOptions"
              :key="item.id"
              :label="item.code"
              :value="item.code"
            >
              {{ item.code }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="厢号" name="index">
          <a-input-number
            v-model:value="trainCarriage.index"
            :min="1"
            placeholder="请输入厢号"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="座位类型" name="seatType">
          <a-select
            v-model:value="trainCarriage.seatType"
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
            v-model:value="trainCarriage.rowCount"
            :min="1"
            placeholder="请输入排数"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item v-if="trainCarriage.id" label="列数">
          <a-input :value="trainCarriage.columnCount" disabled />
        </a-form-item>
        <a-form-item v-if="trainCarriage.id" label="座位数">
          <a-input :value="trainCarriage.seatCount" disabled />
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
import { queryAllTrains } from '@/api/train'
import {
  deleteTrainCarriage,
  queryTrainCarriageList,
  saveTrainCarriage,
  updateTrainCarriage,
} from '@/api/train-carriage'

const seatTypes = [
  { code: '1', description: '一等座' },
  { code: '2', description: '二等座' },
  { code: '3', description: '软卧' },
  { code: '4', description: '硬卧' },
]
const formRef = ref()
const modalVisible = ref(false)
const saving = ref(false)
const loading = ref(false)
const trainCarriages = ref([])
const trainOptions = ref([])
const query = reactive({
  trainCode: undefined,
})
const pagination = reactive({
  current: 1,
  pageSize: 10,
  pageSizeOptions: ['10', '20', '50'],
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 个车厢`,
  total: 0,
})
const trainCarriage = reactive({
  trainCode: undefined,
  index: undefined,
  seatType: undefined,
  rowCount: undefined,
  columnCount: undefined,
  seatCount: undefined,
})
const rules = {
  trainCode: [{ required: true, message: '请选择车次' }],
  index: [{ required: true, message: '请输入厢号' }],
  seatType: [{ required: true, message: '请选择座位类型' }],
  rowCount: [{ required: true, message: '请输入排数' }],
}
const columns = [
  { title: '车次编号', dataIndex: 'trainCode', key: 'trainCode', width: 130 },
  { title: '厢号', dataIndex: 'index', key: 'index', width: 90 },
  { title: '座位类型', dataIndex: 'seatType', key: 'seatType', width: 120 },
  { title: '座位数', dataIndex: 'seatCount', key: 'seatCount', width: 100 },
  { title: '排数', dataIndex: 'rowCount', key: 'rowCount', width: 90 },
  { title: '列数', dataIndex: 'columnCount', key: 'columnCount', width: 90 },
  { title: '操作', key: 'operation', fixed: 'right', width: 140 },
]

function seatTypeName(code) {
  return seatTypes.find((item) => item.code === code)?.description || '-'
}

async function loadTrainOptions() {
  try {
    const data = await queryAllTrains()
    if (data.success) {
      trainOptions.value = data.content || []
    }
  } catch (error) {
    notification.error({
      description: error.response?.data?.message || '车次列表加载失败',
    })
  }
}

async function loadTrainCarriages(
  page = pagination.current,
  pageSize = pagination.pageSize,
) {
  loading.value = true
  try {
    const data = await queryTrainCarriageList({
      page,
      size: pageSize,
      trainCode: query.trainCode,
    })
    if (data.success) {
      trainCarriages.value = data.content?.list || []
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
  loadTrainCarriages(1, pagination.pageSize)
}

function handleTableChange(tablePagination) {
  loadTrainCarriages(tablePagination.current, tablePagination.pageSize)
}

function resetForm() {
  trainCarriage.id = undefined
  trainCarriage.trainCode = undefined
  trainCarriage.index = undefined
  trainCarriage.seatType = undefined
  trainCarriage.rowCount = undefined
  trainCarriage.columnCount = undefined
  trainCarriage.seatCount = undefined
  formRef.value?.clearValidate()
}

function openAddModal() {
  resetForm()
  modalVisible.value = true
}

function openEditModal(record) {
  trainCarriage.id = record.id
  trainCarriage.trainCode = record.trainCode
  trainCarriage.index = record.index
  trainCarriage.seatType = record.seatType
  trainCarriage.rowCount = record.rowCount
  trainCarriage.columnCount = record.columnCount
  trainCarriage.seatCount = record.seatCount
  modalVisible.value = true
}

async function removeTrainCarriage(id) {
  try {
    const data = await deleteTrainCarriage(id)
    if (data.success) {
      notification.success({ description: '删除成功' })
      const lastPage = Math.max(
        1,
        Math.ceil((pagination.total - 1) / pagination.pageSize),
      )
      await loadTrainCarriages(Math.min(pagination.current, lastPage))
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
    const payload = {
      id: trainCarriage.id,
      trainCode: trainCarriage.trainCode,
      index: trainCarriage.index,
      seatType: trainCarriage.seatType,
      rowCount: trainCarriage.rowCount,
    }
    const data = trainCarriage.id
      ? await updateTrainCarriage(payload)
      : await saveTrainCarriage(payload)
    if (data.success) {
      notification.success({
        description: trainCarriage.id ? '编辑成功' : '新增成功',
      })
      modalVisible.value = false
      resetForm()
      await loadTrainCarriages()
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
  loadTrainOptions()
  loadTrainCarriages(1, pagination.pageSize)
})
</script>

<style scoped>
.train-carriage-page {
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

.train-carriage-table {
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
