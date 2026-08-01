<template>
  <div class="content-page">
    <header class="page-heading">
      <h1>乘车人管理</h1>
      <p>维护购票时使用的乘车人信息</p>
    </header>

    <section class="passenger-section">
      <div class="section-toolbar">
        <h2>常用乘车人</h2>
      </div>
      <a-empty description="暂无乘车人" />
    </section>
  </div>
</template>

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
  margin-bottom: 40px;
}

.section-toolbar h2 {
  font-size: 16px;
}
</style>
