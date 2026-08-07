export const PASSENGER_TYPES = Object.freeze([
  { code: '1', desc: '成人' },
  { code: '2', desc: '儿童' },
  { code: '3', desc: '学生' },
])

export function passengerTypeName(code) {
  return PASSENGER_TYPES.find((item) => item.code === code)?.desc || '-'
}
