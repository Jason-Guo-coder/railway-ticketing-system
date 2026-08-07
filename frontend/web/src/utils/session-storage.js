export const SESSION_ORDER = 'train.order'
export const SESSION_TICKET_QUERY = 'train.ticket.query'

export function setSession(key, value) {
  window.sessionStorage.setItem(key, JSON.stringify(value))
}

export function getSession(key, fallback = {}) {
  const value = window.sessionStorage.getItem(key)
  if (!value) {
    return fallback
  }

  try {
    return JSON.parse(value)
  } catch {
    window.sessionStorage.removeItem(key)
    return fallback
  }
}
