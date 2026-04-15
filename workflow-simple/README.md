# Workflow Approval System - Frontend

Simple React frontend for the Office Workflow Approval System.

## Setup

```
npm install
npm run dev
```

App runs at http://localhost:3000
Backend must be running at http://localhost:8080

## Run Tests

```
npm test
```

## Login Credentials

| Role     | Email                 | Password     |
|----------|-----------------------|--------------|
| Admin    | admin@company.com     | Admin@123    |
| Manager  | bob@company.com       | Manager@123  |
| Employee | alice@company.com     | Employee@123 |

## Pages

Admin    - Dashboard, Manage Employees, Master Data
Employee - Dashboard, My Requests, New Request, Notifications
Manager  - Dashboard, Pending Requests, Approval History
