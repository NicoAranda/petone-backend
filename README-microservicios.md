# PetOne — Backend (Microservicios)

Este documento describe cómo instalar, ejecutar y probar cada microservicio dentro del monorepo `petone-backend`.

Estructura principal:
- `ms-bff/` — Backend BFF (Node.js + Express).
- `ms-users/ms-users/` — Microservicio de usuarios (Java Spring Boot).
- `ms-mascotas/ms-mascotas/` — Microservicio de mascotas (Java Spring Boot).
- `ms-publication/` — Microservicio de publicaciones (Java Spring Boot).

## Requisitos generales
- Java 17 (o la versión definida en los `pom.xml`) para los microservicios Java.
- Maven (`mvn`) o usar los wrappers incluidos (`mvnw` / `mvnw.cmd`).
- Node.js 18+ y `npm` para `ms-bff`.

---

## ms-bff (BFF)

Ruta: `ms-bff/`

Instalación:

```bash
cd ms-bff
npm ci
```

Ejecución en desarrollo:

```bash
cd ms-bff
npm run dev   # si existe script dev, o
node index.js
```

Ejecutar tests unitarios (Jest):

```bash
cd ms-bff
npm test
```

Notas:
- Las pruebas usan mocks para `fetch`. Revisa `test/bffRoutes.test.js`.

---

## ms-users

Ruta: `ms-users/ms-users`

Instalación y compilación:

```powershell
cd ms-users/ms-users
./mvnw.cmd -q -DskipTests=true package
```

Ejecutar la aplicación localmente:

```powershell
cd ms-users/ms-users
./mvnw.cmd spring-boot:run
```

Ejecutar tests unitarios:

```powershell
cd ms-users/ms-users
./mvnw.cmd test
```

Notas:
- Configuración de la base de datos y `application.yml` está en `src/main/resources`.
- Si deseas ejecutar contra una base de datos real, actualiza las propiedades en `application.yml`.

---

## ms-mascotas

Ruta: `ms-mascotas/ms-mascotas`

Instalación y compilación:

```powershell
cd ms-mascotas/ms-mascotas
./mvnw.cmd -q -DskipTests=true package
```

Ejecutar localmente:

```powershell
cd ms-mascotas/ms-mascotas
./mvnw.cmd spring-boot:run
```

Ejecutar tests:

```powershell
cd ms-mascotas/ms-mascotas
./mvnw.cmd test
```

Notas:
- Este servicio contiene `SupabaseStorageService` que realiza requests HTTP; en tests unitarios se mockea normalmente.

---

## ms-publication

Ruta: `ms-publication/`

Instalación y compilación:

```powershell
cd ms-publication
./mvnw.cmd -q -DskipTests=true package
```

Ejecutar localmente:

```powershell
cd ms-publication
./mvnw.cmd spring-boot:run
```

Ejecutar tests:

```powershell
cd ms-publication
./mvnw.cmd test
```

Notas:
- `SupabaseStorageService` hace llamadas a Supabase; para pruebas locales los métodos son normalmente mockeados.

---

## Recomendaciones para desarrollo local
- Ejecuta cada microservicio en una terminal separada (puertos por defecto definidos en `application.yml` y `index.js`).
- Usa `VITE_BFF_URL` en el frontend para apuntar al BFF local.
- Para evitar llamadas externas en tests unitarios, los tests ya incluidos mockean dependencias (`fetch`, `MultipartFile`, `SupabaseStorageService`, etc.).

## Ejecutar todos los tests desde la raíz (opcional)

No hay script centralizado, pero puedes ejecutar manualmente per-folder:

```bash
# BFF
cd ms-bff && npm test

# ms-users
cd ms-users/ms-users && ./mvnw.cmd test

# ms-mascotas
cd ms-mascotas/ms-mascotas && ./mvnw.cmd test

# ms-publication
cd ms-publication && ./mvnw.cmd test
```

---

Si quieres, puedo crear scripts integrados o un `Makefile`/PowerShell script para orquestar el inicio y testeo de todos los servicios.

Archivo: [petone-backend/README-microservicios.md](README-microservicios.md)
