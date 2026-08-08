<template>
  <div class="order-page">
    <header class="page-heading">
      <a-button
        aria-label="返回余票查询"
        title="返回余票查询"
        type="text"
        @click="router.push('/ticket')"
      >
        <ArrowLeftOutlined />
      </a-button>
      <h1>确认订单</h1>
    </header>

    <template v-if="dailyTrainTicket.id">
      <section class="train-summary">
        <div class="train-identity">
          <span class="train-date">{{ dailyTrainTicket.date }}</span>
          <strong>{{ dailyTrainTicket.trainCode }}</strong>
          <span>次</span>
        </div>

        <div class="route-summary">
          <div class="station-block">
            <strong>{{ dailyTrainTicket.start }}</strong>
            <span>{{ formatTime(dailyTrainTicket.startTime) }} 出发</span>
          </div>
          <div class="route-line" aria-hidden="true">
            <span></span>
            <ArrowRightOutlined />
          </div>
          <div class="station-block station-end">
            <strong>{{ dailyTrainTicket.end }}</strong>
            <span>{{ formatTime(dailyTrainTicket.endTime) }} 到达</span>
          </div>
        </div>
      </section>

      <section class="seat-section">
        <div class="section-heading">
          <h2>座位余票</h2>
          <span>共 {{ seatTypes.length }} 种席别</span>
        </div>

        <div v-if="seatTypes.length" class="seat-list">
          <div
            v-for="seatType in seatTypes"
            :key="seatType.code"
            class="seat-item"
          >
            <div>
              <strong>{{ seatType.desc }}</strong>
              <span>{{ seatType.count }} 张</span>
            </div>
            <span class="seat-price">
              ￥{{ formatPrice(seatType.price) }}
            </span>
          </div>
        </div>
        <a-empty v-else description="该车次暂无可用座位类型" />
      </section>

      <section class="passenger-section">
        <div class="section-heading">
          <h2>选择乘车人</h2>
          <span>已选择 {{ tickets.length }} 人</span>
        </div>

        <a-spin :spinning="passengerLoading">
          <a-checkbox-group
            v-if="passengerOptions.length"
            v-model:value="passengerChecks"
            :options="passengerOptions"
            class="passenger-options"
          />
          <a-empty v-else-if="!passengerLoading" description="暂无乘车人" />
        </a-spin>
      </section>

      <section v-if="tickets.length" class="ticket-section">
        <div class="section-heading">
          <h2>购票信息</h2>
          <span>最多可购买 5 张</span>
        </div>

        <a-table
          :columns="ticketColumns"
          :data-source="tickets"
          :pagination="false"
          :row-key="(record) => record.passengerId"
          :scroll="{ x: 680 }"
          size="middle"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'passengerType'">
              <a-select v-model:value="record.passengerType">
                <a-select-option
                  v-for="item in PASSENGER_TYPES"
                  :key="item.code"
                  :value="item.code"
                >
                  {{ item.desc }}
                </a-select-option>
              </a-select>
            </template>
            <template v-else-if="column.key === 'seatTypeCode'">
              <a-select v-model:value="record.seatTypeCode">
                <a-select-option
                  v-for="item in seatTypes"
                  :key="item.code"
                  :value="item.code"
                >
                  {{ item.desc }}
                </a-select-option>
              </a-select>
            </template>
          </template>
        </a-table>

        <div class="submit-row">
          <a-button size="large" type="primary" @click="submitOrder">
            提交订单
          </a-button>
        </div>
      </section>

      <a-modal
        v-model:visible="confirmVisible"
        cancel-text="返回修改"
        :confirm-loading="confirmLoading"
        ok-text="确认"
        title="请核对购票信息"
        width="760px"
        @ok="confirmOrder"
      >
        <a-table
          class="confirm-table"
          :columns="confirmColumns"
          :data-source="tickets"
          :pagination="false"
          :row-key="(record) => record.passengerId"
          size="small"
          table-layout="fixed"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'passengerType'">
              {{ passengerTypeName(record.passengerType) }}
            </template>
            <template v-else-if="column.key === 'seatTypeCode'">
              {{ seatTypeName(record.seatTypeCode) }}
            </template>
          </template>
        </a-table>

        <section class="seat-picker">
          <div class="seat-picker-heading">
            <strong>选择座位</strong>
            <span v-if="chooseSeatType !== '0'">
              已选 {{ selectedSeatKeys.length }} / {{ tickets.length }}
            </span>
          </div>

          <a-alert
            v-if="chooseSeatType === '0'"
            message="当前订单不支持选座，将由系统自动分配座位"
            show-icon
            type="info"
          />
          <template v-else>
            <div class="seat-map">
              <div
                v-for="row in seatRows"
                :key="row"
                class="seat-map-row"
              >
                <span class="seat-row-label">{{ row }}排</span>
                <label
                  v-for="column in selectableColumns"
                  :key="`${column}${row}`"
                  :class="[
                    'seat-choice',
                    { 'aisle-start': column === 'D' },
                  ]"
                >
                  <input
                    v-model="seatSelection[`${column}${row}`]"
                    :aria-label="`${row}排${column}座`"
                    type="checkbox"
                  />
                  <span>{{ column }}</span>
                </label>
              </div>
            </div>
          </template>
        </section>
      </a-modal>
    </template>

    <a-empty v-else>
      <template #description>
        <span>未找到待预订的车次</span>
      </template>
      <a-button type="primary" @click="router.push('/ticket')">
        返回余票查询
      </a-button>
    </a-empty>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { notification } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import {
  ArrowLeftOutlined,
  ArrowRightOutlined,
} from '@ant-design/icons-vue'
import {
  buildAvailableSeatTypes,
  SEAT_COLUMNS,
  SEAT_TYPES,
} from '@/constants/seat-types'
import {
  PASSENGER_TYPES,
  passengerTypeName,
} from '@/constants/passenger-types'
import { submitConfirmOrder } from '@/api/confirm-order'
import { queryMyPassengers } from '@/api/passenger'
import {
  getSession,
  SESSION_ORDER,
} from '@/utils/session-storage'

