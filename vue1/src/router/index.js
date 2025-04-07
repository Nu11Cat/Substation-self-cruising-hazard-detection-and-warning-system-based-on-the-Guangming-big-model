import Vue from 'vue'
import VueRouter from 'vue-router'
import ImageDetection from '../views/ImageDetection.vue'
import ImageList from '../views/ImageList.vue'
import ImageStatistics from '../views/Statistics.vue'
import ModelManagement from '../views/ModelManagement.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/image-detection'
  },
  {
    path: '/image-detection',
    name: 'ImageDetection',
    component: ImageDetection
  },
  {
    path: '/image-list',
    name: 'ImageList',
    component: ImageList
  },
  {
    path: '/statistics',
    name: 'ImageStatistics',
    component: ImageStatistics
  },
  {
    path: '/settings',
    name: 'SystemSettings',
    component: () => import('../views/Settings.vue')
  },
  {
    path: '/model-management',
    name: 'ModelManagement',
    component: ModelManagement
  }
]

const router = new VueRouter({
  routes
})

export default router 