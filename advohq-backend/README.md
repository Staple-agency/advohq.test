# AdvoHQ Backend

REST API for the AdvoHQ case & brief management app — **Java 17 + Spring Boot 3**, with
**PostgreSQL** for working data and **AWS S3** for client-provided documents.

It backs every feature of the front-end prototype (which currently uses `localStorage`):
users/auth, cases, important dates, the hearing calendar, document storage, profile,
settings toggles, and custom case stages.

---

## Stack

| Concern            | Choice                                             |
|--------------------|----------------------------------------------------|
| Language / runtime | Java 17                                            |
| Framework          | Spring Boot 3.3 (Web, Data JPA, Security, Validation) |
| Database (SQL)     | PostgreSQL, schema managed by **Flyway**           |
| Document storage   | **AWS S3** (private objects + pre-signed URLs)     |
| Auth               | Stateless **JWT** (BCrypt-hashed passwords)        |
| Build              | Maven                                              |

---

## Prerequisites

- **JDK 17+** — `brew install openjdk@17` (this machine had no JDK; install one to build)
- **Maven 3.9+** — `brew install maven`
- **PostgreSQL 13+** running locally (or via Docker)
- An **AWS account + S3 bucket** (or LocalStack/MinIO for offline dev)

---

## 1. Database

```bash
# quickest: Docker
docker run --name advohq-pg -e POSTGRES_DB=advohq \
  -e POSTGRES_USER=advohq -e POSTGRES_PASSWORD=advohq \
  -p 5432:5432 -d postgres:16
```

Flyway creates all tables automatically on first start (`src/main/resources/db/migration/V1__init.sql`).

## 2. Configure

```bash
cp .env.example .env        # then edit values
# generate a strong JWT secret:
openssl rand -base64 32
```

Everything is environment-driven (see `application.yml`). Key vars:
`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `AWS_REGION`, `AWS_S3_BUCKET`,
`CORS_ALLOWED_ORIGINS`.

AWS credentials are resolved by the default SDK chain (env vars, `~/.aws/credentials`,
or an IAM role) — **never hard-coded**.

## 3. Run

```bash
set -a && source .env && set +a      # load env into the shell
mvn spring-boot:run                  # or: mvn clean package && java -jar target/advohq-backend-1.0.0.jar
```

API base: `http://localhost:8080` · health check: `GET /actuator/health`

> **Note:** the code was written but not compiled in this environment (no JDK/Maven was
> installed here). Run `mvn clean package` once to compile; if JPA's `ddl-auto: validate`
> ever complains about a column type on your Postgres version, switch it to `none` in
> `application.yml` — Flyway already owns the schema.

---

## API reference

All routes except `/api/auth/**` and `/actuator/health` require
`Authorization: Bearer <token>`.

### Auth
| Method | Path                 | Body                                            |
|--------|----------------------|-------------------------------------------------|
| POST   | `/api/auth/register` | `{username,password,fullName,phone?,email?}`    |
| POST   | `/api/auth/login`    | `{username,password}` → `{token,user,...}`      |

### Cases
| Method | Path              | Notes                          |
|--------|-------------------|--------------------------------|
| GET    | `/api/cases`      | list (newest first)            |
| POST   | `/api/cases`      | `{title,stage?,points?,importantDates?}` |
| GET    | `/api/cases/{id}` | one case + its important dates |
| PUT    | `/api/cases/{id}` | full update (dates re-synced)  |
| DELETE | `/api/cases/{id}` |                                |

### Schedule events
| Method | Path               | Notes                                          |
|--------|--------------------|------------------------------------------------|
| GET    | `/api/events`      | `?from=YYYY-MM-DD&to=YYYY-MM-DD` (optional)     |
| POST   | `/api/events`      | `{type,caseName,date,caseNo?,location?,hall?,notes?}` |
| PUT    | `/api/events/{id}` |                                                |
| DELETE | `/api/events/{id}` |                                                |

### Documents (AWS S3)
| Method | Path                              | Notes                                  |
|--------|-----------------------------------|----------------------------------------|
| GET    | `/api/documents`                  | `?caseId=` (optional)                  |
| POST   | `/api/documents`                  | multipart `file` (+ optional `caseId`) |
| GET    | `/api/documents/{id}/download-url`| short-lived pre-signed S3 URL          |
| DELETE | `/api/documents/{id}`             | removes from S3 **and** DB             |

