import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: "/",
            name: 'register',
            component: () => import('../views/register/Index.vue')
        },
        {
            path: '/home',
            name: 'home',
            meta: {
                title: "主页",
            },
            component: () => import('../views/home/Index.vue')
        },
        {
            path: '/login',
            name: 'login',
            meta: {
                title: '用户登录'
            },
            component: () => import('../views/login/Login.vue')
        }
    ]
})

export default router
