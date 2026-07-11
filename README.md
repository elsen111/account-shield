# Account Shield

A Spring Boot backend for user accounts done properly: registration, login, JWT + refresh tokens, brute-force lockout, email verification, profile management, and an admin panel to manage users. Built as a learning project to get hands dirty with Spring Security, JWT, and the everyday headaches of building auth from scratch.

If you've ever wondered what actually happens behind "sign up, verify your email, log in" — this repo is that, laid bare.

## What's inside

- **JWT authentication** — short-lived access tokens + long-lived, rotating refresh tokens
- **Login attempt protection** — too many wrong passwords and the account locks itself for a cooldown period
- **Email verification** — new accounts start unverified and can't log in until they confirm their email (simulated in dev, real SMTP in prod — more on that below)
- **Profile management** — view/update your own profile, change your password
- **Admin panel** — list users, look one up, change their role, activate/suspend/deactivate them
- **Swagger / OpenAPI docs** — every endpoint documented and testable from the browser

## Tech stack

| Layer | Tech |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.4 |
| Security | Spring Security + JWT (`jjwt`) |
| Database | PostgreSQL + Spring Data JPA / Hibernate |
| Mapping | MapStruct |
| Docs | springdoc-openapi (Swagger UI) |
| Build | Gradle |
| Mail | Spring Mail (simulated in dev, real SMTP in prod) |

## Getting started

### You'll need

- Java 21
- PostgreSQL running locally (or reachable somewhere)
- Gradle (the wrapper is included, so you don't need it installed separately)

### 1. Create the database

```sql
CREATE DATABASE account_shield_db;
```

Hibernate handles the tables for you (`ddl-auto: update`), so you don't need to write any schema by hand.

### 2. Set your environment variables

The app is happy with defaults for local dev, but a few things you'll want to set:

| Variable | What it's for | Default |
|---|---|---|
| `DB_PASSWORD` | Your Postgres password | `fallback_password` |
| `MAIL_HOST` / `MAIL_PORT` | SMTP server (only used in `prod` profile) | `smtp.gmail.com` / `587` |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | SMTP login (only used in `prod` profile) | empty |
| `MAIL_FROM` | The "from" address on outgoing emails | `no-reply@accountshield.com` |
| `VERIFICATION_BASE_URL` | The link base used in verification emails | `http://localhost:8080/api/auth/verify-email` |

### 3. Run it

```bash
./gradlew bootRun
```

By default (no profile set), the app runs in "dev mode" — verification emails aren't actually sent, they're just logged to your console so you can copy the token straight out of the terminal. See the [email verification](#email-verification-how-it-actually-works) section below for the full story, including how to switch on real emails.

The app comes up on `http://localhost:8080`.

## Swagger — try it without writing a single curl command

Once the app is running, open:

**http://localhost:8080/swagger-ui.html**

Everything's documented there — request bodies, response shapes, the works. You can register, grab a token, click "Authorize," paste it in, and hit the protected endpoints right from the browser. Honestly the easiest way to explore this project instead of reading the code.

## The API, at a glance

### Auth — `/api/auth`

| Method | Endpoint | What it does | Auth required? |
|---|---|---|---|
| `POST` | `/register` | Creates an account (starts unverified) | No |
| `POST` | `/login` | Authenticates, returns access + refresh tokens | No |
| `POST` | `/refresh` | Trades a refresh token for a new access token | No |
| `POST` | `/logout` | Revokes all refresh tokens for the current user | Yes |
| `POST` | `/verify-email` | Confirms an account using its verification token | No* |
| `POST` | `/resend-verification` | Issues a fresh verification token | No* |

> \* **Known issue:** these two are currently *not* whitelisted in `SecurityConfig`, so they'll get blocked by the JWT filter for a user who isn't logged in yet — which is exactly the user who needs them. Needs a one-line fix (`permitAll()` on these two paths) before the verification flow works end-to-end.

### Profile — `/api/profile` (requires login)

| Method | Endpoint | What it does |
|---|---|---|
| `GET` | `/me` | Get your own profile |
| `PUT` | `/me` | Update your profile |
| `PUT` | `/me/password` | Change your password |

### Admin — `/api/admin/users` (requires `ADMIN` role)

| Method | Endpoint | What it does |
|---|---|---|
| `GET` | `/` | List all users (paginated) |
| `GET` | `/{userId}` | Get one user |
| `PATCH` | `/{userId}/role` | Change a user's role |
| `PATCH` | `/{userId}/status` | Activate / suspend / deactivate a user |

## How the auth flow actually works

1. You **register** — the account is created with `status: PENDING_VERIFICATION`, not `ACTIVE`. It can't log in yet.
2. A verification token gets generated and "emailed" to you.
3. You hit **verify-email** with that token — the account flips to `ACTIVE`.
4. Now you can **log in**. You get back an access token (short-lived, goes in your `Authorization: Bearer ...` header) and a refresh token (long-lived, used to get new access tokens without logging in again).
5. Access token expires? Hit **refresh** with your refresh token — you get a new pair, and the old refresh token is revoked (rotation, not reuse).
6. Get your password wrong too many times in a row and the account **locks itself** for a cooldown period — no brute-forcing your way in.

## Email verification — how it actually works

There's no real mail server wired in by default, and you don't need one to try this project out.

- **Default (dev) mode** — when you register or resend a verification email, the app just **logs the email** to your console: who it's "to," and the verification link with the token baked in. Copy the link (or just the token) from your terminal and paste it into the verify-email request. That's it — no inbox needed.
- **Production mode** — run with `--spring.profiles.active=prod` and set the `MAIL_*` environment variables (see above) and it sends a real email over SMTP instead. Any provider works — Gmail (with an App Password, not your normal login), SendGrid, Mailgun, your company's SMTP relay, whatever you point it at.

Nothing else about the flow changes between the two modes — same tokens, same expiry, same verify endpoint. Only where the notification actually goes differs.

## Some notes

- `ddl-auto: update` is used for convenience — fine for a learning project, but it doesn't handle things like enum value changes gracefully (adding a new status value can require a manual `ALTER TABLE` on an existing database). A real project would use Flyway or Liquibase migrations instead.
- There's no rate limiting beyond the login-attempt lockout — someone could still hammer `/register` or `/resend-verification`.
- The verify-email/resend-verification security-whitelist gap mentioned above is a genuine bug, not a design choice.
