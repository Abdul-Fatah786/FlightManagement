# 🚀 GitHub Codespaces Setup Guide

## Quick Start with GitHub Codespaces

### Step 1: Create GitHub Repository

1. Go to [GitHub.com](https://github.com)
2. Click "+" → "New repository"
3. Name it: `flight-management-system`
4. Make it Public or Private
5. Click "Create repository"

### Step 2: Push Your Code to GitHub

```bash
# Navigate to your project root
cd "c:\Users\DELL\Downloads\HospitalManagement-master\HospitalManagement-master"

# Initialize git (if not already)
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit: Flight Management System"

# Add remote (replace YOUR-USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR-USERNAME/flight-management-system.git

# Push to GitHub
git branch -M main
git push -u origin main
```

### Step 3: Launch GitHub Codespaces

1. Go to your GitHub repository
2. Click the green **"Code"** button
3. Click **"Codespaces"** tab
4. Click **"Create codespace on main"**
5. Wait for the environment to load (1-2 minutes)

### Step 4: Setup Backend in Codespaces

Once your codespace loads:

```bash
# Navigate to backend
cd backend

# Install dependencies
npm install

# Create .env file
cp .env.example .env

# Edit .env with nano
nano .env
```

In nano editor, paste your Neon Postgres credentials:
```env
DATABASE_URL=postgresql://[user]:[password]@[host]/[database]?sslmode=require
JWT_SECRET=your-jwt-secret-key-here
PORT=3000
NODE_ENV=development
CORS_ORIGIN=*
```

Press `Ctrl + X`, then `Y`, then `Enter` to save.

### Step 5: Initialize Database

```bash
# Initialize database tables
npm run init-db

# Seed database with sample data
node scripts/seedDatabase.js
```

### Step 6: Start Backend Server

```bash
# Start the server
npm run dev
```

You should see:
```
🚀 Flight Management System API is running on port 3000
```

### Step 7: Access Your API

GitHub Codespaces will automatically forward port 3000. You'll see a popup:

- Click **"Open in Browser"** or
- Go to the **"PORTS"** tab at the bottom
- Click the globe icon next to port 3000

Your API will be accessible at: `https://YOUR-CODESPACE-NAME.github.dev/`

## Testing Your API in Codespaces

### Option 1: Use the Built-in Terminal

```bash
# Test health endpoint
curl http://localhost:3000/health

# Register a user
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "name": "Test User",
    "phone": "+1234567890"
  }'

# Login
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@flightmanagement.com",
    "password": "admin123"
  }'
```

### Option 2: Install Thunder Client Extension

1. In Codespaces, click Extensions icon (left sidebar)
2. Search for "Thunder Client"
3. Install it
4. Use Thunder Client to test API endpoints

### Option 3: Use the Forwarded URL

Get your public URL:
1. Click **"PORTS"** tab at bottom
2. Right-click port 3000
3. Select **"Copy Local Address"**
4. Use this URL in Postman or your Android app

## Running Android App with Codespaces Backend

### Update Android App API URL

1. Open `app/src/main/java/com/example/flightmanagement/api/RetrofitClient.java`

2. Replace the BASE_URL with your Codespaces forwarded URL:

```java
// Get the URL from PORTS tab in Codespaces
private static final String BASE_URL = "https://your-codespace-name-3000.app.github.dev/api/";
```

3. Make sure the URL ends with `/api/`

4. Build and run your Android app!

## Managing Your Codespace

### Keep Codespace Alive
- Codespaces auto-sleep after 30 minutes of inactivity
- Just reopen it from GitHub → Your Repository → Code → Codespaces

### Stop Codespace
```bash
# Just close the browser tab
# Or click your profile → Codespaces → Stop codespace
```

### Delete Codespace
- Go to [github.com/codespaces](https://github.com/codespaces)
- Click "..." next to your codespace
- Click "Delete"

## Environment Variables in Codespaces

### Set Secrets for Auto-Configuration

1. Go to your repository settings
2. Click **"Secrets and variables"** → **"Codespaces"**
3. Add secrets:
   - `DATABASE_URL`
   - `JWT_SECRET`

Then in your codespace, they'll be automatically available!

## Useful Codespaces Commands

```bash
# View running processes
ps aux | grep node

# Check port status
lsof -i :3000

# View logs
npm run dev 2>&1 | tee api.log

# Stop server
Ctrl + C
```

## Tips for Codespaces

1. **Free Tier:** You get 60 hours/month free (120 hours for Pro)
2. **Machine Type:** Use 2-core for development (4-core for heavy work)
3. **Auto-save:** Your work is saved automatically
4. **Extensions:** Install extensions you need (they persist)
5. **Terminal:** You can open multiple terminals

## Troubleshooting

### Port not forwarding?
- Check PORTS tab at bottom
- Click "Forward a Port" button
- Enter 3000

### Can't access API from Android?
- Make sure URL has `https://`
- Make sure URL ends with `/api/`
- Check if port is public (PORTS tab → right-click → Port Visibility → Public)

### Database connection error?
- Verify DATABASE_URL in `.env`
- Check Neon database is active
- Test connection: `node -e "import('./config/database.js').then(m => m.testConnection())"`

## Quick Reference - Seed Data Login

After running `seedDatabase.js`:

**Admin Account:**
- Email: `admin@flightmanagement.com`
- Password: `admin123`

**Sample Users:**
- Email: `john.doe@email.com`
- Password: `password123`
- (All sample users have password: `password123`)

**Sample Flights:** 12 flights from various origins to destinations

## Next Steps

1. ✅ Backend running in Codespaces
2. ✅ Database seeded with sample data
3. ✅ API accessible via forwarded URL
4. ⬜ Update Android app with Codespaces URL
5. ⬜ Build and test Android app

---

**Happy Coding! 🚀**
