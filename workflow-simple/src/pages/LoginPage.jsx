import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { loginAPI } from '../services/api'
import './LoginPage.css'

function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  async function handleLogin(e) {
    e.preventDefault()
    setError('')
    if (!email || !password) { setError('Please enter email and password'); return }
    setLoading(true)
    try {
      const res = await loginAPI(email, password)
      login(res.data.user, res.data.token)
      const role = res.data.user.role
      if (role === 'ADMIN') navigate('/admin/dashboard')
      else if (role === 'MANAGER') navigate('/manager/dashboard')
      else navigate('/employee/dashboard')
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid email or password')
    } finally { setLoading(false) }
  }

  return (
    <div className="login-wrapper">
      <div className="login-box">
        <div className="login-logo">🏢</div>
        <h2>Workflow Approval System</h2>
        <p>Sign in to your account</p>
        {error && <div className="login-alert">{error}</div>}
        <form onSubmit={handleLogin}>
          <div className="login-form-group">
            <label>Email</label>
            <input type="email" value={email} onChange={e => setEmail(e.target.value)} placeholder="Enter your email" />
          </div>
          <div className="login-form-group">
            <label>Password</label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="Enter your password" />
          </div>
          <button type="submit" className="login-btn" disabled={loading}>
            {loading ? 'Please wait...' : 'Login'}
          </button>
        </form>
      </div>
    </div>
  )
}

export default LoginPage
