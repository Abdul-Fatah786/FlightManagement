# 🚀 Complete Setup & Start Guide

## ✅ Project Status

**Backend: 100% READY** ✅
- Express.js API with 30+ endpoints
- Neon Postgres database
- JWT authentication
- Full CRUD operations

**Android App: Structure Ready** ✅
- Package updated to `flightmanagement`
- All models and API service created
- Old `hospitalmanagement` package removed
- Ready for UI implementation

---

## 🎯 Quick Start (3 Options)

### Option 1: Local Development

```bash
# 1. Setup Backend
cd backend
npm install
cp .env.example .env
# Edit .env with your Neon Postgres credentials
npm run init-db
npm run seed-db
npm run dev

# 2. Update Android App
# In RetrofitClient.java, set:
# BASE_URL = "http://10.0.2.2:3000/api/" (for emulator)
# Or BASE_URL = "http://YOUR_LOCAL_IP:3000/api/" (for device)

# 3. Run Android App in Android Studio
```

### Option 2: GitHub Codespaces (Recommended) ⭐

```bash
# 1. Push to GitHub
git init
git add .
git commit -m "Flight Management System"
git remote add origin https://github.com/YOUR-USERNAME/flight-management-system.git
git push -u origin main

# 2. Open Codespace (on GitHub.com)
# Click Code → Codespaces → Create codespace

# 3. In Codespace terminal:
cd backend
npm install
cp .env.example .env
nano .env  # Add your Neon Postgres credentials
npm run setup  # Initializes DB and seeds data
npm run dev

# 4. Get your Codespace URL from PORTS tab (e.g., https://xxx-3000.app.github.dev)

# 5. Update Android app with Codespace URL
```

### Option 3: Docker (Coming Soon)

---

## 📊 Database Seed Data

After running `npm run seed-db`, you get:

### 🔐 Admin Account
```
Email: admin@flightmanagement.com
Password: admin123
Role: Admin (can manage all flights and bookings)
```

### 👥 Sample Passenger Accounts
```
All use password: password123

1. john.doe@example.com
2. jane.smith@example.com  
3. mike.wilson@example.com
4. sarah.jones@example.com
5. david.brown@example.com
```

### ✈️ 10 Sample Flights

| Flight | Route | Airline | Price | Date |
|--------|-------|---------|-------|------|
| FM101 | New York → London | Flight Air | $599.99 | Feb 15 |
| FM102 | London → New York | Flight Air | $649.99 | Feb 16 |
| FM201 | Los Angeles → Tokyo | Sky Express | $899.99 | Feb 20 |
| FM202 | Tokyo → Los Angeles | Sky Express | $849.99 | Feb 22 |
| FM301 | Paris → Dubai | Global Wings | $499.99 | Feb 18 |
| FM302 | Dubai → Singapore | Global Wings | $699.99 | Feb 19 |
| FM401 | Sydney → Singapore | Pacific Air | $399.99 | Feb 25 |
| FM501 | Miami → Barcelona | Atlantic Airways | $549.99 | Feb 28 |
| FM601 | Stockholm → New York | Nordic Air | $599.99 | Mar 5 |
| FM701 | Mumbai → London | Asia Connect | $699.99 | Mar 10 |

### 🎫 4 Passenger Profiles
- John Doe (US Passport)
- Jane Smith (US Passport)
- Mike Wilson (UK Passport)
- Emily Doe (US Passport)

### 📋 4 Sample Bookings
- Already confirmed bookings for testing

---

## 🧪 Testing the API

### Quick Tests

```bash
# Health Check
curl http://localhost:3000/health

# Login as Admin
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@flightmanagement.com","password":"admin123"}'

# Get All Flights (No auth needed)
curl http://localhost:3000/api/flights

# Search Flights
curl "http://localhost:3000/api/flights/search?origin=New%20York&destination=London"
```

### Get Your Auth Token

```bash
# Login and extract token
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@flightmanagement.com","password":"admin123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4
```

### Test Protected Endpoints

```bash
# Replace YOUR_TOKEN with the token from login
TOKEN="YOUR_TOKEN_HERE"

# Get Current User
curl http://localhost:3000/api/auth/me \
  -H "Authorization: Bearer $TOKEN"

# Get My Bookings
curl http://localhost:3000/api/bookings \
  -H "Authorization: Bearer $TOKEN"

# Create Flight (Admin only)
curl -X POST http://localhost:3000/api/flights \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "flight_number": "FM999",
    "airline": "Test Air",
    "origin": "New York",
    "destination": "Paris",
    "departure_time": "2026-03-15T10:00:00",
    "arrival_time": "2026-03-15T22:00:00",
    "total_seats": 200,
    "available_seats": 200,
    "price": 699.99,
    "status": "scheduled"
  }'
```

---

## 📱 Android App Setup

### 1. Update API URL

**File:** `app/src/main/java/com/example/flightmanagement/api/RetrofitClient.java`

```java
// For Android Emulator (local backend)
private static final String BASE_URL = "http://10.0.2.2:3000/api/";

// For Physical Device (local backend)
private static final String BASE_URL = "http://192.168.1.XXX:3000/api/";

// For GitHub Codespaces
private static final String BASE_URL = "https://your-codespace-3000.app.github.dev/api/";

// For Production
private static final String BASE_URL = "https://your-domain.com/api/";
```

### 2. Build & Run

1. Open project in Android Studio
2. Sync Gradle files
3. Build → Make Project
4. Run app on emulator or device

### 3. Test Login

Use the sample credentials:
- Email: `john.doe@example.com`
- Password: `password123`

---

## 🔄 Database Management Commands

