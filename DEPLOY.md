# Deploying Premier League App to Railway

## One-Time Setup (5 minutes)

### 1. Create a Railway account
- Go to https://railway.com and sign up with your GitHub account

### 2. Push your project to GitHub
```bash
cd premier-league
git init
git add .
git commit -m "Premier League app"
git remote add origin https://github.com/YOUR_USERNAME/premier-league.git
git branch -M main
git push -u origin main
```

### 3. Deploy on Railway
- Go to https://railway.com/dashboard
- Click **"New Project"** → **"Deploy from GitHub Repo"**
- Select your `premier-league` repo
- Railway auto-detects the Dockerfile and starts building

### 4. Add persistent storage (so data survives redeployments)
- In your Railway project, click on your service
- Go to **Settings** → **Volumes**
- Click **"Add Volume"**
- Set mount path to: `/app/data`
- In the **Variables** tab, add: `DATA_DIR` = `/app/data`

### 5. Get your public URL
- Go to **Settings** → **Networking**
- Click **"Generate Domain"**
- Railway gives you a URL like `premier-league-production-xxxx.up.railway.app`

That's it! Your app is live.

---

## Updating After Changes

Every time you push to GitHub, Railway auto-redeploys:

```bash
git add .
git commit -m "Added new team"
git push
```

Railway rebuilds and deploys automatically within ~1 minute.

---

## Environment Variables (already handled)

| Variable   | Purpose                        | Default  |
|-----------|--------------------------------|----------|
| `PORT`    | Server port (Railway sets this)| `8080`   |
| `DATA_DIR`| Where JSON files are saved     | `data`   |

---

## Troubleshooting

- **App won't start**: Check the deploy logs in Railway dashboard
- **Data lost after redeploy**: Make sure you added a Volume mounted at `/app/data` and set the `DATA_DIR` variable
- **Can't access the URL**: Make sure you generated a domain under Settings → Networking

## Free Tier Limits
Railway gives you $5 of free usage per month, which is plenty for a small app like this. The app sleeps after inactivity and wakes on the next request (may take a few seconds).
