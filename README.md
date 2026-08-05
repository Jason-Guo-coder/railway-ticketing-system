# Railway Ticketing System

## Directory Structure

```text
backend/   Spring Boot and Spring Cloud services
frontend/
├── web/    Vue 3 and Vite web application
└── admin/  Vue 3 and Vite administration console
```

## Backend

```bash
cd backend
mvn test
```

Backend environment variables:

- `DB_PASSWORD`: Member and Business database password.
- `JWT_SECRET`: JWT signing secret. Member and Gateway must use the same value.
- `ADMIN_USERNAME`: Admin console username. Defaults to `admin` for learning.
- `ADMIN_PASSWORD`: Admin console password. Defaults to `admin123` for learning.

## Frontend

```bash
cd frontend/web
npm install
npm run dev
```

The frontend development server runs at `http://127.0.0.1:9000` and proxies
`/member/**` requests to Gateway at `http://127.0.0.1:8000`.

## Admin

```bash
cd frontend/admin
npm install
npm run dev
```

The administration console runs at `http://127.0.0.1:9001`.
