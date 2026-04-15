import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../../components/Navbar'
import { getMyRequests, cancelRequest, updateRequest, searchRequest } from '../../services/api'
import './EmployeeRequests.css'

function EmployeeRequests() {
  const navigate = useNavigate()
  const [requests, setRequests] = useState([])
  const [searchId, setSearchId] = useState('')
  const [filterStatus, setFilterStatus] = useState('ALL')
  const [editReq, setEditReq] = useState(null)
  const [editDesc, setEditDesc] = useState('')
  const [msg, setMsg] = useState('')

  useEffect(() => { loadRequests() }, [])
  function loadRequests() { getMyRequests().then(res => setRequests(res.data)).catch(() => {}) }

  async function handleSearch() {
    if (!searchId.trim()) { loadRequests(); return }
    try { const res = await searchRequest(searchId.trim()); setRequests(res.data ? [res.data] : []) }
    catch { setMsg('Request not found'); setRequests([]) }
  }

  async function handleCancel(req) {
    if (!window.confirm('Cancel this request?')) return
    try { await cancelRequest(req.id); setMsg('Request cancelled'); loadRequests() }
    catch (err) { setMsg(err.response?.data?.message || 'Cannot cancel') }
  }

  async function handleUpdate(e) {
    e.preventDefault()
    if (!editDesc.trim()) { setMsg('Description required'); return }
    try { await updateRequest(editReq.id, { ...editReq, description:editDesc }); setMsg('Request updated'); setEditReq(null); loadRequests() }
    catch (err) { setMsg(err.response?.data?.message || 'Update failed') }
  }

  const filtered = requests.filter(r => filterStatus === 'ALL' || r.status === filterStatus)
  const pillClass = s => ({ PENDING:'er-pill-pending', APPROVED:'er-pill-approved', REJECTED:'er-pill-rejected', CANCELLED:'er-pill-cancelled' }[s] || '')

  return (
    <div>
      <Navbar />
      <div className="container">
        <p className="er-title">My Requests</p>
        <div className="er-toolbar">
          <button className="btn btn-secondary" onClick={() => navigate('/employee/dashboard')}>Back</button>
          <button className="btn btn-purple" onClick={() => navigate('/employee/requests/new')}>New Request</button>
        </div>
        {msg && <div className="er-alert">{msg}</div>}
        <div className="er-search">
          <input placeholder="Search by Request ID" value={searchId} onChange={e => setSearchId(e.target.value)} onKeyDown={e => e.key === 'Enter' && handleSearch()} />
          <button className="btn btn-secondary" onClick={handleSearch}>Search</button>
          {searchId && <button className="btn btn-secondary" onClick={() => { setSearchId(''); loadRequests() }}>Clear</button>}
          <select value={filterStatus} onChange={e => setFilterStatus(e.target.value)}>
            <option value="ALL">All Status</option>
            <option value="PENDING">Pending</option>
            <option value="APPROVED">Approved</option>
            <option value="REJECTED">Rejected</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
        </div>
        <div className="er-table-wrap">
          <table className="er-table">
            <thead><tr><th>Request ID</th><th>Type</th><th>Details</th><th>Description</th><th>Date</th><th>Status</th><th>Remarks</th><th>Action</th></tr></thead>
            <tbody>
              {filtered.length === 0 && <tr><td colSpan={8} style={{ textAlign:'center', padding:20 }}>No requests found</td></tr>}
              {filtered.map(r => (
                <tr key={r.id}>
                  <td>{r.requestId}</td>
                  <td>{r.requestType}</td>
                  <td>{r.leaveType && <span>{r.leaveType}/{r.duration}/{r.leavePlan}</span>}{r.softwareName && <span>{r.softwareName}</span>}</td>
                  <td>{r.description}</td>
                  <td>{r.createdDate}</td>
                  <td><span className={'er-pill ' + pillClass(r.status)}>{r.status}</span></td>
                  <td>{r.managerRemarks || '-'}</td>
                  <td>
                    {r.status === 'PENDING' && <span>
                      <button className="btn btn-warning" onClick={() => { setEditReq(r); setEditDesc(r.description) }}>Edit</button>
                      <button className="btn btn-danger" onClick={() => handleCancel(r)}>Cancel</button>
                    </span>}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {editReq && (
          <div className="er-modal-overlay">
            <div className="er-modal-box">
              <button className="er-modal-close" onClick={() => setEditReq(null)}>x</button>
              <h3>Edit Request - {editReq.requestId}</h3>
              <form onSubmit={handleUpdate}>
                <div className="er-modal-group"><label>Description</label><textarea value={editDesc} onChange={e => setEditDesc(e.target.value)} /></div>
                <button type="submit" className="btn btn-primary">Update</button>
                <button type="button" className="btn btn-secondary" onClick={() => setEditReq(null)}>Cancel</button>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default EmployeeRequests