const router = useRouter()
const dailyTrainTicket = getSession(SESSION_ORDER, {})
const seatTypes = buildAvailableSeatTypes(dailyTrainTicket)
const passengers = ref([])
const passengerOptions = ref([])
const passengerChecks = ref([])
const passengerLoading = ref(false)
const tickets = ref([])
const confirmVisible = ref(false)
const confirmLoading = ref(false)
const chooseSeatType = ref('0')
const seatSelection = ref({})
const selectableColumns = computed(
  () => SEAT_COLUMNS[chooseSeatType.value] || [],
)
const seatRows = computed(() => tickets.value.length > 1 ? [1, 2] : [1])
const selectedSeatKeys = computed(() => seatRows.value.flatMap(
  (row) => selectableColumns.value
    .map((column) => `${column}${row}`)
    .filter((key) => seatSelection.value[key]),
))
const ticketColumns = [
  { title: '乘客', dataIndex: 'passengerName', key: 'passengerName', width: 100 },
  { title: '身份证', dataIndex: 'passengerIdCard', key: 'passengerIdCard', width: 220 },
  { title: '票种', key: 'passengerType', width: 130 },
  { title: '席别', key: 'seatTypeCode', width: 130 },
]
const confirmColumns = [
  { title: '乘客', dataIndex: 'passengerName', key: 'passengerName', width: 60 },
  { title: '身份证', dataIndex: 'passengerIdCard', key: 'passengerIdCard', width: 136 },
  { title: '票种', key: 'passengerType', width: 60 },
  { title: '席别', key: 'seatTypeCode', width: 60 },
]

watch(passengerChecks, (selectedPassengers) => {
  tickets.value = selectedPassengers.map((passenger) => ({
    passengerId: passenger.id,
    passengerType: passenger.type,
    passengerName: passenger.name,
    passengerIdCard: passenger.idCard,
    seatTypeCode: seatTypes[0]?.code,
    seat: null,
  }))
})

async function loadPassengers() {
  passengerLoading.value = true
  try {
    const data = await queryMyPassengers()
    if (data.success) {
      passengers.value = data.content || []
      passengerOptions.value = passengers.value.map((passenger) => ({
        label: passenger.name,
        value: passenger,
      }))
    } else {
      notification.error({
        description: data.message || '乘车人加载失败',
      })
    }
  } catch (error) {
    notification.error({
      description: error.response?.data?.message || '乘车人加载失败',
    })
  } finally {
    passengerLoading.value = false
  }
}

function seatTypeName(code) {
  return seatTypes.find((item) => item.code === code)?.desc || '-'
}

