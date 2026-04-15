import axios from 'axios'

const api = axios.create({ baseURL: '/api' })

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = 'Bearer ' + token
  return config
})

api.interceptors.response.use(
  res => res,
  err => {
    if (err.response && err.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

// Auth
export function loginAPI(email, password) {
  return api.post('/auth/login', { email, password })
}

// Admin
export function getEmployees() { return api.get('/admin/employees') }
export function createEmployee(data) { return api.post('/admin/employees', data) }
export function updateEmployee(id, data) { return api.put('/admin/employees/' + id, data) }
export function toggleActive(id) { return api.patch('/admin/employees/' + id + '/toggle-active') }
export function getDepartments() { return api.get('/admin/departments') }
export function createDepartment(data) { return api.post('/admin/departments', data) }
export function deleteDepartment(id) { return api.delete('/admin/departments/' + id) }
export function getRequestTypes() { return api.get('/admin/request-types') }
export function createRequestType(data) { return api.post('/admin/request-types', data) }
export function deleteRequestType(id) { return api.delete('/admin/request-types/' + id) }
export function getAdminStats() { return api.get('/admin/dashboard/stats') }

// Employee
export function getMyRequests() { return api.get('/employee/requests') }
export function createRequest(data) { return api.post('/employee/requests', data) }
export function updateRequest(id, data) { return api.put('/employee/requests/' + id, data) }
export function cancelRequest(id) { return api.patch('/employee/requests/' + id + '/cancel') }
export function searchRequest(requestId) { return api.get('/employee/requests/search?requestId=' + requestId) }
export function getNotifications() { return api.get('/employee/notifications') }
export function markRead(id) { return api.patch('/employee/notifications/' + id + '/read') }

// Manager
export function getPendingRequests() { return api.get('/manager/requests/pending') }
export function approveRequest(id, remarks) { return api.patch('/manager/requests/' + id + '/approve', { remarks }) }
export function rejectRequest(id, remarks) { return api.patch('/manager/requests/' + id + '/reject', { remarks }) }
export function getHistory(startDate, endDate) {
  return api.get('/manager/requests/history?startDate=' + startDate + '&endDate=' + endDate)
}
export function getManagerStats() { return api.get('/manager/dashboard/stats') }
