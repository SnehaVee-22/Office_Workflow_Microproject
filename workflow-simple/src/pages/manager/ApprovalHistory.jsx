import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../../components/Navbar'
import { getHistory } from '../../services/api'
import './ApprovalHistory.css'

function ApprovalHistory() {
  const navigate = useNavigate()
  const [history, setHistory] = useState([])
  const [filterStatus, setFilterStatus] = useState('ALL')
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [msg, setMsg] = useState('')

  useEffect(() => {
    const today = new Date().toISOString().split('T')[0]
    const monthAgo = new Date(Date.now() - 30*24*3600*1000).toISOString().split('T')[0]
    setStartDate(monthAgo); setEndDate(today); fetchHistory(monthAgo, today)
  }, [])

  function fetchHistory(start, end) { getHistory(start, end).then(res => setHistory(res.data)).catch(() => setMsg('Failed to load')) }
  function handleFilter(e) { e.preventDefault(); if (!startDate || !endDate) { setMsg('Select both dates'); return } setMsg(''); fetchHistory(startDate, endDate) }

  const filtered = history.filter(r => filterStatus === 'ALL' || r.status === filterStatus)
  const pillClass = s => s === 'APPROVED' ? 'ah-pill-approved' : 'ah-pill-rejected'

  return (
    <div>
      <Navbar />
      <div className="container">
        <p className="ah-title">Approval History</p>
        <button className="btn btn-secondary" onClick={() => navigate('/manager/dashboard')}>Back</button>
        <div className="ah-filter-card">
          <h3>Filter</h3>
          <form onSubmit={handleFilter}>
            <div className="ah-filter-row">
              <div className="ah-filter-group"><label>From Date</label><input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} /></div>
              <div className="ah-filter-group"><label>To Date</label><input type="date" value={endDate} onChange={e => setEndDate(e.target.value)} /></div>
              <div className="ah-filter-group"><label>Status</label>
                <select value={filterStatus} onChange={e => setFilterStatus(e.target.value)}>
                  <option value="ALL">All</option>
                  <option value="APPROVED">Approved</option>
                  <option value="REJECTED">Rejected</option>
                </select>
              </div>
              <div style={{ alignSelf:'flex-end' }}><button type="submit" className="btn btn-primary">Search</button></div>
            </div>
          </form>
        </div>
        {msg && <div className="ah-alert">{msg}</div>}
        <div className="ah-summary">
          <strong>Total: {filtered.length}</strong>
          <strong>Approved: {filtered.filter(r => r.status === 'APPROVED').length}</strong>
          <strong>Rejected: {filtered.filter(r => r.status === 'REJECTED').length}</strong>
        </div>
        <div className="ah-table-wrap">
          <table className="ah-table">
            <thead><tr><th>Request ID</th><th>Employee</th><th>Dept</th><th>Type</th><th>Details</th><th>Description</th><th>Status</th><th>Decided On</th><th>Remarks</th></tr></thead>
            <tbody>
              {filtered.length === 0 ? <tr><td colSpan={9} style={{ textAlign:'center', padding:20 }}>No records found</td></tr>
               : filtered.map(r => (
                <tr key={r.id}>
                  <td>{r.requestId}</td>
                  <td>{r.employeeName}<span className="ah-sub">{r.employeeId}</span></td>
                  <td>{r.department}</td>
                  <td>{r.requestType}</td>
                  <td>{r.leaveType && <span>{r.leaveType}/{r.duration}</span>}{r.softwareName && <span>{r.softwareName}</span>}</td>
                  <td>{r.description}</td>
                  <td><span className={'ah-pill ' + pillClass(r.status)}>{r.status}</span></td>
                  <td>{r.decidedDate}</td>
                  <td>{r.managerRemarks}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

export default ApprovalHistory
