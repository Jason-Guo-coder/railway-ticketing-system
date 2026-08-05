import request from './request'

export function saveTrainCarriage(data) {
  return request.post('/business/admin/train-carriage/save', data)
}

export function deleteTrainCarriage(id) {
  return request.delete(`/business/admin/train-carriage/delete/${id}`)
}

export function updateTrainCarriage(data) {
  return request.post('/business/admin/train-carriage/update', data)
}

export function queryTrainCarriageList(params) {
  return request.get('/business/admin/train-carriage/query-list', { params })
}
