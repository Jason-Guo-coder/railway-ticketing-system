# Railway Ticketing System

## Directory Structure

```text
backend/   Spring Boot and Spring Cloud services
frontend/  Vue 3 and Vite web application
```

## Backend

```bash
cd backend
mvn test
```

## Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend development server runs at `http://127.0.0.1:9000` and proxies
`/member/**` requests to Gateway at `http://127.0.0.1:8000`.
