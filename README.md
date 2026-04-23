# Control Asistencias Backend

Backend Spring Boot preparado para desplegarse en Railway y consumirse desde el frontend:

`https://lustrous-tiramisu-0825a4.netlify.app`

## Variables de entorno para Railway

Configura estas variables en el servicio del backend:

```env
PORT=8080
SPRING_DATASOURCE_URL=jdbc:mysql://<host>:<port>/<database>?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=<usuario>
SPRING_DATASOURCE_PASSWORD=<password>
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
APP_CORS_ALLOWED_ORIGINS=https://lustrous-tiramisu-0825a4.netlify.app,http://localhost:3000
APP_JWT_SECRET=<clave-larga-de-al-menos-32-caracteres>
APP_JWT_EXPIRATION_MS=86400000
```

## Recomendaciones

- Usa una clave JWT larga y privada. No reutilices la de desarrollo.
- Si tu esquema de base de datos aun cambia mucho, puedes usar `SPRING_JPA_HIBERNATE_DDL_AUTO=update` temporalmente. En produccion estable, vuelve a `validate`.
- Asegurate de que el frontend apunte a la URL publica de Railway para las llamadas API.

## Ejecucion local

```powershell
$env:APP_CORS_ALLOWED_ORIGINS="https://astonishing-kashata-259775.netlify.app/"
$env:APP_JWT_SECRET="una-clave-local-larga-y-segura-de-al-menos-32-bytes"
./mvnw.cmd spring-boot:run
```

## Cambios aplicados

- CORS configurable por variable de entorno.
- JWT persistente entre reinicios mediante `APP_JWT_SECRET`.
- Se desactivo la creacion automatica del usuario temporal de Spring Security.
- `spring.jpa.open-in-view` desactivado para un runtime mas limpio en produccion.
