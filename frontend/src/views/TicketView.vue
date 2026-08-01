<template>
  <div class="content-page">
    <header class="page-heading">
      <h1>车票查询</h1>
      <p>查询指定日期的可售车次</p>
    </header>

    <section class="filter-bar">
      <a-form class="ticket-form" layout="inline" @finish="searched = true">
        <a-form-item label="出发地">
          <a-input v-model:value="form.departure" placeholder="请输入出发地" />
        </a-form-item>
        <a-form-item label="目的地">
          <a-input v-model:value="form.arrival" placeholder="请输入目的地" />
        </a-form-item>
        <a-form-item label="出发日期">
          <a-input v-model:value="form.date" type="date" />
        </a-form-item>
        <a-button html-type="submit" type="primary">
          <SearchOutlined />
          查询
        </a-button>
      </a-form>
    </section>

    <section class="result-section">
      <div class="result-heading">
        <h2>查询结果</h2>
        <span v-if="searched">0 个车次</span>
      </div>
      <a-empty :description="searched ? '暂无符合条件的车次' : '请先设置查询条件'" />
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { SearchOutlined } from '@ant-design/icons-vue'

const route = useRoute()
const searched = ref(false)
const form = reactive({
  departure: route.query.departure || '',
  arrival: route.query.arrival || '',
  date: route.query.date || '',
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
.result-heading h2 {
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

.filter-bar,
.result-section {
  border: 1px solid #dce3e1;
  border-radius: 8px;
  background: #ffffff;
}

.filter-bar {
  padding: 20px;
}

.ticket-form {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.ticket-form :deep(.ant-form-item) {
  margin: 0;
}

.ticket-form .ant-btn-primary {
  border-color: #147d72;
  background: #147d72;
}

.result-section {
  min-height: 320px;
  margin-top: 20px;
  padding: 20px;
}

.result-heading {
  display: flex;
  margin-bottom: 28px;
  align-items: center;
  justify-content: space-between;
}

.result-heading h2 {
  font-size: 16px;
}

.result-heading span {
  color: #6a7774;
  font-size: 13px;
}

@media (max-width: 860px) {
  .ticket-form {
    align-items: stretch;
    flex-direction: column;
  }

  .ticket-form :deep(.ant-form-item),
  .ticket-form :deep(.ant-form-item-control),
  .ticket-form :deep(.ant-input) {
    width: 100%;
  }
}
</style>
