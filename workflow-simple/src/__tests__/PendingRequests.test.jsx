import React from 'react'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { AuthProvider } from '../context/AuthContext'
import PendingRequests from '../pages/manager/PendingRequests'

jest.mock('../services/api', () => ({
  getPendingRequests: jest.fn(),
  approveRequest: jest.fn(),
  rejectRequest: jest.fn()
}))

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn()
}))

import { getPendingRequests, approveRequest, rejectRequest } from '../services/api'

const sampleRequest = {
  id: 1, requestId: 'REQ0001',
  employeeName: 'Alice Johnson', employeeId: 'EMP001',
  department: 'IT', requestType: 'Leave Request',
  leaveType: 'CL', duration: 'Full Day', leavePlan: 'Planned',
  startDate: '2024-04-15', description: 'Doctor appointment',
  createdDate: '2024-04-10', status: 'PENDING'
}

function setup() {
  return render(
    <MemoryRouter><AuthProvider><PendingRequests /></AuthProvider></MemoryRouter>
  )
}

describe('PendingRequests', () => {
  beforeEach(() => jest.clearAllMocks())

  test('shows no-pending message when list is empty', async () => {
    getPendingRequests.mockResolvedValueOnce({ data: [] })
    setup()
    await waitFor(() => expect(screen.getByText(/all caught up/i)).toBeInTheDocument())
  })

  test('renders employee name from pending list', async () => {
    getPendingRequests.mockResolvedValueOnce({ data: [sampleRequest] })
    setup()
    await waitFor(() => expect(screen.getByText('Alice Johnson')).toBeInTheDocument())
  })

  test('renders request ID in the table', async () => {
    getPendingRequests.mockResolvedValueOnce({ data: [sampleRequest] })
    setup()
    await waitFor(() => expect(screen.getByText('REQ0001')).toBeInTheDocument())
  })

  test('opens modal when Approve clicked', async () => {
    getPendingRequests.mockResolvedValueOnce({ data: [sampleRequest] })
    setup()
    await waitFor(() => screen.getByText('Alice Johnson'))
    fireEvent.click(screen.getByRole('button', { name: /^approve$/i }))
    expect(screen.getByText(/confirm approve/i)).toBeInTheDocument()
  })

  test('shows remarks required error on empty submit', async () => {
    getPendingRequests.mockResolvedValueOnce({ data: [sampleRequest] })
    setup()
    await waitFor(() => screen.getByText('Alice Johnson'))
    fireEvent.click(screen.getByRole('button', { name: /^approve$/i }))
    fireEvent.click(screen.getByText(/confirm approve/i))
    expect(screen.getByText('Remarks are required')).toBeInTheDocument()
  })

  test('calls approveRequest with correct id and remarks', async () => {
    getPendingRequests.mockResolvedValue({ data: [sampleRequest] })
    approveRequest.mockResolvedValueOnce({ data: {} })
    setup()
    await waitFor(() => screen.getByText('Alice Johnson'))
    fireEvent.click(screen.getByRole('button', { name: /^approve$/i }))
    fireEvent.change(screen.getByPlaceholderText(/Approved. Enjoy/i), { target: { value: 'Approved!' } })
    fireEvent.click(screen.getByText(/confirm approve/i))
    await waitFor(() => expect(approveRequest).toHaveBeenCalledWith(1, 'Approved!'))
  })

  test('calls rejectRequest with correct id and remarks', async () => {
    getPendingRequests.mockResolvedValue({ data: [sampleRequest] })
    rejectRequest.mockResolvedValueOnce({ data: {} })
    setup()
    await waitFor(() => screen.getByText('Alice Johnson'))
    fireEvent.click(screen.getByRole('button', { name: /^reject$/i }))
    fireEvent.change(screen.getByPlaceholderText(/Insufficient leave/i), { target: { value: 'No balance.' } })
    fireEvent.click(screen.getByText(/confirm reject/i))
    await waitFor(() => expect(rejectRequest).toHaveBeenCalledWith(1, 'No balance.'))
  })

  test('modal closes when Cancel is clicked', async () => {
    getPendingRequests.mockResolvedValueOnce({ data: [sampleRequest] })
    setup()
    await waitFor(() => screen.getByText('Alice Johnson'))
    fireEvent.click(screen.getByRole('button', { name: /^approve$/i }))
    fireEvent.click(screen.getByRole('button', { name: /^cancel$/i }))
    expect(screen.queryByText(/confirm approve/i)).not.toBeInTheDocument()
  })
})
