<template>
  <a-select
    :allow-clear="allowClear"
    :loading="loading"
    :placeholder="placeholder"
    :style="selectStyle"
    :value="modelValue"
    option-filter-prop="label"
    show-search
    @change="handleChange"
  >
    <a-select-option
      v-for="item in stations"
      :key="item.id"
      :label="`${item.name} ${item.namePinyin} ${item.namePy}`"
      :value="item.name"
    >
      {{ item.name }} · {{ item.namePinyin }}
    </a-select-option>
  </a-select>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { notification } from 'ant-design-vue'
import { queryAllStations } from '@/api/station'

const props = defineProps({
  modelValue: {
    type: String,
    default: undefined,
  },
  width: {
    type: [String, Number],
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
const emit = defineEmits(['update:modelValue', 'change'])

const loading = ref(false)
const stations = ref([])
const selectStyle = computed(() => ({
  width: typeof props.width === 'number'
    ? `${props.width}px`
    : props.width,
}))

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
  const station = stations.value.find((item) => item.name === value)
  emit('change', station || null)
}

onMounted(loadStations)
</script>
