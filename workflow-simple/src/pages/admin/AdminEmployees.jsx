import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../../components/Navbar'
import { getEmployees, getDepartments, createEmployee, updateEmployee, toggleActive } from '../../services/api'
import './AdminEmployees.css'

function AdminEmployees() {
  const navigate = useNavigate()
  const [employees, setEmployees] = useState([])
  const [departments, setDepartments] = useState([])
  const [managers, setManagers] = useState([])
  const [showForm, setShowForm] = useState(false)
  const [editId, setEditId] = useState(null)
  const [msg, setMsg] = useState({ text:'', type:'' })
  const [form, setForm] = useState({ employeeId:'', name:'', email:'', department:'', role:'EMPLOYEE', managerId:'', password:'' })
  const [errors, setErrors] = useState({})

  useEffect(() => { loadData() }, [])

  function loadData() {
    getEmployees().then(res => { setEmployees(res.data); setManagers(res.data.filter(e => e.role === 'MANAGER')) }).catch(() => {})
    getDepartments().then(res => setDepartments(res.data)).catch(() => {})
  }

  function openCreate() { setEditId(null); setForm({ employeeId:'', name:'', email:'', department:'', role:'EMPLOYEE', managerId:'', password:'' }); setErrors({}); setShowForm(true) }
  function openEdit(emp) { setEditId(emp.id); setForm({ employeeId:emp.employeeId, name:emp.name, email:emp.email, department:emp.department, role:emp.role, managerId:emp.managerId||'', password:'' }); setErrors({}); setShowForm(true) }

  function validate() {
    const e = {}
    if (!form.employeeId) e.employeeId = 'Required'
    if (!form.name) e.name = 'Required'
    if (!form.email) e.email = 'Required'
    if (!form.department) e.department = 'Required'
    if (!editId && !form.password) e.password = 'Required'
    if (form.role === 'EMPLOYEE' && !form.managerId) e.managerId = 'Select a manager'
    setErrors(e)
    return Object.keys(e).length === 0
  }

  async function handleSave(e) {
    e.preventDefault()
    if (!validate()) return
    try {
      if (editId) { await updateEmployee(editId, form); setMsg({ text:'Employee updated. Credentials sent to email.', type:'success' }) }
      else { await createEmployee(form); setMsg({ text:'Employee created. Credentials sent to ' + form.email, type:'success' }) }
      setShowForm(false); loadData()
    } catch (err) { setMsg({ text: err.response?.data?.message || 'Failed to save', type:'error' }) }
  }

  async function handleToggle(emp) {
    try { await toggleActive(emp.id); setMsg({ text: emp.name + (emp.active ? ' deactivated' : ' activated'), type:'success' }); loadData() }
    catch { setMsg({ text:'Action failed', type:'error' }) }
  }

  return (
    <div>
      <Navbar />
      <div className="container">
        <p className="emp-page-title">Employee Management</p>
        <div className="emp-toolbar">
          <button className="btn btn-secondary" onClick={() => navigate('/admin/dashboard')}>Back</button>
          <button className="btn btn-purple" onClick={openCreate}>+ Add Employee</button>
        </div>
        {msg.text && <div className={'emp-alert emp-alert-' + msg.type}>{msg.text}</div>}
        <div className="emp-table-wrap">
          <table className="emp-table">
            <thead>
              <tr><th>Emp ID</th><th>Name</th><th>Email</th><th>Dept</th><th>Role</th><th>Manager</th><th>Status</th><th>Action</th></tr>
            </thead>
            <tbody>
              {employees.length === 0 && <tr><td colSpan={8} style={{ textAlign:'center', padding:20 }}>No employees found</td></tr>}
              {employees.map(emp => (
                <tr key={emp.id}>
                  <td>{emp.employeeId}</td>
                  <td>{emp.name}</td>
                  <td>{emp.email}</td>
                  <td>{emp.department}</td>
                  <td>{emp.role}</td>
                  <td>{emp.managerName || '-'}</td>
                  <td><span className={'emp-pill ' + (emp.active ? 'emp-pill-active' : 'emp-pill-inactive')}>{emp.active ? 'Active' : 'Inactive'}</span></td>
                  <td>
                    <button className="btn btn-warning" onClick={() => openEdit(emp)}>Edit</button>
                    <button className={'btn ' + (emp.active ? 'btn-danger' : 'btn-success')} onClick={() => handleToggle(emp)}>{emp.active ? 'Deactivate' : 'Activate'}</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {showForm && (
          <div className="emp-modal-overlay">
            <div className="emp-modal-box">
              <button className="emp-modal-close" onClick={() => setShowForm(false)}>x</button>
              <h3>{editId ? 'Edit Employee' : 'Add New Employee'}</h3>
              <form onSubmit={handleSave}>
                <div className="emp-form-group"><label>Employee ID *</label><input value={form.employeeId} onChange={e => setForm({...form, employeeId:e.target.value})} disabled={!!editId} />{errors.employeeId && <p className="error-msg">{errors.employeeId}</p>}</div>
                <div className="emp-form-group"><label>Full Name *</label><input value={form.name} onChange={e => setForm({...form, name:e.target.value})} />{errors.name && <p className="error-msg">{errors.name}</p>}</div>
                <div className="emp-form-group"><label>Email *</label><input type="email" value={form.email} onChange={e => setForm({...form, email:e.target.value})} />{errors.email && <p className="error-msg">{errors.email}</p>}</div>
                <div className="emp-form-group">
                  <label>Department *</label>
                  <select value={form.department} onChange={e => setForm({...form, department:e.target.value})}>
                    <option value="">Select</option>
                    {departments.map(d => <option key={d.id} value={d.name}>{d.name}</option>)}
                  </select>
                  {errors.department && <p className="error-msg">{errors.department}</p>}
                </div>
                <div className="emp-form-group">
                  <label>Role *</label>
                  <select value={form.role} onChange={e => setForm({...form, role:e.target.value})}>
                    <option value="EMPLOYEE">Employee</option>
                    <option value="MANAGER">Manager</option>
                  </select>
                </div>
                {form.role === 'EMPLOYEE' && (
                  <div className="emp-form-group">
                    <label>Assign Manager *</label>
                    <select value={form.managerId} onChange={e => setForm({...form, managerId:e.target.value})}>
                      <option value="">Select Manager</option>
                      {managers.map(m => <option key={m.id} value={m.id}>{m.name}</option>)}
                    </select>
                    {errors.managerId && <p className="error-msg">{errors.managerId}</p>}
                  </div>
                )}
                <div className="emp-form-group"><label>{editId ? 'New Password (optional)' : 'Password *'}</label><input type="password" value={form.password} onChange={e => setForm({...form, password:e.target.value})} />{errors.password && <p className="error-msg">{errors.password}</p>}</div>
                <p className="emp-note">Login credentials will be emailed automatically.</p>
                <button type="submit" className="btn btn-primary">{editId ? 'Update' : 'Create'}</button>
                <button type="button" className="btn btn-secondary" onClick={() => setShowForm(false)}>Cancel</button>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default AdminEmployees
