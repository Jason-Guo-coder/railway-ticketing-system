<template>
  <div class="job-page">
    <header class="page-heading">
      <h1>定时任务</h1>
      <div class="toolbar-actions">
        <a-button
          aria-label="刷新定时任务列表"
          title="刷新列表"
          type="text"
          @click="loadJobs"
        >
          <ReloadOutlined />
        </a-button>
        <a-button type="primary" @click="openAddModal">
          <PlusOutlined />
          新增任务
        </a-button>
      </div>
    </header>

    <section class="job-table">
      <a-table
        :columns="columns"
        :data-source="jobs"
        :loading="loading"
        :pagination="false"
        :row-key="(record) => `${record.group}:${record.name}`"
        table-layout="fixed"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'name'">
            <a-tooltip :title="record.name">
              <span class="ellipsis-cell">{{ record.name }}</span>
            </a-tooltip>
          </template>
          <template v-else-if="column.key === 'description'">
            <a-tooltip :title="record.description || '-'">
              <span class="ellipsis-cell">{{ record.description || '-' }}</span>
            </a-tooltip>
          </template>
          <template v-else-if="column.key === 'fireTime'">
            <div class="fire-time-cell">
              <span>上次 {{ record.previousFireTime || '-' }}</span>
              <span>下次 {{ record.nextFireTime || '-' }}</span>
            </div>
          </template>
          <template v-else-if="column.key === 'state'">
            <a-tag :color="stateInfo(record.state).color">
              {{ stateInfo(record.state).label }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'operation'">
            <div class="row-actions">
              <a-popconfirm
                cancel-text="取消"
                ok-text="确认"
                title="确认立即执行一次该任务吗？"
                @confirm="executeJob(record)"
              >
                <a-button type="link">立即执行</a-button>
              </a-popconfirm>
              <a-button
                v-if="record.state === 'PAUSED' || record.state === 'ERROR'"
                type="link"
                @click="changeJobState(record, 'resume')"
              >
                恢复
              </a-button>
              <a-button
                v-else
                type="link"
                @click="changeJobState(record, 'pause')"
              >
                暂停
              </a-button>
              <a-button type="link" @click="openEditModal(record)">
                编辑
              </a-button>
              <a-popconfirm
                cancel-text="取消"
                ok-text="确认"
                title="删除后不可恢复，确认删除吗？"
                @confirm="removeJob(record)"
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
      :title="editing ? '编辑任务' : '新增任务'"
      cancel-text="取消"
      ok-text="保存"
      @cancel="resetForm"
      @ok="save"
    >
      <a-form
        ref="formRef"
        :label-col="{ span: 6 }"
        :model="job"
        :rules="rules"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="任务类" name="name">
          <a-input
            v-model:value="job.name"
            :disabled="editing"
            :maxlength="250"
            placeholder="请输入任务类全限定名"
          />
        </a-form-item>
        <a-form-item label="任务分组" name="group">
          <a-input
            v-model:value="job.group"
            :disabled="editing"
            :maxlength="190"
            placeholder="请输入任务分组"
          />
        </a-form-item>
        <a-form-item label="任务描述" name="description">
          <a-input
            v-model:value="job.description"
            :maxlength="250"
            placeholder="请输入任务描述"
          />
        </a-form-item>
        <a-form-item label="Cron 表达式" name="cronExpression">
          <a-input
            v-model:value="job.cronExpression"
            placeholder="例如：0/5 * * * * ?"
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
import {
  addJob,
  deleteJob,
  pauseJob,
  queryJobList,
  resumeJob,
  runJob,
  updateJob,
} from '@/api/job'

// Quartz触发器状态及其页面展示样式
const stateMap = {
  NORMAL: { label: '正常', color: 'green' },
  PAUSED: { label: '已暂停', color: 'orange' },
  BLOCKED: { label: '执行中', color: 'blue' },
  ERROR: { label: '异常', color: 'red' },
  COMPLETE: { label: '已完成', color: 'default' },
  NONE: { label: '无触发器', color: 'default' },
}
const columns = [
  { title: '分组', dataIndex: 'group', key: 'group', width: 80 },
  { title: '任务类', dataIndex: 'name', key: 'name', width: 210 },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    width: 110,
    responsive: ['xl'],
  },
  { title: '状态', dataIndex: 'state', key: 'state', width: 78 },
  {
    title: 'Cron 表达式',
    dataIndex: 'cronExpression',
    key: 'cronExpression',
    width: 132,
  },
  {
    title: '执行时间',
    dataIndex: 'fireTime',
    key: 'fireTime',
    width: 150,
    responsive: ['xl'],
  },
  { title: '操作', key: 'operation', width: 230 },
]
const formRef = ref()
const jobs = ref([])
const loading = ref(false)
const saving = ref(false)
const modalVisible = ref(false)
const editing = ref(false)
const job = reactive({
  name: undefined,
  group: 'DEFAULT',
  description: '',
  cronExpression: '',
})
const rules = {
  name: [{ required: true, message: '请选择任务类' }],
  group: [{ required: true, message: '请输入任务分组' }],
  cronExpression: [{ required: true, message: '请输入 Cron 表达式' }],
}

