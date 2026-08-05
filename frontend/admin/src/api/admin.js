import request from './request'

export function loginAdmin(data) {
  return request.post('/business/admin/login', data)
}
