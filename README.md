# Project2
## Hospital Management System

A backend RESTFUL API designed to manage hospital operations efficiently.The system handles user authentication, role-based access control, and core hospital workflows such as managing doctors, patients, appointments, bookings, rooms,etc...

It supports multiple user roles **Admin**, **Doctor**, and **Patient** with secure access and clear separation of responsibilities.

# API Endpoints

| Method | Endpoint | Description | Access Role |
|--------|----------|-------------|-------------|
| POST | /api/auth/register | Register a new user | Public |
| POST | /api/auth/login | Authenticate user and return token | Public |
| POST | /api/auth/logout | Logout current user | Authenticated |
| GET | /api/users/me | Get current user profile | Authenticated |
| PUT | /api/users/me | Update current user profile | Authenticated |
| GET | /api/users | Get all users | Admin |
| GET | /api/users/{id} | Get user by ID | Admin |
| DELETE | /api/users/{id} | Delete user | Admin |
| GET | /api/doctors | Get all doctors | Authenticated |
| GET | /api/doctors/{id} | Get doctor profile | Authenticated |
| PUT | /api/doctors/{id}/availability | Set doctor working days and hours | Doctor |
| GET | /api/doctors/{id}/appointments | Get doctor appointments | Doctor |
| GET | /api/patients/{id}/bookings | Get patient bookings | Patient |
| POST | /api/rooms | Create room | Admin |
| GET | /api/rooms | Get all rooms | Authenticated |
| GET | /api/rooms/{id} | Get room details | Authenticated |
| PUT | /api/rooms/{id} | Update room | Admin |
| DELETE | /api/rooms/{id} | Delete room | Admin |
| POST | /api/treatments | Create treatment type | Admin |
| GET | /api/treatments | Get all treatment types | Authenticated |
| PUT | /api/treatments/{id} | Update treatment type | Admin |
| DELETE | /api/treatments/{id} | Delete treatment type | Admin |
| POST | /api/appointments | Create appointment slot | Admin |
| GET | /api/appointments | Get all appointments | Authenticated |
| GET | /api/appointments/available | Get available appointments | Patient |
| GET | /api/appointments/{id} | Get appointment details | Authenticated |
| DELETE | /api/appointments/{id} | Delete appointment | Admin |
| POST | /api/bookings | Book an appointment | Patient |
| GET | /api/bookings/{id} | Get booking details | Patient |
| DELETE | /api/bookings/{id} | Cancel booking | Patient |
| POST | /api/roles | Create role | Admin |
| GET | /api/roles | Get all roles | Admin |
| POST | /api/permissions | Create permission | Admin |
| GET | /api/permissions | Get all permissions | Admin |
| PUT | /api/roles/{id}/permissions | Assign permissions to role | Admin |



## trello link
https://trello.com/invite/b/695d2d0130962712434d4c6f/ATTIce8d7818f85a379fc47b04c978d056e56AE3CFDE/my-trello-board