```bash
# Initialize database (creates tables)
npm run init-db

# Seed database with sample data
npm run seed-db

# Do both in one command
npm run setup

# Reset everything (WARNING: Deletes all data!)
npm run init-db && npm run seed-db
```

---

## 🌐 GitHub Codespaces Detailed Setup

### Step-by-Step

1. **Create GitHub Repository**
   ```bash
   # On GitHub.com
   Click "+" → New Repository → Name: "flight-management-system"
   ```

2. **Push Your Code**
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git remote add origin https://github.com/YOUR-USERNAME/flight-management-system.git
   git push -u origin main
   ```

3. **Launch Codespace**
   - Go to your repository on GitHub
   - Click green "Code" button
   - Click "Codespaces" tab
   - Click "Create codespace on main"
   - Wait 1-2 minutes for setup

4. **Setup in Codespace**
   ```bash
   cd backend
   npm install
   cp .env.example .env
   nano .env  # Add your Neon credentials
   # Save: Ctrl+X, Y, Enter
   npm run setup
   npm run dev
   ```

5. **Get Your URL**
   - Click "PORTS" tab at bottom
   - Find port 3000
   - Click globe icon 🌐
   - Copy the URL (e.g., `https://xxx-3000.app.github.dev`)

6. **Make Port Public**
   - Right-click port 3000 in PORTS tab
   - Port Visibility → Public

7. **Update Android App**
   - Use the Codespace URL in `RetrofitClient.java`

---

## 📝 Environment Variables

Create `backend/.env` with:

```env
# Required: Neon Postgres Connection
DATABASE_URL=postgresql://[user]:[password]@[host]/[database]?sslmode=require

# Required: JWT Secret (generate random string)
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production

# Optional: Server Configuration
PORT=3000
NODE_ENV=development

# Optional: CORS (use * for development, specific domain for production)
CORS_ORIGIN=*
```

### Generate Secure JWT Secret

```bash
# On Linux/Mac
openssl rand -base64 32

# On Windows (PowerShell)
[Convert]::ToBase64String((1..32|%{Get-Random -Max 256}))

# Or use Node.js
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
```

---

## 🎯 API Endpoints Reference

### Public Endpoints (No Auth)
- `GET /health` - Health check
- `POST /api/auth/register` - Register user
- `POST /api/auth/login` - Login user
- `GET /api/flights` - List all flights
- `GET /api/flights/search` - Search flights
- `GET /api/flights/:id` - Get flight details

### Protected Endpoints (Requires Token)
- `GET /api/auth/me` - Get current user
- `GET /api/bookings` - Get user's bookings
- `POST /api/bookings` - Create booking
- `PATCH /api/bookings/:id/cancel` - Cancel booking
- `GET /api/passengers` - Get user's passengers
- `POST /api/passengers` - Add passenger
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update profile

### Admin Only Endpoints
- `POST /api/flights` - Create flight
- `PUT /api/flights/:id` - Update flight
- `DELETE /api/flights/:id` - Delete flight
- `GET /api/admin/users` - Get all users
- `GET /api/admin/bookings` - Get all bookings
- `DELETE /api/admin/users/:id` - Delete user

---

## 🐛 Troubleshooting

### Backend Issues

**Problem:** `DATABASE_URL is not defined`
```bash
# Solution: Check .env file exists
ls -la backend/.env
# Make sure DATABASE_URL is set correctly
```

**Problem:** `Port 3000 already in use`
```bash
# Solution: Change port in .env
PORT=3001

# Or kill the process
# Windows: netstat -ano | findstr :3000
# Linux/Mac: lsof -ti:3000 | xargs kill -9
```

**Problem:** `Cannot connect to database`
```bash
# Solution: Test database connection
node -e "import('./backend/config/database.js').then(m => m.testConnection())"
```

### Android Issues

**Problem:** `Failed to connect to /10.0.2.2:3000`
```bash
# Solution: Make sure backend is running
# Check the correct IP:
# - Emulator: 10.0.2.2
# - Device: Your computer's local IP (ipconfig/ifconfig)
```

**Problem:** `401 Unauthorized`
```bash
# Solution: Check if token is being sent correctly
# Token should be in header: Authorization: Bearer TOKEN
```

---

## 📚 Documentation Files

- **README.md** - Project overview and features
- **SETUP_GUIDE.md** - Detailed setup instructions
- **GITHUB_CODESPACES.md** - Codespaces-specific guide
- **MIGRATION_SUMMARY.md** - Transformation documentation
- **backend/README.md** - API documentation
- **THIS FILE** - Quick reference guide

---

## ✅ Checklist

### Backend Setup
- [ ] Node.js installed
- [ ] Neon Postgres account created
- [ ] Dependencies installed (`npm install`)
- [ ] `.env` file configured
- [ ] Database initialized (`npm run init-db`)
- [ ] Database seeded (`npm run seed-db`)
- [ ] Server running (`npm run dev`)
- [ ] API tested (health check)

### Android Setup
- [ ] Android Studio installed
- [ ] Project opened
- [ ] Gradle synced
- [ ] API URL updated in `RetrofitClient.java`
- [ ] App builds successfully
- [ ] Can connect to backend
- [ ] Login works with test accounts

---

## 🎊 Success! You're Ready to Fly! ✈️

Your Flight Management System is ready with:
- ✅ Production-ready backend API
- ✅ Secure authentication system
- ✅ Sample data for testing
- ✅ Complete documentation
- ✅ Android app foundation

**Login and explore:**
- Admin: `admin@flightmanagement.com` / `admin123`
- Passenger: `john.doe@example.com` / `password123`

---

**Need Help?** Check the documentation files or review the setup steps above!
