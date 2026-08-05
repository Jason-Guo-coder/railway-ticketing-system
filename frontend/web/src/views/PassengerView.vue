<template>
  <div class="content-page">
    <header class="page-heading">
      <h1>乘车人管理</h1>
      <p>维护购票时使用的乘车人信息</p>
    </header>

    <section class="passenger-section">
      <div class="section-toolbar">
        <h2>常用乘车人</h2>
        <div class="toolbar-actions">
          <a-button
            aria-label="刷新乘车人列表"
            title="刷新列表"
            type="text"
            @click="loadPassengers()"
          >
            <ReloadOutlined />
          </a-button>
          <a-button type="primary" @click="openAddModal">
            <PlusOutlined />
            新增
          </a-button>
        </div>
      </div>
      <a-table
        :columns="columns"
        :data-source="passengers"
        :loading="loading"
        :pagination="pagination"
        :row-key="(record) => record.id"
        :scroll="{ x: 640 }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'type'">
            {{ passengerTypeName(record.type) }}
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ record.createTime || '-' }}
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
                @confirm="removePassenger(record.id)"
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
      :title="passenger.id ? '编辑乘车人' : '新增乘车人'"
      ok-text="保存"
      cancel-text="取消"
      @ok="save"
      @cancel="resetForm"
    >
      <a-form
        ref="formRef"
        :label-col="{ span: 5 }"
        :model="passenger"
        :rules="rules"
        :wrapper-col="{ span: 19 }"
      >
        <a-form-item label="姓名" name="name">
          <a-input
            v-model:value="passenger.name"
            :maxlength="20"
            placeholder="请输入乘车人姓名"
          />
        </a-form-item>
        <a-form-item label="身份证" name="idCard">
          <a-input
            v-model:value="passenger.idCard"
            :maxlength="18"
            placeholder="请输入身份证号码"
          />
        </a-form-item>
        <a-form-item label="旅客类型" name="type">
          <a-select
            v-model:value="passenger.type"
            placeholder="请选择旅客类型"
          >
            <a-select-option
              v-for="item in passengerTypes"
              :key="item.code"
              :value="item.code"
            >
              {{ item.desc }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { notification } from 'ant-design-vue'
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import {
  deletePassenger,
  queryPassengerList,
  savePassenger,
  updatePassenger,
} from '@/api/passenger'

const passengerTypes = [
  { code: '1', desc: '成人' },
  { code: '2', desc: '儿童' },
  { code: '3', desc: '学生' },
]
const formRef = ref()
const modalVisible = ref(false)
const saving = ref(false)
const loading = ref(false)
const passengers = ref([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  pageSizeOptions: ['10', '20', '50'],
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 位乘车人`,
  total: 0,
})
const passenger = reactive({
  name: '',
  idCard: '',
  type: '1',
})
const rules = {
  name: [{ required: true, message: '请输入乘车人姓名' }],
  idCard: [{ required: true, message: '请输入身份证号码' }],
  type: [{ required: true, message: '请选择旅客类型' }],
}
const columns = [
  {
    title: '姓名',
    dataIndex: 'name',
    key: 'name',
    width: 160,
  },
  {
    title: '身份证',
    dataIndex: 'idCard',
    key: 'idCard',
    width: 240,
  },
  {
    title: '旅客类型',
    dataIndex: 'type',
    key: 'type',
    width: 140,
  },
  {
    title: '添加时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 200,
  },
  {
    title: '操作',
    key: 'operation',
    fixed: 'right',
    width: 140,
  },
]

function passengerTypeName(code) {
  return passengerTypes.find((item) => item.code === code)?.desc || '-'
}

async function loadPassengers(
  page = pagination.current,
  pageSize = pagination.pageSize,
) {
  loading.value = true
  try {
    const data = await queryPassengerList({ page, size: pageSize })
    if (data.success) {
      passengers.value = data.content?.list || []
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
  loadPassengers(tablePagination.current, tablePagination.pageSize)
}

function openEditModal(record) {
  passenger.id = record.id
  passenger.name = record.name
  passenger.idCard = record.idCard
  passenger.type = record.type
  modalVisible.value = true
}

async function removePassenger(id) {
  try {
    const data = await deletePassenger(id)
    if (data.success) {
      notification.success({ description: '删除成功' })
      const lastPage = Math.max(
        1,
        Math.ceil((pagination.total - 1) / pagination.pageSize),
      )
      await loadPassengers(Math.min(pagination.current, lastPage))
    } else {
      notification.error({ description: data.message || '删除失败' })
    }
  } catch (error) {
    notification.error({
      description: error.response?.data?.message || '删除失败，请稍后再试',
    })
  }
}

function resetForm() {
  passenger.id = undefined
  passenger.name = ''
  passenger.idCard = ''
  passenger.type = '1'
  formRef.value?.clearValidate()
}

function openAddModal() {
  resetForm()
  modalVisible.value = true
}

async function save() {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  saving.value = true
  try {
    const data = passenger.id
      ? await updatePassenger(passenger)
      : await savePassenger(passenger)
    if (data.success) {
      notification.success({
        description: passenger.id ? '编辑成功' : '保存成功',
      })
      modalVisible.value = false
      resetForm()
      await loadPassengers()
    } else {
      notification.error({ description: data.message })
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
  loadPassengers(1, pagination.pageSize)
})
</script>

<style scoped>
.content-page {
  color: #1f2927;
}

.page-heading {
  min-height: 64px;
  margin-bottom: 20px;
}

.page-heading h1,
.section-toolbar h2 {
  margin: 0;
  color: #17211f;
  font-weight: 700;
}

.page-heading h1 {
  font-size: 24px;
}

.page-heading p {
  margin: 4px 0 0;
  color: #6a7774;
}

.passenger-section {
  min-height: 420px;
  padding: 20px;
  border: 1px solid #dce3e1;
  border-radius: 8px;
  background: #ffffff;
}

.section-toolbar {
  display: flex;
  margin-bottom: 40px;
  align-items: center;
  justify-content: space-between;
}

.toolbar-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.section-toolbar h2 {
  font-size: 16px;
}

.section-toolbar .ant-btn-primary {
  border-color: #147d72;
  background: #147d72;
}

.passenger-section :deep(.ant-table-wrapper) {
  min-height: 260px;
}

.row-actions {
  display: flex;
  align-items: center;
}

@media (max-width: 520px) {
  .passenger-section {
    padding: 16px;
  }

  :deep(.ant-modal) {
    max-width: calc(100vw - 32px);
  }
}
</style>