### Large uploads (browser → S3)
Files over ~40 MB can't come through the API: Cloudflare caps a proxied request body at
100 MB and the instance has little memory. The browser uploads them to S3 in 16 MB parts
using signed URLs, up to `MAX_DIRECT_UPLOAD_BYTES` (default 1 GB).

| Method | Path | Notes |
|--------|------|-------|
| POST | `/api/documents/direct/init` | `{fileName, contentType, sizeBytes, caseId}` → upload id, part size/count, first signed URLs. Checks case ownership, type allowlist and plan storage first. |
| GET | `/api/documents/direct/{id}/urls?from=&to=` | more signed part URLs (batches of 50, 2 h validity) |
| POST | `/api/documents/direct/{id}/complete` | `{parts:[{partNumber, etag}]}` → assembles the object, **re-reads its real size from S3**, re-checks the plan, then creates the document |
| DELETE | `/api/documents/direct/{id}` | abort: discards the parts |

- The browser never supplies the S3 key: it comes from `DocumentService.newObjectKey` and is
  stored in `pending_uploads` (V14), looked up by upload id **and** owner.
- The declared size only rejects a file early; the size charged against the plan is the one
  S3 reports. Over-limit objects are deleted rather than kept.
- Abandoned uploads expire after 12 h and are aborted on the next `init`. Add an S3 lifecycle
  rule ("delete incomplete multipart uploads after 7 days") as a second line of defence.
- **The bucket needs a CORS rule** allowing PUT from the site origin and exposing `ETag`,
  or the browser can't upload parts.
- OneDrive imports stream Graph → S3 through the backend in 16 MB parts, as a background
  job (V15 `import_jobs`), capped by `ONEDRIVE_MAX_IMPORT_BYTES` (default 1 GB).

### OCR for scans (Sarvam Document AI)
A scanned PDF or a photo has no text layer, so Advo AI has nothing to read. Such files are
digitised once by Sarvam and the text is stored in `document_ocr` (V16) — OCR is billed per
page, so a document is never read twice.

| Method | Path | Notes |
|--------|------|-------|
| POST | `/api/documents/{id}/ocr` | `{language?, force?}` → **202** with the job row; returns the existing row if already read or running |
| GET | `/api/documents/{id}/ocr` | progress: `NONE \| QUEUED \| RUNNING \| DONE \| FAILED`, `pagesDone`/`pagesTotal`, `truncated` |
| GET | `/api/documents/{id}/ocr?text=true` | same, plus the extracted text |

- Sarvam limits: **10 pages per job** (PDFs are split by `PdfPageSplitter`) and **10 requests
  per minute per key**, shared by all users — so a single worker thread runs jobs one at a
  time, paces its calls, and retries 429s with backoff.
