import request from './request'

export function registerMember(mobile) {
  return request.post('/member/member/register', { mobile })
}

export function loginMember(data) {
  return request.post('/member/member/login', data)
}
