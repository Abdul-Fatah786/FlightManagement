# 🚀 Quick Reference - Flight Management System

## Backend Quick Start

```bash
cd backend
npm install
cp .env.example .env
# Edit .env with your Neon DB credentials
npm run init-db
npm run dev
```

**API Base URL:** `http://localhost:3000/api/`

## Essential API Endpoints

### Authentication
```
POST   /api/auth/register      # Register user
POST   /api/auth/login         # Login user
GET    /api/auth/me            # Get current user (Protected)
```

### Flights
```
GET    /api/flights            # Get all flights
GET    /api/flights/search     # Search flights
POST   /api/flights            # Create flight (Admin)
```

### Bookings
```
POST   /api/bookings           # Create booking
GET    /api/bookings           # Get my bookings
PATCH  /api/bookings/:id/cancel # Cancel booking
```

## Quick Test Commands

### Register User
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

### Login
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

### Create Flight (Admin)
```bash
curl -X POST http://localhost:3000/api/flights \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "flight_number": "FM101",
    "airline": "Flight Air",
    "origin": "New York",
    "destination": "London",
    "departure_time": "2026-02-15T10:00:00",
    "arrival_time": "2026-02-15T22:00:00",
    "total_seats": 180,
    "available_seats": 180,
    "price": 599.99,
    "status": "scheduled"
  }'
```

## Android Quick Setup

### Update API URL
File: `app/src/main/java/com/example/flightmanagement/api/RetrofitClient.java`

```java
// For Emulator
private static final String BASE_URL = "http://10.0.2.2:3000/api/";

// For Physical Device (replace with your IP)
private static final String BASE_URL = "http://192.168.1.XXX:3000/api/";
```

### Get Your Local IP
```bash
# Windows
ipconfig

# Mac/Linux
ifconfig | grep inet
```

## Database Tables

```sql
users        # id, email, password, name, phone, role
flights      # id, flight_number, airline, origin, destination, times, seats, price
passengers   # id, user_id, name, passport, nationality
bookings     # id, user_id, flight_id, passenger_id, seat, class, status
```

## Environment Variables (.env)

```env
DATABASE_URL=postgresql://user:pass@host/db?sslmode=require
JWT_SECRET=your-secret-key
PORT=3000
NODE_ENV=development
CORS_ORIGIN=*
```

## Common Issues & Fixes

### Backend won't start
```bash
# Check if port is in use
netstat -ano | findstr :3000

# Use different port
# Edit .env: PORT=3001
```

### Database connection failed
- Verify DATABASE_URL in .env
- Check internet connection
- Ensure Neon database is active

### Android can't connect
- Check BASE_URL in RetrofitClient.java
- Ensure backend is running
- For physical device: use your local IP
- For emulator: use 10.0.2.2

### Build errors in Android
```bash
# In Android Studio
File > Invalidate Caches / Restart
```

## File Structure

```
backend/
├── config/       # Database connection
├── controllers/  # Business logic
├── middleware/   # Auth & errors
├── models/       # Database models
├── routes/       # API endpoints
└── server.js     # Main file

app/src/main/java/com/example/flightmanagement/
├── adapter/      # RecyclerView adapters
├── api/          # Retrofit API service
├── model/        # Data models
├── utils/        # SessionManager
└── *.java        # Activities
```

## Useful Commands

```bash
# Backend
npm install          # Install dependencies
npm run dev         # Start dev server
npm run init-db     # Initialize database
npm start           # Start production server

# Android
./gradlew build     # Build app
./gradlew clean     # Clean build
```

## Documentation Files

- **README.md** - Main documentation
- **backend/README.md** - API documentation
- **SETUP_GUIDE.md** - Setup instructions
- **MIGRATION_SUMMARY.md** - Transformation details
- **PROJECT_COMPLETE.md** - Project status

## Next Steps

1. ✅ Setup backend and test API
2. ✅ Create test data (users, flights)
3. ⚠️ Complete Android UI implementation
4. ⚠️ Test all features
5. ⚠️ Deploy and release

---

**Quick Help:** Read SETUP_GUIDE.md for detailed instructions
