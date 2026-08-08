import request from './request'

export function queryConfirmOrderList(params) {
  return request.get('/business/admin/confirm-order/query-list', { params })
}
