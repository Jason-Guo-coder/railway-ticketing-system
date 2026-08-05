import request from './request'

export function saveStation(data) {
  return request.post('/business/admin/station/save', data)
}

export function deleteStation(id) {
  return request.delete(`/business/admin/station/delete/${id}`)
}

export function updateStation(data) {
  return request.post('/business/admin/station/update', data)
}

export function queryStationList(params) {
  return request.get('/business/admin/station/query-list', { params })
}

export function queryAllStations() {
  return request.get('/business/admin/station/query-all')
}
