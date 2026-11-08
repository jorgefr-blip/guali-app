# Cambios sobre la versión final recibida

| Archivo | Cambio | Motivo |
|---|---|---|
| `SecurityConfig.java` | Todo `/admin/**` exige ROL ADMIN; CSRF activo salvo `/api/**`; sin caché de petición | Antes `/admin/lecciones/**` era público y `/admin/importar` y `/admin/exportar` quedaban abiertos (caían en `anyRequest().permitAll()`): un estudiante podía importar, exportar y eliminar lecciones. |
| `admin_lecciones.html` | El formulario de importar lleva el token CSRF en la URL | Un formulario multipart no expone `_csrf` al filtro de seguridad; sin esto, importar daría 403 con CSRF activo. |
| `CompilarController.java` | JDK desde `guali.jdk-home` (por defecto `java.home`) | Estaba fija a una ruta de tu PC; no funcionaba en otro equipo. |
| `form_leccion.html` (nuevo) | Formulario de nueva lección | El botón "+ Nueva Lección" daba error 500: la plantilla no existía. |
| `AdminLeccionController.java` | `guardar` valida campos y fija fechas | Soporte del formulario anterior. |
| `docs/schema.sql` (nuevo) | Esquema de la BD | Con `ddl-auto=none` el proyecto no se podía reproducir sin él. |
| `docs/Documentacion-Tecnica-Fase2.pdf` (nuevo) | Documentación técnica | Entregable de la Fase 2. |
