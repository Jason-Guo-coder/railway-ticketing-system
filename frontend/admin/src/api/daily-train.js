import request from './request'

export function saveDailyTrain(data) {
  return request.post('/business/admin/daily-train/save', data)
}

export function deleteDailyTrain(id) {
  return request.delete(`/business/admin/daily-train/delete/${id}`)
}

export function updateDailyTrain(data) {
  return request.post('/business/admin/daily-train/update', data)
}

export function queryDailyTrainList(params) {
  return request.get('/business/admin/daily-train/query-list', { params })
}

export function generateDailyTrains(date) {
  return request.post(
    `/business/admin/daily-train/gen-daily/${date}`,
    null,
    { timeout: 120000 },
  )
}
