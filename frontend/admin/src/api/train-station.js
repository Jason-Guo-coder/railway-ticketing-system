import request from './request'

export function saveTrainStation(data) {
  return request.post('/business/admin/train-station/save', data)
}

export function deleteTrainStation(id) {
  return request.delete(`/business/admin/train-station/delete/${id}`)
}

export function updateTrainStation(data) {
  return request.post('/business/admin/train-station/update', data)
}

export function queryTrainStationList(params) {
  return request.get('/business/admin/train-station/query-list', { params })
}
