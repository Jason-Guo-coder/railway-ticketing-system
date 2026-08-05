<template>
  <div class="station-page">
    <header class="page-heading">
      <h1>车站管理</h1>
      <div class="toolbar-actions">
        <a-button
          aria-label="刷新车站列表"
          title="刷新列表"
          type="text"
          @click="loadStations()"
        >
          <ReloadOutlined />
        </a-button>
        <a-button type="primary" @click="openAddModal">
          <PlusOutlined />
          新增车站
        </a-button>
      </div>
    </header>

    <section class="station-table">
      <a-table
        :columns="columns"
        :data-source="stations"
        :loading="loading"
        :pagination="pagination"
        :row-key="(record) => record.id"
        :scroll="{ x: 760 }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'operation'">
            <div class="row-actions">
              <a-button type="link" @click="openEditModal(record)">
                编辑
              </a-button>
              <a-popconfirm
                cancel-text="取消"
                ok-text="确认"
                title="删除后不可恢复，确认删除吗？"
                @confirm="removeStation(record.id)"
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
      :title="station.id ? '编辑车站' : '新增车站'"
      cancel-text="取消"
      ok-text="保存"
      @cancel="resetForm"
      @ok="save"
    >
      <a-form
        ref="formRef"
        :label-col="{ span: 6 }"
        :model="station"
        :rules="rules"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="站名" name="name">
          <a-input
            v-model:value="station.name"
            :maxlength="20"
            placeholder="请输入站名"
          />
        </a-form-item>
        <a-form-item label="站名拼音" name="namePinyin">
          <a-input v-model:value="station.namePinyin" disabled />
        </a-form-item>
        <a-form-item label="拼音首字母" name="namePy">
          <a-input v-model:value="station.namePy" disabled />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { notification } from 'ant-design-vue'
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { pinyin } from 'pinyin-pro'
import {
  deleteStation,
  queryStationList,
  saveStation,
  updateStation,
} from '@/api/station'

const formRef = ref()
const modalVisible = ref(false)
const saving = ref(false)
const loading = ref(false)
const stations = ref([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  pageSizeOptions: ['10', '20', '50'],
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 个车站`,
  total: 0,
})
const station = reactive({
  name: '',
  namePinyin: '',
  namePy: '',
})
const rules = {
  name: [{ required: true, message: '请输入站名' }],
  namePinyin: [{ required: true, message: '站名拼音不能为空' }],
  namePy: [{ required: true, message: '拼音首字母不能为空' }],
}
const columns = [
  { title: '站名', dataIndex: 'name', key: 'name', width: 180 },
  {
    title: '站名拼音',
    dataIndex: 'namePinyin',
    key: 'namePinyin',
    width: 240,
  },
  {
    title: '拼音首字母',
    dataIndex: 'namePy',
    key: 'namePy',
    width: 180,
  },
  { title: '操作', key: 'operation', fixed: 'right', width: 140 },
]

watch(
  () => station.name,
  (name) => {
    if (name?.trim()) {
      station.namePinyin = pinyin(name, { toneType: 'none' })
        .replaceAll(' ', '')
      station.namePy = pinyin(name, {
        pattern: 'first',
        toneType: 'none',
      }).replaceAll(' ', '')
    } else {
      station.namePinyin = ''
      station.namePy = ''
    }
  },
)

async function loadStations(
  page = pagination.current,
  pageSize = pagination.pageSize,
) {
  loading.value = true
  try {
    const data = await queryStationList({ page, size: pageSize })
    if (data.success) {
      stations.value = data.content?.list || []
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
  loadStations(tablePagination.current, tablePagination.pageSize)
}

function resetForm() {
  station.id = undefined
  station.name = ''
  station.namePinyin = ''
  station.namePy = ''
  formRef.value?.clearValidate()
}

function openAddModal() {
  resetForm()
  modalVisible.value = true
}

function openEditModal(record) {
  station.id = record.id
  station.name = record.name
  station.namePinyin = record.namePinyin
  station.namePy = record.namePy
  modalVisible.value = true
}

async function removeStation(id) {
  try {
    const data = await deleteStation(id)
    if (data.success) {
      notification.success({ description: '删除成功' })
      const lastPage = Math.max(
        1,
        Math.ceil((pagination.total - 1) / pagination.pageSize),
      )
      await loadStations(Math.min(pagination.current, lastPage))
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
    const data = station.id
      ? await updateStation(station)
      : await saveStation(station)
    if (data.success) {
      notification.success({
        description: station.id ? '编辑成功' : '新增成功',
      })
      modalVisible.value = false
      resetForm()
      await loadStations()
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
  loadStations(1, pagination.pageSize)
})
</script>

<style scoped>
.station-page {
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

.station-table {
  min-height: 420px;
  padding: 20px;
  border: 1px solid #dce3e1;
  border-radius: 8px;
  background: #ffffff;
}

@media (max-width: 520px) {
  .page-heading {
    align-items: flex-start;
    flex-direction: column;
  }

  .station-table {
    padding: 16px;
  }

  :deep(.ant-modal) {
    max-width: calc(100vw - 32px);
  }
}
</style>
