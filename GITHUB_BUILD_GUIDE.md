# GitHub Actions se APK Build Karna (Phone Browser se)

Ye guide bilkul phone ke browser se follow ho sakti hai. AIDE ki koi zaroorat nahi.

## Step 1: GitHub Account Banayein
1. Browser mein `github.com` kholein.
2. "Sign up" par tap karein aur account bana lein (agar pehle se hai to skip karein).

## Step 2: Naya Repository Banayein
1. Login ke baad, upar right corner mein **+** icon par tap karein → **New repository**.
2. Repository ka naam dein: `QuranUnlock` (ya jo chahein).
3. **Public** ya **Private** — dono chalega, private behtar hai agar app private rakhni ho.
4. "Create repository" par tap karein.

## Step 3: Project Files Upload Karein
1. Naye khaali repo ke page par "uploading an existing file" wala link dikhega — usay tap karein.
   (Agar na dikhe, to repo ke andar **Add file → Upload files** button dhoondein.)
2. Is poore `QuranUnlock` folder ko **zip karke** apne phone mein rakhein (already zip hai jo maine di hai).
3. GitHub ka upload page sirf files/folders accept karta hai, zip ko pehle **extract** karna hoga phone mein
   (koi bhi file manager app se "Extract" kar dein), phir extracted `QuranUnlock` folder ke andar ki **saari files aur folders** select karke upload karein.
   - Zaroori: `.github` folder (jisme workflow hai) bhi upload honi chahiye — ye hidden folder ho sakta hai,
     file manager mein "show hidden files" on karein.
4. Neeche commit message likh dein (e.g. "Initial upload") aur **Commit changes** par tap karein.

## Step 4: Build Apne Aap Shuru Ho Jayega
1. Upload hote hi GitHub Actions apne aap trigger ho jayega (kyunke workflow `push` par chalta hai).
2. Repo ke upar **Actions** tab par tap karein.
3. Wahan "Build APK" naam ka workflow run dikhega, chal raha hoga (yellow dot) — 2-5 minute lagte hain.
4. Jab green tick ho jaye, us run par tap karein.

## Step 5: APK Download Karein
1. Run ke page ke neeche **Artifacts** section mein `QuranUnlock-debug-apk` dikhega.
2. Us par tap karein — ek `.zip` download hoga jisme APK hoga.
3. Zip ko extract karein, andar `app-debug.apk` milega.
4. Us APK ko apne phone mein install kar lein (Settings mein "install from unknown sources" allow karna pare ga).

## Agar Build Fail Ho Jaye
1. Actions tab mein failed (red cross) run par tap karein.
2. "build" job kholein, jahan error laal rang mein dikhega.
3. Wo error message mujhe yahan bhej dein, main fix kar dunga.

## Baad Mein Update Karna Ho To
Jab bhi code mein koi tabdeeli karke dobara upload/commit karenge, ye workflow apne aap phir se chalega
aur naya APK bana dega — bar bar manually kuch karne ki zaroorat nahi.
