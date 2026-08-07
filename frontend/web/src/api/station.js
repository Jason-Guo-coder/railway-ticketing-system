import request from './request'

export function queryAllStations() {
  return request.get('/business/station/query-all')
}
