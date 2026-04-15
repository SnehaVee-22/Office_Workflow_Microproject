import React from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Navbar.css'

function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <div className="navbar">
      <h2>Workflow Approval System</h2>
      <div className="navbar-right">
        <span className="navbar-user">
          {user ? user.name : ''} ({user ? user.role : ''})
        </span>
        <button className="navbar-logout" onClick={handleLogout}>Logout</button>
      </div>
    </div>
  )
}

export default Navbar
