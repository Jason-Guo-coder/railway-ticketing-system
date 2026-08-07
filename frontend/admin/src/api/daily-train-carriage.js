import request from './request'

export function saveDailyTrainCarriage(data) {
  return request.post('/business/admin/daily-train-carriage/save', data)
}

export function deleteDailyTrainCarriage(id) {
  return request.delete(`/business/admin/daily-train-carriage/delete/${id}`)
}

export function updateDailyTrainCarriage(data) {
  return request.post('/business/admin/daily-train-carriage/update', data)
}

export function queryDailyTrainCarriageList(params) {
  return request.get('/business/admin/daily-train-carriage/query-list', {
    params,
  })
}
