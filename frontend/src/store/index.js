import { createStore } from 'vuex'

export default createStore({
  state: {
    member: {},
  },
  mutations: {
    setMember(state, member) {
      state.member = member
    },
  },
})
