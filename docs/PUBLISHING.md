# Sharing this with the world

How to put the app on someone else's tablet, and how to publish it so other
people can find it. Written to be followed step by step.

Nothing here is done for you: the parts that involve passwords, accounts and
irreversible choices should be yours.

---

## 1. The one thing you cannot undo: the signing key

Every Android app is **signed**. The signature is how Android knows that an
update to an app came from the same person who made the original. It is not a
formality; it is the whole trust model.

Right now the app is signed with a **debug key**, which Android Studio
generates automatically. That is fine for testing on your own phone, and it is
what has been installed so far. It is not fine for sharing:

- it is the same well-known key on every machine, so it proves nothing;
- Google Play will refuse it;
- if you later switch keys, existing users cannot update — they must uninstall
  first, **which erases the words the app has learned about them**.

So before you share anything, you make a **release key**, once, and keep it
forever.

### Making it

```
keytool -genkeypair -v \
  -keystore comunicador-release.jks \
  -alias comunicador \
  -keyalg RSA -keysize 4096 -validity 10000
```

It asks for a password and some details. **Write the password down somewhere
real.** Then:

- **Back the `.jks` file up in at least two places** that are not this laptop.
- **Never commit it to git.** `.gitignore` already excludes `*.jks` and
  `keystore.properties`.
- Losing it means you can never update the app for anyone who installed it.
  That is the single most expensive mistake available to you here.

### Using it

Create `keystore.properties` at the project root — **not committed**:

```
storeFile=/absolute/path/to/comunicador-release.jks
storePassword=...
keyAlias=comunicador
keyPassword=...
```

The build reads it if present and signs the release build with it; if it is
absent, the release build is simply unsigned, so a fresh clone still compiles.

Then:

```
./gradlew assembleRelease
```

The result is `app/build/outputs/apk/release/app-release.apk`.

Check it really is signed by you:

```
$ANDROID_HOME/build-tools/36.0.0/apksigner verify --print-certs \
  app/build/outputs/apk/release/app-release.apk
```

It should name your certificate, not "Android Debug".

---

## 2. Putting it on someone else's tablet

No cable, no developer tools, no Play Store needed.

1. Send them `app-release.apk` — email, a link, a USB stick, anything.
2. On the tablet, tap the file.
3. Android asks whether to allow installing apps from that source (the browser,
   or Files, or Gmail). They allow it, once, for that app.
4. Install.

If the tablet is **managed by an organisation**, this can be blocked by policy
and there is nothing on the device that will get around it. See the note about
managed devices in the project history: that route needs the organisation.

### Updating someone later

Send a newer APK signed **with the same key** and they tap it. It installs over
the top, keeping their settings, phrases and learned words.

For that to work, **`versionCode` in `app/build.gradle.kts` must go up** with
every release you hand out. `versionName` is the human label ("0.3"); the
`versionCode` is the integer Android actually compares. If it does not
increase, the install is refused as a downgrade.

---

## 3. Where to publish

In rough order of effort.

### GitHub Releases — start here

Free, immediate, and the natural home for an open project.

1. Make the repository public (Settings → General → Danger Zone → Change
   visibility). Check the history first for anything private: this project has
   held real phrases and a real person's name.
2. Tag a version and attach the APK:

```
git tag v0.3
git push origin v0.3
```

Then on GitHub: Releases → Draft a new release → pick the tag → attach
`app-release.apk` → publish.

People download and install it as in section 2. Write clearly on the release
page what the app is and who it is for.

### F-Droid — the natural fit later

An app store for open-source software, popular with people who avoid Google
accounts, and free. It **builds your app itself from your source**, which means
no key handling on your side, and it is a good fit for an accessibility tool
funded by nobody. It expects a clean licence and reproducible builds, and
submission takes some back and forth.

### Google Play — widest reach, most friction

- One-off developer fee (about €25) and an account.
- New apps are submitted as an **AAB** (`./gradlew bundleRelease`), not an APK.
- Requires a privacy policy, a store listing, screenshots, content rating, and
  review. Expect rejections and iteration.
- Play can manage the signing key for you, which protects against losing it.

Worth doing if you want families to find it by searching. Not worth doing to
get it onto one tablet.

---

## 4. Licence and credit: read this before making the repo public

The app is not only your code. It carries other people's work, and each piece
comes with obligations.

| What | Where from | Obligation |
| --- | --- | --- |
| Atkinson Hyperlegible font | Braille Institute, SIL Open Font License | Keep `OFL-AtkinsonHyperlegible.txt` alongside it. Already done. |
| `ca/es/en-model.txt` | Built from OpenSubtitles corpora via OPUS | Credit the source. Check the licence terms before redistributing. |

The models are **derived from a corpus that is not yours**, and the usual terms
for that data (CC BY-SA on the frequency lists) ask for attribution and can ask
that derived work be shared alike. That may constrain which licence you can put
on the project. This is not legal advice; it is a flag that the question is
real and worth ten minutes before you publish, not after.

**This is now done.** The project is GPL-3.0 (see `LICENSE`), and every piece of
other people's work it carries is credited in `THIRD-PARTY-NOTICES.md`, with
what each licence asks of you. Read that file once before you publish, so you
know what you are agreeing to pass on.

---

## 5. Before you tell anyone it exists

- [ ] A `README` saying what it is, who it is for, and how to install it.
- [x] A `LICENSE` file. Done: GPL-3.0.
- [x] Credit for the font and the corpora. Done: `THIRD-PARTY-NOTICES.md`.
- [ ] A release build that you have **actually installed and used**, not just
      compiled. A release build differs from a debug build, and it is the first
      time some things are exercised.
- [ ] `versionCode` bumped.
- [ ] The signing key backed up somewhere that is not this laptop.
- [ ] A think about the package name (see below).

### The package name

The app's permanent identity is `cat.merce.comunicador`. Two things follow:

- It contains a real person's name, and it is visible to anyone who installs it.
- It cannot be changed later without the result being, to Android, **a
  different app**: no updates, and existing users start from nothing.

Decided, 2026-08-17: **her name stays.** It is the name of the person the app
was made for, and that is worth keeping. Noted here so the choice is on the
record rather than an accident, and so anyone who later wants a neutral name
knows it means a new app rather than a rename.
