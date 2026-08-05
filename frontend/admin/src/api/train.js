import request from './request'

export function saveTrain(data) {
  return request.post('/business/admin/train/save', data)
}

export function deleteTrain(id) {
  return request.delete(`/business/admin/train/delete/${id}`)
}

export function updateTrain(data) {
  return request.post('/business/admin/train/update', data)
}

export function queryTrainList(params) {
  return request.get('/business/admin/train/query-list', { params })
}