function stateInfo(state) {
  // 将Quartz状态转换为页面标签
  return stateMap[state] || { label: state || '-', color: 'default' }
}

function keyPayload(record) {
  // 操作任务时只提交任务唯一标识
  return {
    name: record.name,
    group: record.group,
  }
}

function showError(error, fallback) {
  // 统一显示接口失败提示
  notification.error({
    description: error.response?.data?.message || fallback,
  })
}

async function loadJobs() {
  // 查询Quartz中保存的任务列表
  loading.value = true
  try {
    const data = await queryJobList()
    if (data.success) {
      jobs.value = data.content || []
    } else {
      notification.error({ description: data.message || '查询失败' })
    }
  } catch (error) {
    showError(error, '查询失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

function resetForm() {
  // 清空新增或编辑表单
  editing.value = false
  job.name = undefined
  job.group = 'DEFAULT'
  job.description = ''
  job.cronExpression = ''
  formRef.value?.clearValidate()
}

function openAddModal() {
  // 打开新增任务弹窗
  resetForm()
  modalVisible.value = true
}

function openEditModal(record) {
  // 将列表中的任务信息放入编辑表单
  editing.value = true
  job.name = record.name
  job.group = record.group
  job.description = record.description || ''
  job.cronExpression = record.cronExpression
  modalVisible.value = true
}

async function save() {
  //1. 先校验表单
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  saving.value = true
  try {
    //2. 根据当前模式调用新增或编辑接口
    const action = editing.value ? updateJob : addJob
    const data = await action({ ...job })
    if (data.success) {
      notification.success({
        description: editing.value ? '编辑成功' : '新增成功',
      })
      modalVisible.value = false
      resetForm()
      await loadJobs()
    } else {
      notification.error({ description: data.message || '保存失败' })
    }
  } catch (error) {
    showError(error, '保存失败，请稍后再试')
  } finally {
    saving.value = false
  }
}

async function removeJob(record) {
  // 删除任务及其关联的Cron触发器
  try {
    const data = await deleteJob(keyPayload(record))
    if (data.success) {
      notification.success({ description: '删除成功' })
      await loadJobs()
    } else {
      notification.error({ description: data.message || '删除失败' })
    }
  } catch (error) {
    showError(error, '删除失败，请稍后再试')
  }
}

async function changeJobState(record, action) {
  // 根据目标状态调用暂停或恢复接口
  try {
    const data = action === 'resume'
      ? await resumeJob(keyPayload(record))
      : await pauseJob(keyPayload(record))
    if (data.success) {
      notification.success({
        description: action === 'resume' ? '恢复成功' : '暂停成功',
      })
      await loadJobs()
    } else {
      notification.error({ description: data.message || '操作失败' })
    }
  } catch (error) {
    showError(error, '操作失败，请稍后再试')
  }
}

async function executeJob(record) {
  // 手工补偿：立即执行一次，不修改原有Cron计划
  try {
    const data = await runJob(keyPayload(record))
    if (data.success) {
      notification.success({ description: '任务已立即执行' })
      await loadJobs()
    } else {
      notification.error({ description: data.message || '执行失败' })
    }
  } catch (error) {
    showError(error, '执行失败，请稍后再试')
  }
}

onMounted(loadJobs)
</script>

<style scoped>
.job-page {
  color: #1f2927;
}

.page-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.page-heading h1 {
  margin: 0;
  font-size: 22px;
  font-weight: 650;
}

.toolbar-actions,
.row-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

.job-table {
  width: 100%;
}

.job-table :deep(.ant-table) {
  table-layout: fixed;
}

.job-table :deep(.ant-table-cell) {
  overflow: hidden;
  white-space: nowrap;
}

.ellipsis-cell {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fire-time-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
  overflow: hidden;
  font-size: 12px;
  line-height: 1.35;
  white-space: nowrap;
}

.row-actions {
  flex-wrap: wrap;
  line-height: 1.2;
}

.row-actions :deep(.ant-btn-link) {
  padding-right: 4px;
  padding-left: 4px;
}

@media (max-width: 640px) {
  .page-heading {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
