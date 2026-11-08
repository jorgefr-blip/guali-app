# Cambios: login y redirecciones

| Archivo | Qué cambió |
|---|---|
| `SecurityConfig.java` | BCrypt en vez de NoOp; CSRF activo; `/admin/**` exige rol ADMIN; login vuelve a la página protegida que se pidió; `?error` explícito; logout invalida sesión y borra cookie; usuario con `activo=false` no entra |
| `MigradorContrasenas.java` (nuevo) | Al arrancar convierte a BCrypt las contraseñas en texto plano de `usuario_admin` (ej. `admin123`) |
| `LoginController.java` | Si ya hay sesión, `/login` redirige a `/admin/panel` |
| `AdminController.java` | `/admin` redirige a `/admin/panel`; quita la validación manual (la hace Security) |
| `AdminActualAdvice.java` (nuevo) | Variable `adminActual` para las vistas |
| `admin_panel.html` | Cerrar sesión por POST con token CSRF; enlace a lecciones; muestra el usuario |
| `lecciones.html`, `historial.html` | Enlace "Panel admin" solo si hay admin con sesión |
| `HistorialController.java` | Ordena por id descendente (el comentario lo decía, pero `findAll()` no ordenaba) |
| `application.properties` | Tu conexión intacta; agrega ajustes de sesión y no-cache |

Flujo: `/admin` o `/admin/panel` sin sesión -> `/login` -> credenciales ok -> `/admin/panel`.
Logout -> `/lecciones`. `/login` con sesión iniciada -> `/admin/panel`.
