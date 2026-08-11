import { createRouter, createWebHistory } from 'vue-router'
import JobList from '../views/JobList.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import JobCreate from '../views/JobCreate.vue'

const routes = [
  { path: '/', component: JobList },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/create', component: JobCreate }
]

const router = createRouter({ history: createWebHistory(), routes })
export default router
