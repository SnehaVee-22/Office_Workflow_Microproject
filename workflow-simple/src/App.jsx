import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'

import LoginPage from './pages/LoginPage'
import AdminDashboard from './pages/admin/AdminDashboard'
import AdminEmployees from './pages/admin/AdminEmployees'
import AdminMasterData from './pages/admin/AdminMasterData'
import EmployeeDashboard from './pages/employee/EmployeeDashboard'
import EmployeeRequests from './pages/employee/EmployeeRequests'
import CreateRequest from './pages/employee/CreateRequest'
import Notifications from './pages/employee/Notifications'
import ManagerDashboard from './pages/manager/ManagerDashboard'
import PendingRequests from './pages/manager/PendingRequests'
import ApprovalHistory from './pages/manager/ApprovalHistory'

function ProtectedRoute({ children, role }) {
  const { user, loading } = useAuth()
  if (loading) return <p style={{ padding: 20 }}>Loading...</p>
  if (!user) return <Navigate to="/login" />
  if (role && user.role !== role) return <Navigate to="/login" />
  return children
}

function HomeRedirect() {
  const { user, loading } = useAuth()
  if (loading) return <p style={{ padding: 20 }}>Loading...</p>
  if (!user) return <Navigate to="/login" />
  if (user.role === 'ADMIN') return <Navigate to="/admin/dashboard" />
  if (user.role === 'MANAGER') return <Navigate to="/manager/dashboard" />
  return <Navigate to="/employee/dashboard" />
}

function App() {
  return (
    <BrowserRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={<HomeRedirect />} />

          <Route path="/admin/dashboard" element={<ProtectedRoute role="ADMIN"><AdminDashboard /></ProtectedRoute>} />
          <Route path="/admin/employees" element={<ProtectedRoute role="ADMIN"><AdminEmployees /></ProtectedRoute>} />
          <Route path="/admin/master-data" element={<ProtectedRoute role="ADMIN"><AdminMasterData /></ProtectedRoute>} />

          <Route path="/employee/dashboard" element={<ProtectedRoute role="EMPLOYEE"><EmployeeDashboard /></ProtectedRoute>} />
          <Route path="/employee/requests" element={<ProtectedRoute role="EMPLOYEE"><EmployeeRequests /></ProtectedRoute>} />
          <Route path="/employee/requests/new" element={<ProtectedRoute role="EMPLOYEE"><CreateRequest /></ProtectedRoute>} />
          <Route path="/employee/notifications" element={<ProtectedRoute role="EMPLOYEE"><Notifications /></ProtectedRoute>} />

          <Route path="/manager/dashboard" element={<ProtectedRoute role="MANAGER"><ManagerDashboard /></ProtectedRoute>} />
          <Route path="/manager/pending" element={<ProtectedRoute role="MANAGER"><PendingRequests /></ProtectedRoute>} />
          <Route path="/manager/history" element={<ProtectedRoute role="MANAGER"><ApprovalHistory /></ProtectedRoute>} />

          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App
