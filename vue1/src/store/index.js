import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    imageList: [],
    total: 0,
    loading: false,
    statistics: {
      today: 0,
      week: 0,
      month: 0
    }
  },
  mutations: {
    SET_IMAGE_LIST(state, { rows, total }) {
      state.imageList = rows
      state.total = total
    },
    SET_LOADING(state, status) {
      state.loading = status
    },
    SET_STATISTICS(state, data) {
      state.statistics = data
    }
  },
  actions: {
    async fetchImageList({ commit }, params) {
      commit('SET_LOADING', true)
      try {
        const response = await axios.get('/images', { params })
        if (response.data.code === 1) {
          commit('SET_IMAGE_LIST', response.data.data)
        } else {
          Vue.prototype.$message.error(response.data.msg || '获取数据失败')
        }
      } catch (error) {
        console.error('获取图片列表失败:', error)
      } finally {
        commit('SET_LOADING', false)
      }
    },
    async fetchStatistics({ commit }, params) {
      try {
        const response = await axios.get('/images/data', { params })
        commit('SET_STATISTICS', response.data.data)
      } catch (error) {
        console.error('获取统计数据失败:', error)
      }
    }
  }
}) 