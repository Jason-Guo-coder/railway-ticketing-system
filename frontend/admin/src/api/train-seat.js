import request from './request'

export function queryTrainSeatList(params) {
  return request.get('/business/admin/train-seat/query-list', { params })
}
