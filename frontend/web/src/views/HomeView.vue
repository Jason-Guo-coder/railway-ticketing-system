<template>
  <div class="dashboard-page">
    <header class="page-heading">
      <div>
        <p class="welcome-label">欢迎回来</p>
        <h1>{{ member.mobile }}</h1>
      </div>
      <span class="status-badge">会员服务正常</span>
    </header>

    <section class="search-section" aria-labelledby="ticket-search-title">
      <div class="section-heading">
        <div>
          <h2 id="ticket-search-title">查询车票</h2>
          <p>输入行程信息，快速查找可售车次</p>
        </div>
        <SearchOutlined />
      </div>

      <a-form class="search-form" layout="vertical" @finish="searchTickets">
        <a-form-item label="出发地">
          <a-input v-model:value="searchForm.departure" placeholder="请输入出发地" />
        </a-form-item>
        <a-form-item label="目的地">
          <a-input v-model:value="searchForm.arrival" placeholder="请输入目的地" />
        </a-form-item>
        <a-form-item label="出发日期">
          <a-input v-model:value="searchForm.date" type="date" />
        </a-form-item>
        <a-button html-type="submit" type="primary">
          <SearchOutlined />
          查询车票
        </a-button>
      </a-form>
    </section>

    <div class="dashboard-grid">
      <section class="trip-section" aria-labelledby="recent-trip-title">
        <div class="section-title-row">
          <h2 id="recent-trip-title">近期行程</h2>
          <router-link to="/ticket">查询车次</router-link>
        </div>
        <a-empty description="暂无近期行程" />
      </section>

      <section class="quick-section" aria-labelledby="quick-action-title">
        <h2 id="quick-action-title">常用功能</h2>
        <router-link class="quick-link" to="/ticket">
          <span class="quick-icon"><SearchOutlined /></span>
          <span>
            <strong>车票查询</strong>
            <small>查询出发日期与车次</small>
          </span>
          <RightOutlined />
        </router-link>
        <router-link class="quick-link" to="/passenger">
          <span class="quick-icon"><TeamOutlined /></span>
          <span>
            <strong>乘车人管理</strong>
            <small>查看常用乘车人信息</small>
          </span>
          <RightOutlined />
        </router-link>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { RightOutlined, SearchOutlined, TeamOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const store = useStore()
const member = computed(() => store.state.member)
const searchForm = reactive({
  departure: '',
  arrival: '',
  date: '',
})

function searchTickets() {
  router.push({
    path: '/ticket',
    query: { ...searchForm },
  })
}
</script>

<style scoped>
.dashboard-page {
  color: #1f2927;
}

.page-heading,
.section-heading,
.section-title-row,
.quick-link {
  display: flex;
  align-items: center;
}

.page-heading {
  min-height: 64px;
  margin-bottom: 20px;
  justify-content: space-between;
}

.welcome-label,
.page-heading h1,
.section-heading h2,
.section-heading p,
.section-title-row h2,
.quick-section h2 {
  margin: 0;
}

.welcome-label {
  color: #6a7774;
  font-size: 13px;
}

.page-heading h1 {
  color: #17211f;
  font-size: 24px;
  font-weight: 700;
}

.status-badge {
  padding: 5px 10px;
  border: 1px solid #b7d6d1;
  border-radius: 6px;
  color: #126f66;
  background: #edf7f5;
  font-size: 12px;
}

.search-section,
.trip-section,
.quick-section {
  border: 1px solid #dce3e1;
  border-radius: 8px;
  background: #ffffff;
}

.search-section {
  padding: 20px;
}

.section-heading {
  margin-bottom: 18px;
  justify-content: space-between;
}

.section-heading h2,
.section-title-row h2,
.quick-section h2 {
  color: #17211f;
  font-size: 16px;
  font-weight: 700;
}

.section-heading p {
  margin-top: 3px;
  color: #6a7774;
  font-size: 13px;
}

.section-heading > .anticon {
  color: #147d72;
  font-size: 22px;
}

.search-form {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr)) auto;
  gap: 16px;
  align-items: end;
}

.search-form :deep(.ant-form-item) {
  margin: 0;
}

.search-form .ant-btn-primary {
  border-color: #147d72;
  background: #147d72;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) minmax(280px, 0.7fr);
  gap: 20px;
  margin-top: 20px;
}

.trip-section,
.quick-section {
  min-height: 300px;
  padding: 20px;
}

.section-title-row {
  margin-bottom: 34px;
  justify-content: space-between;
}

.section-title-row a {
  color: #147d72;
  font-size: 13px;
}

.quick-section h2 {
  margin-bottom: 14px;
}

.quick-link {
  min-height: 72px;
  gap: 12px;
  padding: 12px 4px;
  border-bottom: 1px solid #edf0ef;
  color: #25302e;
}

.quick-link:hover {
  color: #126f66;
}

.quick-icon {
  display: grid;
  width: 38px;
  height: 38px;
  flex: 0 0 38px;
  border-radius: 6px;
  color: #126f66;
  background: #e8f3f1;
  place-items: center;
}

.quick-link > span:nth-child(2) {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
}

.quick-link strong {
  font-weight: 600;
}

.quick-link small {
  margin-top: 2px;
  color: #788481;
}

.quick-link > .anticon {
  color: #8b9694;
}

@media (max-width: 900px) {
  .search-form,
  .dashboard-grid {
    grid-template-columns: 1fr;
  }

  .trip-section,
  .quick-section {
    min-height: 260px;
  }
}

@media (max-width: 520px) {
  .page-heading {
    align-items: flex-start;
    flex-direction: column;
    gap: 10px;
  }
}
</style>
