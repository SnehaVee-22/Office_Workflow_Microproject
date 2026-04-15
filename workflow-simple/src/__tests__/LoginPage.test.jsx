import React from 'react'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { AuthProvider } from '../context/AuthContext'
import LoginPage from '../pages/LoginPage'

jest.mock('../services/api', () => ({ loginAPI: jest.fn() }))

const mockNavigate = jest.fn()
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate
}))

import { loginAPI } from '../services/api'

function setup() {
  return render(
    <MemoryRouter><AuthProvider><LoginPage /></AuthProvider></MemoryRouter>
  )
}

describe('LoginPage', () => {
  beforeEach(() => { jest.clearAllMocks(); localStorage.clear() })

  test('renders title and form fields', () => {
    setup()
    expect(screen.getByText('Workflow Approval System')).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Enter your email')).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Enter your password')).toBeInTheDocument()
  })

  test('shows error when both fields empty', () => {
    setup()
    fireEvent.click(screen.getByRole('button', { name: /login/i }))
    expect(screen.getByText('Please enter email and password')).toBeInTheDocument()
  })

  test('calls loginAPI with entered values', async () => {
    loginAPI.mockResolvedValueOnce({
      data: { token: 'tok', user: { name: 'Alice', role: 'EMPLOYEE', email: 'alice@co.com' } }
    })
    setup()
    fireEvent.change(screen.getByPlaceholderText('Enter your email'), { target: { value: 'alice@co.com' } })
    fireEvent.change(screen.getByPlaceholderText('Enter your password'), { target: { value: 'pass123' } })
    fireEvent.click(screen.getByRole('button', { name: /login/i }))
    await waitFor(() => expect(loginAPI).toHaveBeenCalledWith('alice@co.com', 'pass123'))
  })

  test('navigates to /employee/dashboard for EMPLOYEE role', async () => {
    loginAPI.mockResolvedValueOnce({
      data: { token: 'tok', user: { name: 'Alice', role: 'EMPLOYEE', email: 'alice@co.com' } }
    })
    setup()
    fireEvent.change(screen.getByPlaceholderText('Enter your email'), { target: { value: 'alice@co.com' } })
    fireEvent.change(screen.getByPlaceholderText('Enter your password'), { target: { value: 'pass' } })
    fireEvent.click(screen.getByRole('button', { name: /login/i }))
    await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith('/employee/dashboard'))
  })

  test('navigates to /admin/dashboard for ADMIN role', async () => {
    loginAPI.mockResolvedValueOnce({
      data: { token: 'tok', user: { name: 'Admin', role: 'ADMIN', email: 'admin@co.com' } }
    })
    setup()
    fireEvent.change(screen.getByPlaceholderText('Enter your email'), { target: { value: 'admin@co.com' } })
    fireEvent.change(screen.getByPlaceholderText('Enter your password'), { target: { value: 'Admin@123' } })
    fireEvent.click(screen.getByRole('button', { name: /login/i }))
    await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith('/admin/dashboard'))
  })

  test('shows error message on wrong credentials', async () => {
    loginAPI.mockRejectedValueOnce({ response: { data: { message: 'Invalid Login' } } })
    setup()
    fireEvent.change(screen.getByPlaceholderText('Enter your email'), { target: { value: 'x@co.com' } })
    fireEvent.change(screen.getByPlaceholderText('Enter your password'), { target: { value: 'wrong' } })
    fireEvent.click(screen.getByRole('button', { name: /login/i }))
    await waitFor(() => expect(screen.getByText('Invalid Login')).toBeInTheDocument())
  })

  test('button text changes to Please wait while loading', async () => {
    loginAPI.mockImplementationOnce(() => new Promise(r => setTimeout(r, 2000)))
    setup()
    fireEvent.change(screen.getByPlaceholderText('Enter your email'), { target: { value: 'a@b.com' } })
    fireEvent.change(screen.getByPlaceholderText('Enter your password'), { target: { value: 'pw' } })
    fireEvent.click(screen.getByRole('button', { name: /login/i }))
    expect(screen.getByText('Please wait...')).toBeInTheDocument()
  })
})
