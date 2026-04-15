import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../../components/Navbar'
import { getDepartments, createDepartment, deleteDepartment, getRequestTypes, createRequestType, deleteRequestType } from '../../services/api'
import './AdminMasterData.css'

function AdminMasterData() {
  const navigate = useNavigate()
  const [departments, setDepartments] = useState([])
  const [reqTypes, setReqTypes] = useState([])
  const [deptName, setDeptName] = useState(''); const [deptDesc, setDeptDesc] = useState('')
  const [typeName, setTypeName] = useState(''); const [typeDesc, setTypeDesc] = useState('')
  const [msg, setMsg] = useState('')

  useEffect(() => { loadData() }, [])
  function loadData() {
    getDepartments().then(res => setDepartments(res.data)).catch(() => {})
    getRequestTypes().then(res => setReqTypes(res.data)).catch(() => {})
  }

  async function handleAddDept(e) {
    e.preventDefault()
    if (!deptName.trim()) { setMsg('Department name is required'); return }
    try { await createDepartment({ name:deptName, description:deptDesc }); setDeptName(''); setDeptDesc(''); setMsg('Department added'); loadData() }
    catch (err) { setMsg(err.response?.data?.message || 'Failed') }
  }
  async function handleDeleteDept(id) {
    if (!window.confirm('Delete this department?')) return
    try { await deleteDepartment(id); loadData() } catch { setMsg('Cannot delete') }
  }
  async function handleAddType(e) {
    e.preventDefault()
    if (!typeName.trim()) { setMsg('Request type name is required'); return }
    try { await createRequestType({ name:typeName, description:typeDesc }); setTypeName(''); setTypeDesc(''); setMsg('Request type added'); loadData() }
    catch (err) { setMsg(err.response?.data?.message || 'Failed') }
  }
  async function handleDeleteType(id) {
    if (!window.confirm('Delete this request type?')) return
    try { await deleteRequestType(id); loadData() } catch { setMsg('Cannot delete') }
  }

  return (
    <div>
      <Navbar />
      <div className="container">
        <p className="master-title">Master Data</p>
        <button className="btn btn-secondary" onClick={() => navigate('/admin/dashboard')}>Back</button>
        {msg && <div className="master-alert">{msg}</div>}
        <div className="master-grid">
          <div className="master-card">
            <h3>Departments</h3>
            <form onSubmit={handleAddDept}>
              <div className="master-form-group"><label>Name</label><input value={deptName} onChange={e => setDeptName(e.target.value)} placeholder="e.g. Marketing" /></div>
              <div className="master-form-group"><label>Description</label><input value={deptDesc} onChange={e => setDeptDesc(e.target.value)} placeholder="Optional" /></div>
              <button type="submit" className="btn btn-primary">Add</button>
            </form>
            <div className="master-table-wrap">
              <table className="master-table">
                <thead><tr><th>Name</th><th>Action</th></tr></thead>
                <tbody>{departments.map(d => <tr key={d.id}><td>{d.name}</td><td><button className="btn btn-danger" onClick={() => handleDeleteDept(d.id)}>Delete</button></td></tr>)}</tbody>
              </table>
            </div>
          </div>
          <div className="master-card">
            <h3>Request Types</h3>
            <form onSubmit={handleAddType}>
              <div className="master-form-group"><label>Name</label><input value={typeName} onChange={e => setTypeName(e.target.value)} placeholder="e.g. Hardware Request" /></div>
              <div className="master-form-group"><label>Description</label><input value={typeDesc} onChange={e => setTypeDesc(e.target.value)} placeholder="Optional" /></div>
              <button type="submit" className="btn btn-primary">Add</button>
            </form>
            <div className="master-table-wrap">
              <table className="master-table">
                <thead><tr><th>Name</th><th>Action</th></tr></thead>
                <tbody>{reqTypes.map(t => <tr key={t.id}><td>{t.name}</td><td><button className="btn btn-danger" onClick={() => handleDeleteType(t.id)}>Delete</button></td></tr>)}</tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default AdminMasterData