function submitOrder() {
  if (tickets.value.length > 5) {
    notification.warning({ description: '一次最多只能购买 5 张车票' })
    return
  }

  //1. 使用余票副本逐张预扣，避免前端校验修改页面上的真实余票。
  if (!hasEnoughTickets()) {
    return
  }

  //2. 只有同为一等座或同为二等座，且余票不少于20张时支持选座。
  chooseSeatType.value = calculateChooseSeatType()

  //3. 每次打开核对窗口都重新初始化，避免切换席别后残留隐藏座位。
  initializeSeatSelection()
  confirmVisible.value = true
}

function hasEnoughTickets() {
  //1. 复制席别余票，只在副本上执行预扣。
  const remainingSeatTypes = seatTypes.map((seatType) => ({ ...seatType }))

  //2. 逐张车票扣减对应席别，任一席别小于零即表示余票不足。
  for (const ticket of tickets.value) {
    const seatType = remainingSeatTypes.find(
      (item) => item.code === ticket.seatTypeCode,
    )
    if (!seatType) {
      notification.error({ description: '所选席别余票不足' })
      return false
    }
    seatType.count -= 1
    if (seatType.count < 0) {
      notification.error({
        description: `${seatType.desc}余票不足`,
      })
      return false
    }
  }
  return true
}

function calculateChooseSeatType() {
  //1. 提取并去重全部购票席别，混合席别不支持选座。
  const selectedTypes = [...new Set(
    tickets.value.map((ticket) => ticket.seatTypeCode),
  )]
  if (selectedTypes.length !== 1) {
    return '0'
  }

  //2. 只有纯一等座或纯二等座支持选座。
  const seatTypeCode = selectedTypes[0]
  if (![SEAT_TYPES.YDZ.code, SEAT_TYPES.EDZ.code].includes(seatTypeCode)) {
    return '0'
  }

  //3. 对应席别余票达到20张才开放选座。
  const seatType = seatTypes.find((item) => item.code === seatTypeCode)
  return seatType && seatType.count >= 20 ? seatTypeCode : '0'
}

function initializeSeatSelection() {
  const selection = {}
  for (const row of seatRows.value) {
    for (const column of selectableColumns.value) {
      selection[`${column}${row}`] = false
    }
  }
  seatSelection.value = selection
}

async function confirmOrder() {
  //1. 清空上一次确认结果，确保反复选座不会遗留旧座位。
  tickets.value.forEach((ticket) => {
    ticket.seat = null
  })

  //2. 完全不选座时由后端自动分配；主动选座时数量必须等于购票数。
  const seatCount = selectedSeatKeys.value.length
  if (seatCount > tickets.value.length) {
    notification.error({ description: '所选座位数大于购票数' })
    return
  }
  if (seatCount > 0 && seatCount < tickets.value.length) {
    notification.error({ description: '所选座位数小于购票数' })
    return
  }

  //3. 按界面从第一排到第二排、每排从左到右的顺序分配给乘客。
  selectedSeatKeys.value.forEach((seat, index) => {
    tickets.value[index].seat = seat
  })

  confirmLoading.value = true
  try {
    const data = await submitConfirmOrder({
      date: dailyTrainTicket.date,
      trainCode: dailyTrainTicket.trainCode,
      start: dailyTrainTicket.start,
      end: dailyTrainTicket.end,
      dailyTrainTicketId: dailyTrainTicket.id,
      tickets: tickets.value,
    })
    if (data.success) {
      confirmVisible.value = false
      notification.success({ description: '下单成功' })
    } else {
      notification.error({ description: data.message || '下单失败' })
    }
  } catch (error) {
    notification.error({
      description: error.response?.data?.message || '下单失败，请稍后再试',
    })
  } finally {
    confirmLoading.value = false
  }
}

function formatTime(value) {
  return value ? value.slice(0, 5) : '-'
}

function formatPrice(value) {
  const price = Number(value)
  return Number.isFinite(price) ? price.toFixed(2) : '-'
}

onMounted(loadPassengers)
</script>

<style scoped>
.order-page {
  color: #1f2927;
}

.page-heading,
.train-identity,
.route-summary,
.section-heading,
.seat-item,
.seat-item > div {
  display: flex;
  align-items: center;
}

.page-heading {
  min-height: 40px;
  margin-bottom: 18px;
  gap: 8px;
}

.page-heading h1,
.section-heading h2 {
  margin: 0;
  color: #17211f;
  font-weight: 700;
}

.page-heading h1 {
  font-size: 24px;
}

.train-summary {
  padding: 24px 0 28px;
  border-top: 1px solid #dce3e1;
  border-bottom: 1px solid #dce3e1;
}

.train-identity {
  margin-bottom: 24px;
  gap: 7px;
  color: #4f5c59;
}

