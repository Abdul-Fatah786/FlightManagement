# 🚀 Quick Start Guide - Flight Management System

This guide will help you set up and run the Flight Management System on your local machine.

## 📋 Prerequisites Checklist

- [ ] Node.js (v18 or higher) installed
- [ ] Android Studio installed
- [ ] Neon Postgres account created
- [ ] Git installed (optional)

## 🔧 Backend Setup (5 minutes)

### Step 1: Navigate to Backend Directory
```bash
cd backend
```

### Step 2: Install Dependencies
```bash
npm install
```

### Step 3: Create Neon Postgres Database

1. Go to [Neon Console](https://console.neon.tech)
2. Click "Create Project"
3. Copy your connection string (looks like: `postgresql://user:password@host/database?sslmode=require`)

### Step 4: Configure Environment Variables

1. Copy the example env file:
```bash
cp .env.example .env
```

2. Open `.env` and update with your values:
```env
DATABASE_URL=postgresql://your-user:your-password@your-host/your-database?sslmode=require
JWT_SECRET=your-random-secret-key-here
PORT=3000
NODE_ENV=development
CORS_ORIGIN=*
```

**Generating a secure JWT_SECRET:**
```bash
# On Linux/Mac
openssl rand -base64 32

# Or use this Node.js command
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
```

### Step 5: Initialize Database
```bash
npm run init-db
```

You should see:
```
✅ Users table created
✅ Flights table created
✅ Passengers table created
✅ Bookings table created
✅ Indexes created
🎉 Database initialization completed successfully!
```

### Step 6: Start the Server
```bash
# Development mode (with auto-reload)
npm run dev

# Or production mode
npm start
```

You should see:
```
🚀 Flight Management System API is running on port 3000
📊 Environment: development
🔗 Health check: http://localhost:3000/health
```

### Step 7: Test the API

Open your browser or use curl:
```bash
curl http://localhost:3000/health
```

Expected response:
```json
{
  "status": "success",
  "message": "Flight Management System API is running",
  "timestamp": "2026-01-08T..."
}
```

## 📱 Android App Setup (10 minutes)

### Step 1: Open Project in Android Studio

1. Launch Android Studio
2. Click "Open an existing project"
3. Navigate to your project root folder
4. Click "OK"

### Step 2: Configure API Base URL

1. Open `app/src/main/java/com/example/flightmanagement/api/RetrofitClient.java`
2. Update the `BASE_URL` constant:

```java
// For Android Emulator (if backend is on your computer)
private static final String BASE_URL = "http://10.0.2.2:3000/api/";

// For Physical Device (replace YOUR_IP with your computer's IP)
private static final String BASE_URL = "http://192.168.1.XXX:3000/api/";

// For Production (when you deploy)
private static final String BASE_URL = "https://your-production-api.com/api/";
```

**Finding your local IP address:**
- Windows: Open CMD and type `ipconfig`, look for "IPv4 Address"
- Mac/Linux: Open Terminal and type `ifconfig | grep inet`, look for your local IP

### Step 3: Sync Gradle

1. Click "Sync Now" if prompted
2. Or go to **File > Sync Project with Gradle Files**
3. Wait for sync to complete

### Step 4: Build the Project

1. Go to **Build > Make Project**
2. Wait for build to complete
3. Check for any errors in the Build tab

### Step 5: Run the App

1. Connect an Android device or start an emulator
2. Click the green "Run" button (▶️)
3. Select your device
4. Wait for app installation and launch

## 🧪 Testing the Complete System

### Test 1: Register a New User

1. Open the app
2. Click "Sign Up"
3. Enter:
   - Name: Test User
   - Email: test@example.com
   - Phone: +1234567890
   - Password: password123
4. Click "Register"

### Test 2: Login

1. Enter the credentials you just created
2. Click "Login"
3. You should see the passenger dashboard

### Test 3: Create Sample Data (Backend)

Use curl, Postman, or Thunder Client to create test flights:

```bash
# First, login to get a token (you'll need an admin account)
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@flightmanagement.com",
    "password": "admin123",
    "name": "Admin User",
    "phone": "+1234567890",
    "role": "admin"
  }'

# Use the token from the response to create a flight
curl -X POST http://localhost:3000/api/flights \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
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

## 🐛 Troubleshooting

### Backend Issues

**Problem:** Database connection failed
- **Solution:** Check your DATABASE_URL in `.env` file
- Verify your Neon database is running
- Check your internet connection

**Problem:** Port 3000 already in use
- **Solution:** Change PORT in `.env` to 3001 or another available port

**Problem:** JWT_SECRET error
- **Solution:** Make sure JWT_SECRET is set in `.env` file

### Android App Issues

**Problem:** Cannot connect to API
- **Solution:** 
  - For emulator: Use `http://10.0.2.2:3000/api/`
  - For physical device: Make sure your phone and computer are on the same network
  - Check firewall settings

**Problem:** Build errors
- **Solution:**
  - Click **File > Invalidate Caches / Restart**
  - Delete `.gradle` folder and sync again
  - Update Android Studio to latest version

**Problem:** App crashes on launch
- **Solution:** Check Logcat in Android Studio for error messages

## 📚 Next Steps

1. ✅ Read the [Backend README](backend/README.md) for API documentation
2. ✅ Explore the API endpoints using Postman or Thunder Client
3. ✅ Create test data (flights, passengers, bookings)
4. ✅ Customize the Android UI to match your brand
5. ✅ Deploy the backend to a cloud service (Render, Railway, etc.)
6. ✅ Build and release the Android app

## 🔐 Creating an Admin User

By default, users are created as "passenger". To create an admin:

**Option 1: Through Registration**
```bash
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "securepassword",
    "name": "Admin User",
    "phone": "+1234567890",
    "role": "admin"
  }'
```

**Option 2: Direct Database Update**
```sql
-- Connect to your Neon database and run:
UPDATE users SET role = 'admin' WHERE email = 'test@example.com';
```

## 📞 Need Help?

- Check the [main README](README.md) for detailed documentation
- Review error messages carefully
- Check backend logs for API issues
- Check Android Logcat for app issues

---

**Congratulations! 🎉** Your Flight Management System is now up and running!
