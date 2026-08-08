import request from './request'

export function submitConfirmOrder(data) {
  return request.post('/business/confirm-order/do', data)
}
