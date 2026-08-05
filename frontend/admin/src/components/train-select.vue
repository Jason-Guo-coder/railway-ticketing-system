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
      v-for="item in trains"
      :key="item.id"
      :label="`${item.code} ${item.start} ${item.end}`"
      :value="item.code"
    >
      {{ item.code }} · {{ item.start }} → {{ item.end }}
    </a-select-option>
  </a-select>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { notification } from 'ant-design-vue'
import { queryAllTrains } from '@/api/train'

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
    default: '请选择车次',
  },
})
const emit = defineEmits(['update:modelValue', 'change'])

const loading = ref(false)
const trains = ref([])
const selectStyle = computed(() => ({
  width: typeof props.width === 'number'
    ? `${props.width}px`
    : props.width,
}))

async function loadTrains() {
  loading.value = true
  try {
    const data = await queryAllTrains()
    if (data.success) {
      trains.value = data.content || []
    } else {
      notification.error({
        description: data.message || '车次列表加载失败',
      })
    }
  } catch (error) {
    notification.error({
      description: error.response?.data?.message || '车次列表加载失败',
    })
  } finally {
    loading.value = false
  }
}

function handleChange(value) {
  emit('update:modelValue', value)
  const train = trains.value.find((item) => item.code === value)
  emit('change', train || null)
}

onMounted(loadTrains)
</script>
