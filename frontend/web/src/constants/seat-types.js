export const SEAT_TYPES = Object.freeze({
  YDZ: { code: '1', desc: '一等座' },
  EDZ: { code: '2', desc: '二等座' },
  RW: { code: '3', desc: '软卧' },
  YW: { code: '4', desc: '硬卧' },
})

export const SEAT_COLUMNS = Object.freeze({
  [SEAT_TYPES.YDZ.code]: Object.freeze(['A', 'C', 'D', 'F']),
  [SEAT_TYPES.EDZ.code]: Object.freeze(['A', 'B', 'C', 'D', 'F']),
})

export function buildAvailableSeatTypes(ticket = {}) {
  return Object.entries(SEAT_TYPES).flatMap(([type, seatType]) => {
    const key = type.toLowerCase()
    const count = Number(ticket[key])

    // -1表示该车次没有此座位类型，0表示有该类型但已经售罄。
    if (!Number.isFinite(count) || count < 0) {
      return []
    }

    return [{
      type,
      ...seatType,
      count,
      price: ticket[`${key}Price`],
    }]
  })
}
