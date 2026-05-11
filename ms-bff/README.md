ms-bff — Backend For Frontend (BFF) for Pet-One

Quick start

1. From the project root run:

```bash
cd petone-backend/ms-bff
npm install
```

2. Copy `.env.example` to `.env` and adjust if needed.

3. Run the service:

```bash
npm start
# or for development with auto-reload
npm run dev
```

Endpoints
- `GET /bff/publicaciones` — devuelve listado de publicaciones (proxy a ms-publication)
- `GET /bff/publicaciones/reportadas` — devuelve las publicaciones marcadas como reportadas
- `GET /bff/publicaciones/:id` — obtiene detalle de publicación
- `PATCH /bff/publicaciones/:id/reportar` — marca publicación como reportada (reenvía al servicio de publicaciones)
- `GET /health` — healthcheck del servicio

Notes
- The BFF forwards `Authorization` header si está presente.
- Default ms-publication URL: `http://localhost:8080` (override via `PUBLICATION_SERVICE`).
