<template>
  <a-select
    :allow-clear="allowClear"
    :loading="loading"
    :placeholder="placeholder"
    :style="{ width }"
    :value="modelValue"
    option-filter-prop="label"
    show-search
    @change="handleChange"
  >
    <a-select-option
      v-for="station in stations"
      :key="station.id"
      :label="`${station.name} ${station.namePinyin} ${station.namePy}`"
      :value="station.name"
    >
      {{ station.name }} · {{ station.namePinyin }}
    </a-select-option>
  </a-select>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { notification } from 'ant-design-vue'
import { queryAllStations } from '@/api/station'

defineProps({
  modelValue: {
    type: String,
    default: undefined,
  },
  width: {
    type: String,
    default: '100%',
  },
  allowClear: {
    type: Boolean,
    default: true,
  },
  placeholder: {
    type: String,
    default: '请选择车站',
  },
})
const emit = defineEmits(['update:modelValue'])

const loading = ref(false)
const stations = ref([])

async function loadStations() {
  loading.value = true
  try {
    const data = await queryAllStations()
    if (data.success) {
      stations.value = data.content || []
    } else {
      notification.error({
        description: data.message || '车站列表加载失败',
      })
    }
  } catch (error) {
    notification.error({
      description: error.response?.data?.message || '车站列表加载失败',
    })
  } finally {
    loading.value = false
  }
}

function handleChange(value) {
  emit('update:modelValue', value)
}

onMounted(loadStations)
</script>
