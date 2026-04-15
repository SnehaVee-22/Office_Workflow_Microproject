import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../../components/Navbar'
import { getPendingRequests, approveRequest, rejectRequest } from '../../services/api'
import './PendingRequests.css'

function PendingRequests() {
  const navigate = useNavigate()
  const [requests, setRequests] = useState([])
  const [selected, setSelected] = useState(null)
  const [action, setAction] = useState('')
  const [remarks, setRemarks] = useState('')
  const [remarksErr, setRemarksErr] = useState('')
  const [msg, setMsg] = useState('')

  useEffect(() => { load() }, [])
  function load() { getPendingRequests().then(res => setRequests(res.data)).catch(() => {}) }
  function openAction(req, type) { setSelected(req); setAction(type); setRemarks(''); setRemarksErr('') }

  async function handleDecision(e) {
    e.preventDefault()
    if (!remarks.trim()) { setRemarksErr('Remarks are required'); return }
    try {
      if (action === 'approve') await approveRequest(selected.id, remarks)
      else await rejectRequest(selected.id, remarks)
      setMsg('Request ' + (action === 'approve' ? 'approved' : 'rejected') + '. Employee has been notified.')
      setSelected(null); load()
    } catch (err) { setMsg(err.response?.data?.message || 'Action failed') }
  }

  return (
    <div>
      <Navbar />
      <div className="container">
        <p className="pr-title">Pending Requests</p>
        <button className="btn btn-secondary" onClick={() => navigate('/manager/dashboard')}>Back</button>
        {msg && <div className="pr-alert">{msg}</div>}
        <div className="mt-10">
          {requests.length === 0 ? (
            <div className="pr-empty">No pending requests. You are all caught up!</div>
          ) : (
            <div className="pr-table-wrap">
              <table className="pr-table">
                <thead><tr><th>Request ID</th><th>Employee</th><th>Dept</th><th>Type</th><th>Details</th><th>Description</th><th>Date</th><th>Action</th></tr></thead>
                <tbody>
                  {requests.map(r => (
                    <tr key={r.id}>
                      <td>{r.requestId}</td>
                      <td>{r.employeeName}<span className="pr-sub">{r.employeeId}</span></td>
                      <td>{r.department}</td>
                      <td>{r.requestType}</td>
                      <td>{r.leaveType && <span>{r.leaveType}/{r.duration}<br />{r.leavePlan}/{r.startDate}</span>}{r.softwareName && <span>{r.softwareName}</span>}</td>
                      <td>{r.description}</td>
                      <td>{r.createdDate}</td>
                      <td>
                        <button className="btn btn-success" onClick={() => openAction(r, 'approve')}>Approve</button>
                        <button className="btn btn-danger" onClick={() => openAction(r, 'reject')}>Reject</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
        {selected && (
          <div className="pr-modal-overlay">
            <div className="pr-modal-box">
              <button className="pr-modal-close" onClick={() => setSelected(null)}>x</button>
              <h3>{action === 'approve' ? 'Approve' : 'Reject'} - {selected.requestId}</h3>
              <div className="pr-modal-info"><strong>Employee:</strong> {selected.employeeName} &nbsp;|&nbsp; <strong>Type:</strong> {selected.requestType}</div>
              <form onSubmit={handleDecision}>
                <div className="pr-form-group">
                  <label>Remarks * (required)</label>
                  <textarea value={remarks} onChange={e => { setRemarks(e.target.value); setRemarksErr('') }} placeholder={action === 'approve' ? 'e.g. Approved. Enjoy your leave.' : 'e.g. Insufficient leave balance.'} />
                  {remarksErr && <p className="error-msg">{remarksErr}</p>}
                </div>
                <button type="submit" className={'btn ' + (action === 'approve' ? 'btn-success' : 'btn-danger')}>Confirm {action === 'approve' ? 'Approve' : 'Reject'}</button>
                <button type="button" className="btn btn-secondary" onClick={() => setSelected(null)}>Cancel</button>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default PendingRequests
