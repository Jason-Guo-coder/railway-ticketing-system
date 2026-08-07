import request from './request'

export function queryDailyTrainSeatList(params) {
  return request.get('/business/admin/daily-train-seat/query-list', {
    params,
  })
}
