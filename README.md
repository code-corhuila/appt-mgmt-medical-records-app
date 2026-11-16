# Salud Activa — Medical Records App

Aplicación Android independiente para consultar y administrar los estudios médicos del usuario
autenticado en Salud Activa. Este repositorio implementa la interfaz móvil del bounded context
`medical-records`.

## Funcionalidad

- Inicio de sesión contra `POST /api/auth/login` y almacenamiento cifrado del JWT.
- Consulta de estudios propios mediante `GET /api/estudios`.
- Búsqueda por nombre o tipo, actualización manual y estado vacío.
- Registro con nombre, tipo, fecha, resultado y enlace opcional al documento.
- Eliminación confirmada de estudios.
- Los enlaces de documentos deben ser URLs HTTP(S) accesibles por el dispositivo; esta app no sube
  archivos locales.

El backend aplica el alcance por usuario desde el JWT. La app no permite enviar un `usuarioId`
desde el cliente.

## Requisitos y ejecución

- Android Studio con Android SDK 34.
- JDK 17.
- Emulador o dispositivo Android 7.0 (API 24) o superior.

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

La URL predeterminada de la API es
`https://saludactiva-backend.onrender.com/`; puede cambiarse con la propiedad Gradle
`API_BASE_URL`, por ejemplo `-PAPI_BASE_URL=https://api.example.com/` tras adaptar la configuración
del build.

## API utilizada

| Operación | Método y ruta |
|---|---|
| Iniciar sesión | `POST /api/auth/login` |
| Listar estudios del usuario | `GET /api/estudios` |
| Crear estudio | `POST /api/estudios` |
| Eliminar estudio | `DELETE /api/estudios/{id}` |

Todas las rutas de estudios requieren `Authorization: Bearer <JWT>`.

## Flujo del repositorio

Los cambios entran por ramas de historia `hu-xxx-dev` y Pull Request a `develop`; la promoción a
`qa` y luego `main` también se realiza mediante Pull Requests. No se deben modificar directamente
las ramas protegidas. La gobernanza del curso está en
[`library-docs`](https://github.com/code-corhuila/library-docs).

## Branching

Three permanent branches. **None of them accepts a direct commit** — you enter through a child
branch and leave through a Pull Request.

```
develop  <--PR--  feat/... fix/... chore/...
qa       <--PR--  qa/...
main     <--PR--  release/...  hotfix/...
```

Promotion happens **by re-application** (`git cherry-pick -x`), never by merging one permanent
branch into another: `merge develop -> qa` and `merge qa -> main` do not exist in this model.

`main` requires **1 approval from `ariel5253`**. On `develop` and `qa` the team sets its own review
rule.

Full policy: `00-governance/branching-policy.md` in `library-docs`.
