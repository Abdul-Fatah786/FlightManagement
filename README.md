# ✈️ Flight Management System

A comprehensive Flight Management System with an Android frontend and Express.js backend using Neon Postgres database.

## 📱 Project Overview

This system provides a complete solution for managing flights, bookings, passengers, and user accounts. It consists of:

- **Android Application**: Native Android app for passengers and administrators
- **Express.js Backend**: RESTful API server with authentication and authorization
- **Neon Postgres Database**: Cloud-hosted PostgreSQL database for data persistence

## 🏗️ Architecture

```
Flight-Management-System/
├── app/                          # Android Application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/flightmanagement/
│   │   │   ├── res/              # Android resources
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   └── build.gradle.kts
├── backend/                      # Express.js Backend
│   ├── config/                   # Database configuration
│   ├── controllers/              # Route controllers
│   ├── middleware/               # Authentication & error handling
│   ├── models/                   # Database models
│   ├── routes/                   # API routes
│   ├── scripts/                  # Database initialization
│   └── server.js                 # Main server file
└── README.md
```

## 🚀 Features

### For Passengers
- ✅ User registration and authentication
- ✅ Search and browse available flights
- ✅ Book flights with passenger details
- ✅ View booking history
- ✅ Manage passenger profiles
- ✅ Cancel bookings
- ✅ View booking confirmation and details

### For Administrators
- ✅ Manage flights (create, update, delete)
- ✅ View all bookings and passengers
- ✅ Manage user accounts
- ✅ Track flight status
- ✅ Monitor system activity

## 🛠️ Technology Stack

### Backend
- **Runtime**: Node.js
- **Framework**: Express.js
- **Database**: Neon Postgres (Serverless PostgreSQL)
- **Authentication**: JWT (JSON Web Tokens)
- **Password Hashing**: bcrypt
- **API Documentation**: RESTful API

### Android App
- **Language**: Java
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **UI Components**: Material Design 3
- **Networking**: Retrofit 2
- **Architecture**: MVC Pattern

## 📦 Installation & Setup

### Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Install dependencies:
```bash
npm install
```

3. Create environment file:
```bash
cp .env.example .env
```

4. Configure your Neon Postgres database in `.env`:
```env
DATABASE_URL=postgresql://[user]:[password]@[host]/[database]?sslmode=require
JWT_SECRET=your-super-secret-jwt-key
PORT=3000
NODE_ENV=development
```

5. Initialize the database:
```bash
npm run init-db
```

6. Start the server:
```bash
# Development
npm run dev

# Production
npm start
```

### Android App Setup

1. Open the project in Android Studio

2. Update the API base URL in your API service configuration:
   - For emulator: `http://10.0.2.2:3000/api`
   - For physical device: `http://YOUR_LOCAL_IP:3000/api`
   - For production: `https://your-production-api.com/api`

3. Sync Gradle files

4. Build and run the application

## 🔌 API Endpoints

### Authentication
```
POST   /api/auth/register          Register new user
POST   /api/auth/login             Login user
GET    /api/auth/me                Get current user (Protected)
```

### Flights
```
GET    /api/flights                Get all flights
GET    /api/flights/search         Search flights
GET    /api/flights/:id            Get flight details
POST   /api/flights                Create flight (Admin)
PUT    /api/flights/:id            Update flight (Admin)
DELETE /api/flights/:id            Delete flight (Admin)
```

### Bookings
```
POST   /api/bookings               Create booking (Protected)
GET    /api/bookings               Get user bookings (Protected)
GET    /api/bookings/:id           Get booking details (Protected)
PATCH  /api/bookings/:id/cancel    Cancel booking (Protected)
DELETE /api/bookings/:id           Delete booking (Admin)
```

### Passengers
```
POST   /api/passengers             Add passenger (Protected)
GET    /api/passengers             Get user passengers (Protected)
GET    /api/passengers/:id         Get passenger details (Protected)
PUT    /api/passengers/:id         Update passenger (Protected)
DELETE /api/passengers/:id         Delete passenger (Protected)
```

### Admin
```
GET    /api/admin/users            Get all users
GET    /api/admin/bookings         Get all bookings
GET    /api/admin/passengers       Get all passengers
DELETE /api/admin/users/:id        Delete user
```

## 📊 Database Schema

### Users Table
- id (PRIMARY KEY)
- email (UNIQUE)
- password (hashed)
- name
- phone
- role (passenger/admin)
- timestamps

### Flights Table
- id (PRIMARY KEY)
- flight_number (UNIQUE)
- airline
- origin
- destination
- departure_time
- arrival_time
- total_seats
- available_seats
- price
- status
- timestamps

### Passengers Table
- id (PRIMARY KEY)
- user_id (FOREIGN KEY)
- first_name
- last_name
- date_of_birth
- passport_number (UNIQUE)
- nationality
- email
- phone
- timestamps

### Bookings Table
- id (PRIMARY KEY)
- booking_reference (UNIQUE)
- user_id (FOREIGN KEY)
- flight_id (FOREIGN KEY)
- passenger_id (FOREIGN KEY)
- seat_number
- booking_class
- status
- timestamps

## 🔐 Security Features

- Password hashing with bcrypt
- JWT-based authentication
- Role-based access control (RBAC)
- Protected API endpoints
- SQL injection prevention
- CORS configuration
- Input validation

## 🧪 Testing

### Backend Testing
```bash
# Run tests (when implemented)
npm test
```

### API Testing
Use tools like:
- Postman
- Thunder Client
- curl

Example test:
```bash
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "name": "Test User",
    "phone": "+1234567890"
  }'
```

## 📱 Android App Screens

1. **Splash Screen**: App introduction
2. **Onboarding**: Feature walkthrough
3. **Login/Signup**: User authentication
4. **Home**: Dashboard with flight search
5. **Flight Search**: Search and filter flights
6. **Flight Details**: View flight information
7. **Booking**: Create new booking
8. **My Bookings**: View booking history
9. **Passengers**: Manage passenger profiles
10. **Profile**: User account settings
11. **Admin Panel**: Administrative controls (Admin only)

## 🌟 Future Enhancements

- [ ] Push notifications for flight updates
- [ ] Payment gateway integration
- [ ] Seat selection visualization
- [ ] Real-time flight tracking
- [ ] Multi-language support
- [ ] Dark mode
- [ ] Flight recommendations
- [ ] Loyalty program
- [ ] In-app chat support
- [ ] Baggage tracking

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the ISC License.

## 👥 Authors

- Your Name - Initial work

## 🙏 Acknowledgments

- Material Design guidelines
- Neon Postgres documentation
- Express.js community
- Android development community

## 📞 Support

For support, email support@flightmanagement.com or join our Slack channel.

---

Made with ❤️ for travelers worldwide