.train-identity strong {
  color: #b73b36;
  font-size: 22px;
}

.train-date {
  margin-right: 10px;
  color: #147d72;
  font-weight: 600;
}

.route-summary {
  width: min(660px, 100%);
  justify-content: space-between;
}

.station-block {
  display: grid;
  min-width: 132px;
  gap: 5px;
}

.station-block strong {
  color: #17211f;
  font-size: 22px;
}

.station-block span {
  color: #6a7774;
}

.station-end {
  text-align: right;
}

.route-line {
  display: flex;
  min-width: 120px;
  color: #147d72;
  align-items: center;
  flex: 1;
  margin: 0 28px;
}

.route-line span {
  height: 1px;
  background: #99bcb7;
  flex: 1;
}

.seat-section,
.passenger-section,
.ticket-section {
  margin-top: 28px;
}

.section-heading {
  margin-bottom: 14px;
  justify-content: space-between;
}

.section-heading h2 {
  font-size: 17px;
}

.section-heading span {
  color: #73807d;
}

.seat-list {
  display: grid;
  border-top: 1px solid #dce3e1;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.seat-item {
  min-height: 76px;
  padding: 14px 18px;
  border-bottom: 1px solid #dce3e1;
  justify-content: space-between;
}

.seat-item:nth-child(odd) {
  border-right: 1px solid #dce3e1;
}

.seat-item > div {
  gap: 12px;
}

.seat-item strong {
  color: #17211f;
  font-size: 16px;
}

.seat-item > div span {
  color: #6a7774;
}

.seat-price {
  color: #b73b36;
  font-size: 18px;
  font-variant-numeric: tabular-nums;
  font-weight: 700;
}

.passenger-options {
  display: flex;
  min-height: 48px;
  padding: 14px 0;
  border-top: 1px solid #dce3e1;
  border-bottom: 1px solid #dce3e1;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px 24px;
}

.passenger-options :deep(.ant-checkbox-wrapper) {
  margin-left: 0;
}

.ticket-section :deep(.ant-select) {
  width: 100%;
}

.submit-row {
  display: flex;
  margin-top: 20px;
  justify-content: flex-end;
}

.submit-row .ant-btn-primary {
  min-width: 128px;
  border-color: #147d72;
  background: #147d72;
}

.confirm-table :deep(.ant-table-cell) {
  overflow-wrap: anywhere;
}

.seat-picker {
  margin-top: 22px;
  padding-top: 18px;
  border-top: 1px solid #dce3e1;
}

.seat-picker-heading,
.seat-map-row {
  display: flex;
  align-items: center;
}

.seat-picker-heading {
  margin-bottom: 14px;
  justify-content: space-between;
}

.seat-picker-heading strong {
  color: #17211f;
  font-size: 16px;
}

.seat-picker-heading span,
.seat-row-label {
  color: #73807d;
}

.seat-map {
  display: grid;
  width: fit-content;
  gap: 10px;
}

.seat-map-row {
  gap: 8px;
}

.seat-row-label {
  width: 34px;
  flex: 0 0 34px;
}

.seat-choice {
  display: block;
  width: 42px;
  height: 42px;
  cursor: pointer;
}

.seat-choice.aisle-start {
  margin-left: 18px;
}

.seat-choice input {
  position: absolute;
  width: 1px;
  height: 1px;
  opacity: 0;
}

.seat-choice span {
  display: grid;
  width: 100%;
  height: 100%;
  border: 1px solid #aebbb8;
  border-radius: 4px;
  background: #fff;
  color: #33413e;
  font-weight: 600;
  place-items: center;
  transition: border-color 0.16s ease, background 0.16s ease,
    color 0.16s ease;
}

.seat-choice input:checked + span {
  border-color: #147d72;
  background: #147d72;
  color: #fff;
}

.seat-choice input:focus-visible + span {
  outline: 2px solid #147d72;
  outline-offset: 2px;
}

@media (max-width: 640px) {
  .route-line {
    min-width: 48px;
    margin: 0 12px;
  }

  .station-block {
    min-width: 100px;
  }

  .station-block strong {
    font-size: 18px;
  }

  .section-heading {
    align-items: flex-start;
    flex-direction: column;
    gap: 4px;
  }

  .seat-list {
    grid-template-columns: 1fr;
  }

  .seat-item:nth-child(odd) {
    border-right: 0;
  }

  .confirm-table :deep(.ant-table-cell) {
    padding: 8px 6px;
  }
}
</style>
