import request from './request'

// 新增Quartz任务
export function addJob(data) {
  return request.post('/batch/admin/job/add', data)
}

// 删除Quartz任务
export function deleteJob(data) {
  return request.post('/batch/admin/job/delete', data)
}

// 修改Quartz任务
export function updateJob(data) {
  return request.post('/batch/admin/job/update', data)
}

// 查询全部Quartz任务
export function queryJobList() {
  return request.get('/batch/admin/job/query-list')
}

// 暂停Quartz任务
export function pauseJob(data) {
  return request.post('/batch/admin/job/pause', data)
}

// 恢复Quartz任务
export function resumeJob(data) {
  return request.post('/batch/admin/job/resume', data)
}

// 立即执行一次Quartz任务
export function runJob(data) {
  return request.post('/batch/admin/job/run', data)
}
