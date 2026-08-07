import request from './request'

export function saveDailyTrainSeat(data) {
  return request.post('/business/admin/daily-train-seat/save', data)
}

export function deleteDailyTrainSeat(id) {
  return request.delete(`/business/admin/daily-train-seat/delete/${id}`)
}

export function updateDailyTrainSeat(data) {
  return request.post('/business/admin/daily-train-seat/update', data)
}

export function queryDailyTrainSeatList(params) {
  return request.get('/business/admin/daily-train-seat/query-list', {
    params,
  })
}
