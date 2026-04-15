import React from 'react'
import { render, screen, act } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { AuthProvider, useAuth } from '../context/AuthContext'
import Navbar from '../components/Navbar'

const mockNavigate = jest.fn()
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate
}))

function renderNavbar() {
  return render(
    <MemoryRouter><AuthProvider><Navbar /></AuthProvider></MemoryRouter>
  )
}

function renderNavbarWithUser(user) {
  function Wrapper() {
    const { login } = useAuth()
    React.useEffect(() => { login(user, 'tok') }, [])
    return <Navbar />
  }
  return render(
    <MemoryRouter><AuthProvider><Wrapper /></AuthProvider></MemoryRouter>
  )
}

describe('Navbar', () => {
  beforeEach(() => { jest.clearAllMocks(); localStorage.clear() })

  test('renders system title', () => {
    renderNavbar()
    expect(screen.getByText('Workflow Approval System')).toBeInTheDocument()
  })

  test('renders Logout button', () => {
    renderNavbar()
    expect(screen.getByRole('button', { name: /logout/i })).toBeInTheDocument()
  })

  test('shows logged in user name', async () => {
    renderNavbarWithUser({ name: 'Alice Johnson', role: 'EMPLOYEE' })
    await screen.findByText(/Alice Johnson/)
    expect(screen.getByText(/Alice Johnson/)).toBeInTheDocument()
  })

  test('shows logged in user role', async () => {
    renderNavbarWithUser({ name: 'Bob Smith', role: 'MANAGER' })
    await screen.findByText(/MANAGER/)
    expect(screen.getByText(/MANAGER/)).toBeInTheDocument()
  })

  test('logout button navigates to /login', async () => {
    renderNavbarWithUser({ name: 'Alice', role: 'EMPLOYEE' })
    await screen.findByText(/Alice/)
    act(() => screen.getByRole('button', { name: /logout/i }).click())
    expect(mockNavigate).toHaveBeenCalledWith('/login')
  })
})
