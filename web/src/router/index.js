import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import CoursePage from '../views/courses/CoursePage.vue'
import BriefPage from '../views/brief/BriefPage.vue'
import ExamPage from '../views/exams/ExamPage.vue'
import AbilityPage from '../views/ability/AbilityPage.vue'
import RecommendPage from '../views/recommend/RecommendPage.vue'
import WrongQuestionPage from '../views/wrong/WrongQuestionPage.vue'
import FeedbackPage from '../views/feedback/FeedbackPage.vue'
import AccountPage from '../views/account/AccountPage.vue'
import AdminCoursePage from '../views/admin/AdminCoursePage.vue'
import AdminStudentPage from '../views/admin/AdminStudentPage.vue'
import AdminStatsPage from '../views/admin/AdminStatsPage.vue'
import AdminFeedbackPage from '../views/admin/AdminFeedbackPage.vue'
import LoginPage from '../views/auth/LoginPage.vue'
import RegisterPage from '../views/auth/RegisterPage.vue'

const routes = [
  { path: '/', redirect: '/courses' },
  { path: '/login', name: 'login', component: LoginPage, meta: { public: true, authPage: true } },
  { path: '/register', name: 'register', component: RegisterPage, meta: { public: true, authPage: true } },
  { path: '/tasks', redirect: '/courses' },
  { path: '/courses', name: 'courses', component: CoursePage },
  { path: '/brief', name: 'brief', component: BriefPage },
  { path: '/exams', name: 'exams', component: ExamPage },
  { path: '/ability', name: 'ability', component: AbilityPage },
  { path: '/recommend', name: 'recommend', component: RecommendPage },
  { path: '/wrong', name: 'wrong', component: WrongQuestionPage },
  { path: '/feedback', name: 'feedback', component: FeedbackPage },
  { path: '/account', name: 'account', component: AccountPage },
  { path: '/admin/courses', name: 'admin-courses', component: AdminCoursePage },
  { path: '/admin/students', name: 'admin-students', component: AdminStudentPage },
  { path: '/admin/stats', name: 'admin-stats', component: AdminStatsPage },
  { path: '/admin/feedback', name: 'admin-feedback', component: AdminFeedbackPage }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const username = localStorage.getItem('auth.username') || ''
  const password = localStorage.getItem('auth.password') || ''
  const role = (localStorage.getItem('auth.role') || '').toUpperCase()
  const loggedIn = Boolean(username && password)

  if (!to.meta.public && !loggedIn) {
    return '/login'
  }

  if ((to.path === '/login' || to.path === '/register') && loggedIn) {
    return role === 'ADMIN' ? '/admin/stats' : '/courses'
  }

  if (loggedIn && role === 'ADMIN' && !to.meta.public && !to.path.startsWith('/admin')) {
    return '/admin/stats'
  }

  if (to.path.startsWith('/admin') && role !== 'ADMIN') {
    ElMessage.warning('仅管理员可访问管理端页面')
    return '/courses'
  }

  return true
})

export default router
