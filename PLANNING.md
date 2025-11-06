## Job Application Tracker — Delivery Plan

### Executive Summary
- Most logical, simple, high-ROI task before frontend integration: **API Documentation with Swagger/OpenAPI**.
- Rationale: Instant visibility of endpoints and payloads, easier contract alignment with the frontend, enables rapid manual testing and onboarding, minimal code risk.
- Secondary quick wins: **Add Logging** and **CORS/auth hardening**.

---

## Prioritized Roadmap

### Phase 0 — Preconditions for Frontend Integration (Do now)
1. Enable Swagger/OpenAPI UI
   - Add `springdoc-openapi-starter-webmvc-ui`.
   - Expose `/swagger-ui.html` and `/v3/api-docs`.
   - Annotate controllers & DTOs with examples and clear response codes.
2. Add structured logging (application-wide)
   - Use SLF4J at service/controller boundaries; log request ids, auth user, key transitions.
   - Configure log levels per package; avoid logging secrets/JWTs.
3. Stabilize authentication & CORS
   - Confirm JWT flow (login → access token, refresh if applicable).
   - Configure CORS for dev (`http://localhost:5173` or `3000`).
   - Add 401/403 consistent error shapes.
4. Standardize response contracts
   - DTOs for list/detail; include pagination envelope.
   - Validation with clear error schema (field, message, code).
5. Seed data and deterministic fixtures
   - Provide sample users and 15–30 applications across statuses for demo & charts.

### Phase 1 — Minimal Backend Surface for MVP UI (Next)
1. CRUD ready for all entities essential to applications (companies, positions, notes, tags as applicable).
2. List & pagination for applications
   - Sort: date applied, company, status.
   - Basic filters: status, date range.
3. Basic stats endpoint for dashboard (lightweight)
   - Totals by status, last 30 days submissions, average salary if present.

### Phase 2 — Quality-of-Life and Observability
1. Request/response logging middleware (sampling in prod).
2. Error handling: global exception mapper → consistent problem+json-like payload.
3. Health checks & actuator (`/actuator/health`, metrics optional).

### Phase 3 — Enhanced Features (Later versions)
- Advanced Search & Filtering (full-text `keyword`, multi-field ranges, sorting presets).
- Rich Dashboard Statistics (weekly trends, conversion funnels, time-to-response, charts-ready series).
- Account Verification (email, token expiry, re-send flow).
- Role-based access control (admin/user), audit trails.
- Notifications (email/webhook), scheduled reminders.
- Performance: caching, pagination optimization, N+1 query review, indexes.

---

## Decision: What to do first (from your list)
1) Create services/controllers for remaining entities — useful but medium effort and domain-specific decisions needed.
2) Dashboard Statistics — valuable but depends on stable data model; medium complexity.
3) Search and Filtering — highest complexity; best deferred until contracts stabilize.
4) Account Verification — non-trivial (email infra, tokens, flows); can wait.
5) Add Logging — quick win; do right after docs.
6) API Documentation with Swagger/OpenAPI —
   - Winner for first task. Small, self-contained, unlocks rapid API exploration, consumer alignment, and testability.

Recommended immediate order:
1. Swagger/OpenAPI UI
2. Logging
3. CORS/auth hardening + error contract
4. Minimal CRUD endpoints completion
5. Basic dashboard stats
6. Advanced search
7. Email verification

---

## Backend Acceptance Checklist for Frontend Consumption
- Swagger UI loads locally and in dev env; endpoints have clear schemas and examples.
- All application endpoints return DTOs, not entities; stable field names.
- Pagination envelope: `{ data: T[], page, size, total }`.
- Errors standardized: `{ timestamp, path, status, error, message, details[] }`.
- CORS allows dev origin; credentials configured if cookies used.
- JWT auth tested with Postman/Swagger authorize.
- Seed script provides demo data.

---

## React Frontend Build Plan

### Tech Stack
- Tooling: Vite + React + TypeScript
- State/data: React Query (or RTK Query) for API caching
- Forms: React Hook Form + Zod for schema validation
- UI: MUI or Ant Design
- Charts: Recharts (or Chart.js)
- Routing: React Router v6

### Project Structure
```
src/
  app/
    queryClient.ts
    router.tsx
    store.ts (optional if using RTK)
  components/
    layout/, nav/, charts/
  features/
    auth/
      api.ts, hooks.ts, LoginPage.tsx, RegisterPage.tsx
    applications/
      api.ts, types.ts, hooks.ts
      ApplicationsPage.tsx, ApplicationForm.tsx, ApplicationDetail.tsx
    dashboard/
      api.ts, DashboardPage.tsx, widgets/
  lib/
    axios.ts (base client with interceptors)
  pages/
    NotFound.tsx
  index.tsx, main.tsx
```

### Authentication Strategy
- Short term (per your plan): store JWT access token in `localStorage`.
  - Interceptor attaches `Authorization: Bearer <token>`.
  - On 401, redirect to login; clear token.
- Recommended longer term: httpOnly cookies + refresh token to mitigate XSS.

### API Client
- Create a single Axios instance with baseURL (e.g., `http://localhost:8080`).
- Request interceptor injects token if present.
- Response interceptor normalizes errors to a single shape.

### Pages for MVP
1. Login/Register
2. Dashboard (cards: total apps, by status; small chart of last 30 days)
3. Applications List
   - Table with pagination, status chips, search by company/title (client-side first)
4. Create/Edit Application Form

### Data Hooks (React Query)
- `useLogin`, `useRegister`, `useMe` (optional)
- `useApplications({ page, size, status, q })`
- `useCreateApplication`, `useUpdateApplication`, `useDeleteApplication`
- `useDashboardStats()`

### Routing and Guards
- Public routes: `/login`, `/register`
- Protected routes: `/`, `/applications`, `/applications/:id`, `/applications/new`
- Guard reads token from `localStorage`.

### UI/UX Guidelines
- Consistent empty/loading/error states.
- Form validation with Zod schemas mirroring backend constraints.
- Accessible components (labels, aria, keyboard navigation).

### Dev/Build Workflow
- Create app: `npm create vite@latest job-tracker-frontend -- --template react-ts`
- Install deps: `npm i axios @tanstack/react-query react-hook-form zod @mui/material @emotion/react @emotion/styled recharts react-router-dom`
- Setup `.env.development` with `VITE_API_BASE_URL=http://localhost:8080`
- Run dev: `npm run dev`
- Build: `npm run build`; Preview: `npm run preview`

### Integration Contract Checklist
- Swagger matches TypeScript types (`types.ts`) — consider `openapi-typescript` to generate.
- Confirm auth header name and 401 behavior.
- Verify CORS and preflight success from browser.
- Timeouts/retries policy on API client.

---

## Test Plan
- Backend: unit tests for services; slice tests for repositories; MVC tests for controllers (auth, error shapes).
- Frontend: component tests for forms and list; integration tests for login flow; mocked API with MSW.

---

## Risks and Mitigations
- Scope creep on search/stats → timebox and defer advanced features.
- Inconsistent error shapes → enforce global exception handler early.
- Auth storage risks (XSS) → plan migration to httpOnly cookies later.

---

## Definition of Done (MVP)
- Swagger documented endpoints; Postman collection exported.
- Frontend can authenticate, list applications, create/edit an application, and view basic dashboard stats.
- CI builds both apps; lints/tests pass; seed data available.


