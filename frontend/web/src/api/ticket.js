import request from './request'

export function queryTickets(params) {
  return request.get('/business/daily-train-ticket/query-list', { params })
}
