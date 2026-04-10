import { createRouter, createWebHistory } from 'vue-router'
import { useManagerStore } from '@/store'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('../views/layout/index.vue'),
      redirect: '/home',
      children: [
        { path: '/home', name: 'home', component: () => import('../views/home/index.vue') },
        { path: '/bookCategory', name: 'bookCategory', component: () => import('../views/bookCategory/index.vue') },
        { path: '/bookCategory/update', name: 'bookCategoryUpdate', component: () => import('../views/bookCategory/update.vue') },
        { path: '/book', name: 'book', component: () => import('../views/book/index.vue') },
        { path: '/book/update', name: 'bookUpdate', component: () => import('../views/book/update.vue') },
        { path: '/readerCategory', name: 'readerCategory', component: () => import('../views/readerCategory/index.vue') },
        { path: '/readerCategory/update', name: 'readerCategoryUpdate', component: () => import('../views/readerCategory/update.vue') },
        { path: '/reader', name: 'reader', component: () => import('../views/reader/index.vue') },
        { path: '/reader/update', name: 'readerUpdate', component: () => import('../views/reader/update.vue') },
        { path: '/lendReturn', name: 'lendReturn', component: () => import('../views/lendReturn/index.vue') },
        { path: '/lendReturn/add', name: 'lendReturnAdd', component: () => import('../views/lendReturn/add.vue') },
        { path: '/lendReturn/update', name: 'lendReturnUpdate', component: () => import('../views/lendReturn/update.vue') },
        { path: '/manager', name: 'manager', component: () => import('../views/manager/index.vue') },
      ],
    },
    { path: '/reader-client', name: 'readerClient', component: () => import('../views/readerClient/index.vue') },
    { path: '/login', name: 'login', component: () => import('../views/login/index.vue') },
    { path: '/reg', name: 'reg', component: () => import('../views/reg/index.vue') },
    { path: '/reader-reg', name: 'readerReg', component: () => import('../views/readerReg/index.vue') },
  ],
})

router.beforeEach((to) => {
  const store = useManagerStore()
  const storedRole = localStorage.getItem('loginRole') as typeof store.loginRole
  const storedToken = localStorage.getItem('token')
  const role = store.loginRole || storedRole
  const managerToken = store.managerInfo?.token || storedToken
  const readerToken = store.readerInfo?.token || storedToken
  const publicPaths = ['/login', '/reg', '/reader-reg']

  if (publicPaths.includes(to.path)) {
    return true
  }

  if (to.path.startsWith('/reader-client')) {
    if (role === 'reader' && readerToken) return true
    return '/login'
  }

  if (role === 'manager' && managerToken) {
    return true
  }

  if (role === 'reader' && readerToken) {
    return '/reader-client'
  }

  return '/login'
})

export default router
