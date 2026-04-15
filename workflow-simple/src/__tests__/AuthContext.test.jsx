import React from 'react'
import { render, screen, act } from '@testing-library/react'
import { AuthProvider, useAuth } from '../context/AuthContext'

function TestComponent() {
  const { user, token, login, logout } = useAuth()
  return (
    <div>
      <span data-testid="name">{user ? user.name : 'none'}</span>
      <span data-testid="role">{user ? user.role : 'none'}</span>
      <span data-testid="token">{token || 'none'}</span>
      <button onClick={() => login({ name: 'Alice', role: 'EMPLOYEE' }, 'tok123')}>Login</button>
      <button onClick={logout}>Logout</button>
    </div>
  )
}

describe('AuthContext', () => {
  beforeEach(() => localStorage.clear())

  test('default state has no user and no token', () => {
    render(<AuthProvider><TestComponent /></AuthProvider>)
    expect(screen.getByTestId('name').textContent).toBe('none')
    expect(screen.getByTestId('token').textContent).toBe('none')
  })

  test('login sets user name in state', () => {
    render(<AuthProvider><TestComponent /></AuthProvider>)
    act(() => screen.getByText('Login').click())
    expect(screen.getByTestId('name').textContent).toBe('Alice')
  })

  test('login sets role in state', () => {
    render(<AuthProvider><TestComponent /></AuthProvider>)
    act(() => screen.getByText('Login').click())
    expect(screen.getByTestId('role').textContent).toBe('EMPLOYEE')
  })

  test('login sets token in state', () => {
    render(<AuthProvider><TestComponent /></AuthProvider>)
    act(() => screen.getByText('Login').click())
    expect(screen.getByTestId('token').textContent).toBe('tok123')
  })

  test('login saves token to localStorage', () => {
    render(<AuthProvider><TestComponent /></AuthProvider>)
    act(() => screen.getByText('Login').click())
    expect(localStorage.getItem('token')).toBe('tok123')
  })

  test('logout clears user from state', () => {
    render(<AuthProvider><TestComponent /></AuthProvider>)
    act(() => screen.getByText('Login').click())
    act(() => screen.getByText('Logout').click())
    expect(screen.getByTestId('name').textContent).toBe('none')
  })

  test('logout clears token from state', () => {
    render(<AuthProvider><TestComponent /></AuthProvider>)
    act(() => screen.getByText('Login').click())
    act(() => screen.getByText('Logout').click())
    expect(screen.getByTestId('token').textContent).toBe('none')
  })

  test('logout removes token from localStorage', () => {
    render(<AuthProvider><TestComponent /></AuthProvider>)
    act(() => screen.getByText('Login').click())
    act(() => screen.getByText('Logout').click())
    expect(localStorage.getItem('token')).toBeNull()
  })

  test('restores user session from localStorage on mount', () => {
    localStorage.setItem('token', 'saved')
    localStorage.setItem('user', JSON.stringify({ name: 'Bob', role: 'MANAGER' }))
    render(<AuthProvider><TestComponent /></AuthProvider>)
    expect(screen.getByTestId('name').textContent).toBe('Bob')
    expect(screen.getByTestId('token').textContent).toBe('saved')
  })
})
