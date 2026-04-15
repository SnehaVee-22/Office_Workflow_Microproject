import React from 'react'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { AuthProvider } from '../context/AuthContext'
import CreateRequest from '../pages/employee/CreateRequest'

jest.mock('../services/api', () => ({ createRequest: jest.fn() }))

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn()
}))

import { createRequest } from '../services/api'

function setup() {
  return render(
    <MemoryRouter><AuthProvider><CreateRequest /></AuthProvider></MemoryRouter>
  )
}

describe('CreateRequest', () => {
  beforeEach(() => { jest.clearAllMocks(); window.alert = jest.fn() })

  test('renders request type dropdown', () => {
    setup()
    expect(screen.getByText('-- Select --')).toBeInTheDocument()
  })

  test('shows leave fields when Leave Request is selected', () => {
    setup()
    fireEvent.change(screen.getAllByRole('combobox')[0], { target: { value: 'Leave Request' } })
    expect(screen.getByText('Leave Type *')).toBeInTheDocument()
    expect(screen.getByText('Duration *')).toBeInTheDocument()
    expect(screen.getByText('Leave Plan *')).toBeInTheDocument()
  })

  test('shows software fields when Software Requirement is selected', () => {
    setup()
    fireEvent.change(screen.getAllByRole('combobox')[0], { target: { value: 'Software Requirement' } })
    expect(screen.getByText('Software Name *')).toBeInTheDocument()
    expect(screen.getByText('Reason *')).toBeInTheDocument()
  })

  test('shows manager note after selecting request type', () => {
    setup()
    fireEvent.change(screen.getAllByRole('combobox')[0], { target: { value: 'Leave Request' } })
    expect(screen.getByText(/assigned manager/i)).toBeInTheDocument()
  })

  test('shows error when description is missing on submit', async () => {
    setup()
    fireEvent.change(screen.getAllByRole('combobox')[0], { target: { value: 'Software Requirement' } })
    await waitFor(() => screen.getByText('Software Name *'))
    fireEvent.change(screen.getByPlaceholderText('e.g. VS Code, Figma'), { target: { value: 'Figma' } })
    fireEvent.change(screen.getByPlaceholderText('Why do you need it?'), { target: { value: 'Design' } })
    fireEvent.click(screen.getByRole('button', { name: /submit request/i }))
    await waitFor(() => expect(screen.getByText('Description is required')).toBeInTheDocument())
  })

  test('calls createRequest API with valid data', async () => {
    createRequest.mockResolvedValueOnce({ data: {} })
    setup()
    fireEvent.change(screen.getAllByRole('combobox')[0], { target: { value: 'Software Requirement' } })
    await waitFor(() => screen.getByText('Software Name *'))
    fireEvent.change(screen.getByPlaceholderText('e.g. VS Code, Figma'), { target: { value: 'Figma' } })
    fireEvent.change(screen.getByPlaceholderText('Why do you need it?'), { target: { value: 'Design work' } })
    fireEvent.change(screen.getByPlaceholderText('Add any additional details...'), { target: { value: 'Needed urgently' } })
    fireEvent.click(screen.getByRole('button', { name: /submit request/i }))
    await waitFor(() => expect(createRequest).toHaveBeenCalled())
  })
})
