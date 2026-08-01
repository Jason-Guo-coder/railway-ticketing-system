import { createStore } from 'vuex'

const MEMBER_STORAGE_KEY = 'member'

function loadMember() {
  const value = window.sessionStorage.getItem(MEMBER_STORAGE_KEY)
  if (!value) {
    return {}
  }

  try {
    return JSON.parse(value)
  } catch {
    window.sessionStorage.removeItem(MEMBER_STORAGE_KEY)
    return {}
  }
}

export default createStore({
  state: {
    member: loadMember(),
  },
  mutations: {
    setMember(state, member) {
      state.member = member
      window.sessionStorage.setItem(
        MEMBER_STORAGE_KEY,
        JSON.stringify(member),
      )
    },
    clearMember(state) {
      state.member = {}
      window.sessionStorage.removeItem(MEMBER_STORAGE_KEY)
    },
  },
})