- `OCR_MAX_PAGES` (default 300) caps pages per document; beyond it the text is marked
  `truncated`. Files over 200 MB are refused (Sarvam's own limit).
- Languages: any of Sarvam's 23 (`en-IN` default). The editor offers a re-read in another
  language, which is the only case that re-bills a document.
- The frontend only calls this when a document has no usable text, so ordinary PDFs cost nothing.
- Needs `SARVAM_API_KEY` on the **backend** (the frontend has its own copy for translation).

### Plans & entitlements
Three monthly plans (`PlanCatalog`), plus a 14-day Premium-level free trial for every account
(`users.trial_ends_at`, V13):

| Plan | Price | Storage | AI |
|------|-------|---------|----|
| BASIC | ₹799 | 15 GB | none |
| PROFESSIONAL | ₹2,899 | 50 GB | Advo AI quick actions + risk analysis, Sarvam |
| PREMIUM | ₹3,599 | 200 GB | everything (free-form Advo AI, Document Intelligence) |

- `GET /api/subscriptions/entitlements` → `{tier, tierName, paidUntil, trialEndsAt, storageUsedBytes, storageLimitBytes, canUpload, aiPresets, aiFreeform, docIntel, sarvam}`.
- Storage (Trash included) is enforced in `DocumentService.store`/`replaceContent` and before OneDrive downloads; over-limit or no-plan is **402** with `code: STORAGE_FULL | PLAN_REQUIRED`.
- AI is enforced by the frontend server (`advohq-frontend/api/_lib/entitlements.js`), which forwards the user's JWT to this endpoint. Professional users may send only a preset id; the server supplies the wording.
- With no plan and no trial, files stay viewable/downloadable; uploads, edited saves and AI are refused.
- Switching plans: once the new subscription is charged, older live subscriptions are cancelled at Razorpay immediately (no proration).

### OneDrive import (Microsoft Graph)
OneDrive is an import **source** only: the chosen file is downloaded server-side and
stored in S3 as an ordinary document (same checks as an upload), so viewing it never
depends on OneDrive afterwards.

| Method | Path                          | Auth   | Notes |
|--------|-------------------------------|--------|-------|
| GET    | `/api/onedrive/status`        | JWT    | `{available, connected, accountName, accountEmail}` |
| POST   | `/api/onedrive/authorize`     | JWT    | → `{loginUrl}` — one-time, 2-minute ticket URL for the browser to navigate to |
| GET    | `/api/onedrive/login?ticket=` | ticket | sets an HttpOnly nonce cookie, 302 → Microsoft sign-in (PKCE) |
| GET    | `/api/onedrive/callback`      | state + cookie | Microsoft's redirect; exchanges the code, 302 → `ONEDRIVE_FRONTEND_URL?onedrive=connected\|error` |
| GET    | `/api/onedrive/files`         | JWT    | `?folderId=&pageToken=` — one page of a folder (root by default) |
| GET    | `/api/onedrive/files/{id}`    | JWT    | one item's metadata |
| POST   | `/api/onedrive/import`        | JWT    | `{itemId, caseId}` → **202** with an import job (`ImportJobDto`); the copy runs in the background |
| GET    | `/api/onedrive/imports/{id}`  | JWT    | job progress: `QUEUED \| RUNNING \| DONE \| FAILED`, `copiedBytes`/`totalBytes`, `documentId` on success |
| DELETE | `/api/onedrive/connection`    | JWT    | unlink (deletes stored Microsoft tokens; imported documents stay) |

- Scopes: `offline_access User.Read Files.Read` (delegated, the user's own files only).
- Microsoft tokens are AES-GCM encrypted at rest (`ONEDRIVE_TOKEN_ENCRYPTION_KEY`) and never sent to the browser.
- "Not connected / session expired" is a **409** with `code: ONEDRIVE_NOT_CONNECTED` — deliberately not 401/403, which the frontend treats as an AdvoHQ logout.
- Env: `MICROSOFT_CLIENT_ID`, `MICROSOFT_CLIENT_SECRET`, `MICROSOFT_TENANT_ID` (`common`),
  `MICROSOFT_REDIRECT_URI`, `ONEDRIVE_TOKEN_ENCRYPTION_KEY`, `ONEDRIVE_FRONTEND_URL`. Without the
  credentials or key, `status` reports `available: false` and everything else returns 503.

### Profile / settings / stages
| Method | Path               | Notes                              |
|--------|--------------------|------------------------------------|
| GET/PUT| `/api/me`          | profile                            |
| GET/PUT| `/api/settings`    | key/value toggles (`{key,value}`)  |
| GET/POST/DELETE | `/api/stages` `/api/stages/{id}` | custom case stages |

---

## Front-end integration

A ready-made client lives at `../advohq-frontend/api.js`. Add it to any page:

```html
<script>window.ADVOHQ_API_BASE = 'http://localhost:8080';</script>
<script src="api.js"></script>
```

Then replace the `localStorage` calls with API calls, e.g.:

```js
// login.html
await AdvoAPI.auth.login(username, password);   // stores the JWT for you
location.href = 'advohq-home.html';

// dashboard — load the user's cases from SQL instead of localStorage
const cases = await AdvoAPI.cases.list();

// upload a client document to S3
await AdvoAPI.documents.upload(fileInput.files[0], caseId);
const { url } = await AdvoAPI.documents.downloadUrl(docId);
window.open(url);                                // opens the pre-signed S3 link
```

`AdvoAPI` mirrors the existing data shapes (`importantDates`, `caseName`, `dateISO`, …),
so wiring it in is mostly swapping each `localStorage.getItem/setItem` for the matching
`AdvoAPI.*` call.

---

## Security notes
- Passwords hashed with BCrypt; tokens are signed JWTs (HMAC-SHA256).
- Every query is scoped to the authenticated user — no cross-tenant access.
- S3 objects are **private**; the browser only ever receives time-limited pre-signed URLs.
- Set a real `JWT_SECRET` and restrict `CORS_ALLOWED_ORIGINS` in production.
