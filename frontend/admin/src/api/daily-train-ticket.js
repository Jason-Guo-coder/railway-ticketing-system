import request from './request'

export function queryDailyTrainTicketList(params) {
  return request.get('/business/admin/daily-train-ticket/query-list', {
    params,
  })
}
