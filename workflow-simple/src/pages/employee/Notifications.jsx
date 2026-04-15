import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../../components/Navbar'
import { getNotifications, markRead } from '../../services/api'
import './Notifications.css'

function Notifications() {
  const navigate = useNavigate()
  const [notifs, setNotifs] = useState([])

  useEffect(() => { getNotifications().then(res => setNotifs(res.data)).catch(() => {}) }, [])

  async function handleMarkRead(id) {
    try { await markRead(id); setNotifs(prev => prev.map(n => n.id === id ? { ...n, read:true } : n)) } catch {}
  }

  return (
    <div>
      <Navbar />
      <div className="container">
        <p className="notif-title">Notifications</p>
        <button className="btn btn-secondary" onClick={() => navigate('/employee/dashboard')}>Back</button>
        {notifs.length === 0 ? (
          <div className="notif-empty">No notifications yet.</div>
        ) : (
          <div className="notif-wrap">
            <table className="notif-table">
              <thead><tr><th>Message</th><th>Type</th><th>Date</th><th>Status</th><th>Action</th></tr></thead>
              <tbody>
                {notifs.map(n => (
                  <tr key={n.id} className={!n.read ? 'notif-unread' : ''}>
                    <td>{n.message}</td>
                    <td>{n.type}</td>
                    <td>{n.createdAt ? n.createdAt.substring(0,10) : ''}</td>
                    <td>{n.read ? <span className="notif-read-text">Read</span> : <span className="notif-unread-text">Unread</span>}</td>
                    <td>{!n.read && <button className="btn btn-secondary" onClick={() => handleMarkRead(n.id)}>Mark Read</button>}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}

export default Notifications
