import request from './request'

export function saveDailyTrainStation(data) {
  return request.post('/business/admin/daily-train-station/save', data)
}

export function deleteDailyTrainStation(id) {
  return request.delete(`/business/admin/daily-train-station/delete/${id}`)
}

export function updateDailyTrainStation(data) {
  return request.post('/business/admin/daily-train-station/update', data)
}

export function queryDailyTrainStationList(params) {
  return request.get('/business/admin/daily-train-station/query-list', {
    params,
  })
}
