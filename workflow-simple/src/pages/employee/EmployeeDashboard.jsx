import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../../components/Navbar'
import { getMyRequests } from '../../services/api'
import { useAuth } from '../../context/AuthContext'
import './EmployeeDashboard.css'

function EmployeeDashboard() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [requests, setRequests] = useState([])

  useEffect(() => { getMyRequests().then(res => setRequests(res.data)).catch(() => {}) }, [])

  const pending  = requests.filter(r => r.status === 'PENDING').length
  const approved = requests.filter(r => r.status === 'APPROVED').length
  const rejected = requests.filter(r => r.status === 'REJECTED').length
  const recent   = requests.slice(0, 5)
  const pillClass = s => ({ PENDING:'ed-pill-pending', APPROVED:'ed-pill-approved', REJECTED:'ed-pill-rejected', CANCELLED:'ed-pill-cancelled' }[s] || '')

  return (
    <div>
      <Navbar />
      <div className="container">
        <p className="ed-title">Welcome, {user?.name}</p>
        <div className="ed-stats">
          <div className="ed-stat"><div className="num">{requests.length}</div><div className="lbl">Total Requests</div></div>
          <div className="ed-stat"><div className="num">{pending}</div><div className="lbl">Pending</div></div>
          <div className="ed-stat"><div className="num">{approved}</div><div className="lbl">Approved</div></div>
          <div className="ed-stat"><div className="num">{rejected}</div><div className="lbl">Rejected</div></div>
        </div>
        <div className="ed-links">
          <h3>Quick Links</h3>
          <button className="btn btn-purple" onClick={() => navigate('/employee/requests/new')}>New Request</button>
          <button className="btn btn-primary" onClick={() => navigate('/employee/requests')}>My Requests</button>
          <button className="btn btn-secondary" onClick={() => navigate('/employee/notifications')}>Notifications</button>
        </div>
        <div className="ed-recent">
          <h3>Recent Requests</h3>
          {recent.length === 0 ? <p style={{ color:'#888', fontSize:13 }}>No requests yet.</p> : (
            <div className="ed-table-wrap">
              <table className="ed-table">
                <thead><tr><th>Request ID</th><th>Type</th><th>Description</th><th>Date</th><th>Status</th></tr></thead>
                <tbody>
                  {recent.map(r => (
                    <tr key={r.id}>
                      <td>{r.requestId}</td><td>{r.requestType}</td><td>{r.description}</td><td>{r.createdDate}</td>
                      <td><span className={'ed-pill ' + pillClass(r.status)}>{r.status}</span></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default EmployeeDashboard
