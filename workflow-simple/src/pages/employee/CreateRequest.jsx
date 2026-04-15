import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../../components/Navbar'
import { createRequest } from '../../services/api'
import './CreateRequest.css'

function CreateRequest() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ requestType:'', leaveType:'', duration:'', leavePlan:'', startDate:'', endDate:'', softwareName:'', softwareReason:'', description:'' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const set = (k, v) => setForm(f => ({ ...f, [k]: v }))

  function validate() {
    if (!form.requestType) { setError('Select a request type'); return false }
    if (!form.description.trim()) { setError('Description is required'); return false }
    if (form.requestType === 'Leave Request') {
      if (!form.leaveType) { setError('Select leave type'); return false }
      if (!form.duration)  { setError('Select duration'); return false }
      if (!form.leavePlan) { setError('Select leave plan'); return false }
      if (!form.startDate) { setError('Select start date'); return false }
    }
    if (form.requestType === 'Software Requirement') {
      if (!form.softwareName.trim())   { setError('Enter software name'); return false }
      if (!form.softwareReason.trim()) { setError('Enter reason'); return false }
    }
    return true
  }

  async function handleSubmit(e) {
    e.preventDefault(); setError('')
    if (!validate()) return
    setLoading(true)
    try { await createRequest(form); alert('Request submitted successfully!'); navigate('/employee/requests') }
    catch (err) { setError(err.response?.data?.message || 'Submission failed') }
    finally { setLoading(false) }
  }

  return (
    <div>
      <Navbar />
      <div className="container">
        <p className="cr-title">New Request</p>
        <button className="btn btn-secondary" onClick={() => navigate('/employee/requests')}>Back</button>
        <div className="cr-card">
          {error && <div className="cr-alert">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="cr-group">
              <label>Request Type *</label>
              <select value={form.requestType} onChange={e => { set('requestType', e.target.value); setError('') }}>
                <option value="">-- Select --</option>
                <option value="Leave Request">Leave Request</option>
                <option value="Software Requirement">Software Requirement</option>
              </select>
            </div>

            {form.requestType === 'Leave Request' && (
              <div>
                <div className="cr-section-label">Leave Details</div>
                <div className="cr-leave-grid">
                  <div className="cr-group">
                    <label>Leave Type *</label>
                    <select value={form.leaveType} onChange={e => set('leaveType', e.target.value)}>
                      <option value="">-- Select --</option>
                      <option value="CL">CL - Casual Leave</option>
                      <option value="LOP">LOP - Loss of Pay</option>
                    </select>
                  </div>
                  <div className="cr-group">
                    <label>Duration *</label>
                    <select value={form.duration} onChange={e => set('duration', e.target.value)}>
                      <option value="">-- Select --</option>
                      <option value="Full Day">Full Day</option>
                      <option value="Half Day">Half Day</option>
                    </select>
                  </div>
                  <div className="cr-group">
                    <label>Leave Plan *</label>
                    <select value={form.leavePlan} onChange={e => set('leavePlan', e.target.value)}>
                      <option value="">-- Select --</option>
                      <option value="Planned">Planned</option>
                      <option value="Unplanned">Unplanned</option>
                    </select>
                  </div>
                  <div className="cr-group">
                    <label>Start Date *</label>
                    <input type="date" value={form.startDate} onChange={e => set('startDate', e.target.value)} />
                  </div>
                  {form.duration === 'Full Day' && (
                    <div className="cr-group">
                      <label>End Date</label>
                      <input type="date" value={form.endDate} onChange={e => set('endDate', e.target.value)} />
                    </div>
                  )}
                </div>
              </div>
            )}

            {form.requestType === 'Software Requirement' && (
              <div>
                <div className="cr-section-label">Software Details</div>
                <div className="cr-group"><label>Software Name *</label><input value={form.softwareName} onChange={e => set('softwareName', e.target.value)} placeholder="e.g. VS Code, Figma" /></div>
                <div className="cr-group"><label>Reason *</label><input value={form.softwareReason} onChange={e => set('softwareReason', e.target.value)} placeholder="Why do you need it?" /></div>
              </div>
            )}

            {form.requestType && (
              <div>
                <div className="cr-group"><label>Description *</label><textarea value={form.description} onChange={e => set('description', e.target.value)} placeholder="Add any additional details..." /></div>
                <div className="cr-note">This request will be sent to your assigned manager for approval.</div>
                <button type="submit" className="btn btn-purple" disabled={loading}>{loading ? 'Submitting...' : 'Submit Request'}</button>
                <button type="button" className="btn btn-secondary" onClick={() => navigate('/employee/requests')}>Cancel</button>
              </div>
            )}
          </form>
        </div>
      </div>
    </div>
  )
}

export default CreateRequest
