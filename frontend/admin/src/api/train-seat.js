import request from './request'

export function saveTrainSeat(data) {
  return request.post('/business/admin/train-seat/save', data)
}

export function deleteTrainSeat(id) {
  return request.delete(`/business/admin/train-seat/delete/${id}`)
}

export function updateTrainSeat(data) {
  return request.post('/business/admin/train-seat/update', data)
}

export function queryTrainSeatList(params) {
  return request.get('/business/admin/train-seat/query-list', { params })
}
