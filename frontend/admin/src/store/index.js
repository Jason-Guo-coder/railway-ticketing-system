import { createStore } from 'vuex'

const ADMIN_STORAGE_KEY = 'admin'

function loadAdmin() {
  const value = window.sessionStorage.getItem(ADMIN_STORAGE_KEY)
  if (!value) {
    return {}
  }

  try {
    return JSON.parse(value)
  } catch {
    window.sessionStorage.removeItem(ADMIN_STORAGE_KEY)
    return {}
  }
}

export default createStore({
  state: {
    admin: loadAdmin(),
  },
  mutations: {
    setAdmin(state, admin) {
      state.admin = admin
      window.sessionStorage.setItem(
        ADMIN_STORAGE_KEY,
        JSON.stringify(admin),
      )
    },
    clearAdmin(state) {
      state.admin = {}
      window.sessionStorage.removeItem(ADMIN_STORAGE_KEY)
    },
  },
})
