import {createRouter, createWebHashHistory} from "vue-router"

const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {
            path: '/',
            redirect: '/login',
        },
        {
            path: '/login',
            component: () => import('../views/users/Login.vue'),
            meta: { title: '用户登录' }
        },
        {
            path: '/register',
            component: () => import('../views/users/Register.vue'),
            meta: { title: '用户注册' }
        },
        {
            path: '/home',
            redirect: '/dashboard',
            component: () => import('../views/Home.vue'),
            children: [
                {
                    path: 'dashboard',
                    name: 'Dashboard',
                    component: () => import('../views/users/Dashboard.vue'),
                    meta: {title: '个人信息'}
                },
            ]
        },
        {
            path: '/home',
            redirect: '/cart',
            component: () => import('../views/Home.vue'),
            children: [
                {
                    path: 'cart',
                    name: 'Cart',
                    component: () => import('../views/users/Cart.vue'),
                    meta: {title: '购物车'}
                },
            ]
        },
        {
            path: '/home',
            redirect: '/allproduct',
            component: () => import('../views/Home.vue'),
            children: [
                {
                    path: '/allproduct',
                    name: 'AllProduct',
                    component: () => import('../views/product/AllProduct.vue'),
                    meta: {title: '所有商品'}

                },
            ]
        },
        {
            path: '/home',
            redirect: '/productdetail',
            component: () => import('../views/Home.vue'),
            children: [
                {
                    path: '/productdetail/:id',  // 添加动态参数:id
                    name: 'ProductDetail',
                    component: () => import('../views/product/ProductDetail.vue'),
                    meta: {title: '商品详情'},
                    props: true  // 将路由参数作为props传递给组件
                },
            ]
        },
        {
            path: '/home',
            redirect: '/modifyproduct',
            component: () => import('../views/Home.vue'),
            children: [
                {
                    path: '/modifyproduct/:id',  // 添加参数id
                    name: 'ModifyProduct',
                    component: () => import('../views/product/ModifyProduct.vue'),
                    meta: {title: '修改商品信息'},
                    props: true
                },
            ]
        },
        {
            path: '/home',
            redirect: '/createproduct',
            component: () => import('../views/Home.vue'),
            children: [
                {
                    path: '/createproduct',
                    name: 'CreateProduct',
                    component: () => import('../views/product/CreateProduct.vue'),
                    meta: {title: '创建商品'},

                },
            ]
        },
        {
            path: '/home',
            redirect: '/order',
            component: () => import('../views/Home.vue'),
            children: [
                {
                    path: '/order',
                    name: 'Order',
                    component: () => import('../views/users/Order.vue'),
                    meta: {title: '订单'},

                },
            ]
        },
        {
            path: '/home',
            redirect: '/circles',
            component: () => import('../views/Home.vue'),
            children: [
                {
                    path: '/home/circles',
                    name: 'AllCircles',
                    component: () => import('../views/circles/CircleList.vue'),
                    meta: {title: '书友圈'},
                },
                {
                    path: '/home/circles/postliked',
                    name: 'PostLiked',
                    component: () => import('../views/circles/PostLiked.vue'),
                    meta: {title: '我喜欢的帖子'},
                },
                {
                    path: '/home/circles/:circleId',
                    name: 'CircleDetail',
                    component: () => import('../views/circles/CircleDetail.vue'),
                    meta: {title : '书友圈详情'},
                },
                {
                    path: '/home/circles/posts/:postId',
                    name : 'PostDetail',
                    component: () => import('../views/circles/PostDetail.vue'),
                    meta: {title : '评论详情'},
                }

            ]
        },
        {
            path: '/home',
            redirect: '/history',
            component: () => import('../views/Home.vue'),
            children: [
                {
                    path: '/history',
                    name: 'History',
                    component: () => import('../views/users/History.vue'),
                    meta: {title: '购买记录'},
                },
            ]
        },
        {
            path: '/home',
            redirect: '/manageadvertisement',
            component: () => import('../views/Home.vue'),
            children: [
                {
                    path: '/manageadvertisement',
                    name: 'ManageAdvertisement',
                    component: () => import('../views/users/ManageAdvertisement.vue'),
                    meta: {title: '广告管理'},
                },
            ]
        },
        {
            path: '/404',
            component: () => import('../views/NotFound.vue'),
            meta: { title: '404' }
        },
        {
            path: '/:catchAll(.*)',
            redirect: '/404'
        }
    ]
})
router.beforeEach((to, _, next) => {
    const token: string | null = sessionStorage.getItem('token');
    const role: string | null = sessionStorage.getItem('role')

    if (to.meta.title) {
        document.title = to.meta.title
    }

    if (token) {
        if (to.meta.permission) {
            if (to.meta.permission.includes(role!)) {
                next()
            } else {
                next('/404')
            }
        } else {
            next()
        }
    } else {
        if (to.path === '/login') {
            next();
        } else if (to.path === '/register') {
            next()
        } else {
            next('/login')
        }
    }
})
export {router}