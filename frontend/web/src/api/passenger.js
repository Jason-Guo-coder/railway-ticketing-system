import request from './request'

export function savePassenger(data) {
  return request.post('/member/passenger/save', data)
}

export function deletePassenger(id) {
  return request.delete(`/member/passenger/delete/${id}`)
}

export function updatePassenger(data) {
  return request.post('/member/passenger/update', data)
}

export function queryPassengerList(params) {
  return request.get('/member/passenger/query-list', { params })
}

export function queryMyPassengers() {
  return request.get('/member/passenger/query-mine')
}
