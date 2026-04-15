import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../../components/Navbar'
import { getAdminStats } from '../../services/api'
import { useAuth } from '../../context/AuthContext'
import './AdminDashboard.css'

function AdminDashboard() {
  const navigate = useNavigate()
  const { user } = useAuth()
  const [stats, setStats] = useState({ totalEmployees:0, activeEmployees:0, totalManagers:0, totalRequests:0, pendingRequests:0, approvedRequests:0, rejectedRequests:0 })

  useEffect(() => {
    getAdminStats().then(res => setStats(res.data)).catch(() => {})
  }, [])

  return (
    <div>
      <Navbar />
      <div className="container">
        <p className="adm-dash-title">Admin Dashboard</p>
        <div className="adm-stats">
          <div className="adm-stat-card"><div className="stat-num">{stats.totalEmployees}</div><div className="stat-lbl">Total Employees</div></div>
          <div className="adm-stat-card"><div className="stat-num">{stats.activeEmployees}</div><div className="stat-lbl">Active</div></div>
          <div className="adm-stat-card"><div className="stat-num">{stats.totalManagers}</div><div className="stat-lbl">Managers</div></div>
          <div className="adm-stat-card"><div className="stat-num">{stats.totalRequests}</div><div className="stat-lbl">Total Requests</div></div>
          <div className="adm-stat-card"><div className="stat-num">{stats.pendingRequests}</div><div className="stat-lbl">Pending</div></div>
          <div className="adm-stat-card"><div className="stat-num">{stats.approvedRequests}</div><div className="stat-lbl">Approved</div></div>
          <div className="adm-stat-card"><div className="stat-num">{stats.rejectedRequests}</div><div className="stat-lbl">Rejected</div></div>
        </div>
        <div className="adm-quick-card">
          <h3>Quick Links</h3>
          <button className="btn btn-primary" onClick={() => navigate('/admin/employees')}>Manage Employees</button>
          <button className="btn btn-purple" onClick={() => navigate('/admin/master-data')}>Master Data</button>
        </div>
      </div>
    </div>
  )
}

export default AdminDashboard
