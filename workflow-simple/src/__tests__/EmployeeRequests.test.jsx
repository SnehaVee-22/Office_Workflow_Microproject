import React from 'react'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { AuthProvider } from '../context/AuthContext'
import EmployeeRequests from '../pages/employee/EmployeeRequests'

jest.mock('../services/api', () => ({
  getMyRequests: jest.fn(),
  cancelRequest: jest.fn(),
  updateRequest: jest.fn(),
  searchRequest: jest.fn()
}))

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn()
}))

import { getMyRequests, cancelRequest } from '../services/api'

const sampleRequests = [
  {
    id: 1, requestId: 'REQ0001', requestType: 'Leave Request',
    leaveType: 'CL', duration: 'Full Day', leavePlan: 'Planned',
    description: 'Annual leave', createdDate: '2024-04-01',
    status: 'PENDING', managerRemarks: null
  },
  {
    id: 2, requestId: 'REQ0002', requestType: 'Software Requirement',
    softwareName: 'Figma', description: 'Need Figma',
    createdDate: '2024-04-05', status: 'APPROVED',
    managerRemarks: 'Approved'
  }
]

function setup() {
  return render(
    <MemoryRouter><AuthProvider><EmployeeRequests /></AuthProvider></MemoryRouter>
  )
}

describe('EmployeeRequests', () => {
  beforeEach(() => { jest.clearAllMocks(); window.confirm = jest.fn(() => true) })

  test('renders page title', async () => {
    getMyRequests.mockResolvedValueOnce({ data: [] })
    setup()
    expect(screen.getByText('My Requests')).toBeInTheDocument()
  })

  test('renders request rows from API', async () => {
    getMyRequests.mockResolvedValueOnce({ data: sampleRequests })
    setup()
    await waitFor(() => expect(screen.getByText('REQ0001')).toBeInTheDocument())
    expect(screen.getByText('REQ0002')).toBeInTheDocument()
  })

  test('shows no requests message when list is empty', async () => {
    getMyRequests.mockResolvedValueOnce({ data: [] })
    setup()
    await waitFor(() => expect(screen.getByText('No requests found')).toBeInTheDocument())
  })

  test('shows Edit and Cancel buttons only for PENDING requests', async () => {
    getMyRequests.mockResolvedValueOnce({ data: sampleRequests })
    setup()
    await waitFor(() => screen.getByText('REQ0001'))
    expect(screen.getByRole('button', { name: /edit/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /cancel/i })).toBeInTheDocument()
  })

  test('calls cancelRequest when Cancel is confirmed', async () => {
    getMyRequests.mockResolvedValue({ data: sampleRequests })
    cancelRequest.mockResolvedValueOnce({ data: {} })
    setup()
    await waitFor(() => screen.getByText('REQ0001'))
    fireEvent.click(screen.getByRole('button', { name: /cancel/i }))
    await waitFor(() => expect(cancelRequest).toHaveBeenCalledWith(1))
  })

  test('opens edit modal when Edit is clicked', async () => {
    getMyRequests.mockResolvedValueOnce({ data: sampleRequests })
    setup()
    await waitFor(() => screen.getByText('REQ0001'))
    fireEvent.click(screen.getByRole('button', { name: /edit/i }))
    expect(screen.getByText(/edit request/i)).toBeInTheDocument()
  })

  test('renders New Request button', async () => {
    getMyRequests.mockResolvedValueOnce({ data: [] })
    setup()
    expect(screen.getByRole('button', { name: /new request/i })).toBeInTheDocument()
  })
})
