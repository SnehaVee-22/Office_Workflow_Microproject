import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../../components/Navbar'
import { getManagerStats } from '../../services/api'
import { useAuth } from '../../context/AuthContext'
import './ManagerDashboard.css'

function ManagerDashboard() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [stats, setStats] = useState({ pendingRequests:0, approvedRequests:0, rejectedRequests:0, totalHandled:0 })

  useEffect(() => { getManagerStats().then(res => setStats(res.data)).catch(() => {}) }, [])

  return (
    <div>
      <Navbar />
      <div className="container">
        <p className="mgr-title">Welcome, {user?.name}</p>
        <div className="mgr-stats">
          <div className="mgr-stat"><div className="num">{stats.pendingRequests}</div><div className="lbl">Pending Review</div></div>
          <div className="mgr-stat"><div className="num">{stats.approvedRequests}</div><div className="lbl">Approved</div></div>
          <div className="mgr-stat"><div className="num">{stats.rejectedRequests}</div><div className="lbl">Rejected</div></div>
          <div className="mgr-stat"><div className="num">{stats.totalHandled}</div><div className="lbl">Total Handled</div></div>
        </div>
        <div className="mgr-links">
          <h3>Quick Links</h3>
          <button className="btn btn-purple" onClick={() => navigate('/manager/pending')}>Pending Requests</button>
          <button className="btn btn-primary" onClick={() => navigate('/manager/history')}>Approval History</button>
        </div>
      </div>
    </div>
  )
}

export default ManagerDashboard
