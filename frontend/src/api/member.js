import request from './request'

export function sendLoginCode(mobile) {
  return request.post('/member/member/send-code', { mobile })
}

export function loginMember(data) {
  return request.post('/member/member/login', data)
}
