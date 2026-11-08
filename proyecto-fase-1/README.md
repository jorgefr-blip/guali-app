# guali — Fase 1

Aplicación web para práctica de programación introductoria en Java
(proyecto de curso Desarrollo Web, UMG).

## Cómo ejecutar

Requisitos: JDK 17+ y Maven (o el wrapper `./mvnw` si lo agregas).

```bash
mvn spring-boot:run
```

Luego abre `http://localhost:8080` en el navegador.

Como en esta fase no hay backend de lógica (solo se sirven archivos
estáticos), también puedes abrir `src/main/resources/static/index.html`
con cualquier servidor estático simple (por ejemplo `npx serve` o la
extensión "Live Server"), siempre que sirvas la carpeta completa (para
que `fetch()` pueda leer `lecciones/manifest.json` — abrir el HTML
directamente con `file://` no funciona por restricciones CORS del
navegador).

## Estructura del proyecto

```
proyecto-fase-1/
├── pom.xml
├── src/main/java/com/umg/guali/
│   └── GualiApplication.java        ← arranca el servidor, sin controladores aún
└── src/main/resources/
    ├── application.properties
    └── static/                      ← todo el frontend vive aquí
        ├── index.html               ← estructura de las 4 áreas de la interfaz
        ├── css/style.css            ← estilos, temas claro/oscuro
        ├── js/app.js                ← toda la lógica de esta fase
        └── lecciones/
            ├── manifest.json        ← índice de lecciones (ver nota abajo)
            ├── leccion-01-hola-mundo/
            │   ├── leccion.md
            │   └── config.txt
            └── leccion-02-errores-compilacion/
                ├── leccion.md
                └── config.txt
```

## Decisiones de arquitectura (Fase 1)

- **Sin frameworks de frontend.** Se implementó con HTML, CSS y
  JavaScript puro (sin React/Vue/jQuery), tal como se definió con el
  catedrático. Esto también facilita migrar la lógica de UI a Fase 2
  sin reescritura.

- **Spring Boot solo como servidor de archivos estáticos por ahora.**
  El proyecto ya está inicializado como una app Spring Boot (Maven,
  `spring-boot-starter-web`) para que en Fase 2 solo se agreguen
  controladores REST, JPA/MariaDB y Security, sin reestructurar el
  proyecto ni el frontend.

- **`lecciones/manifest.json`.** El navegador no puede listar el
  contenido de una carpeta por sí solo (no existe una API JS para
  "listar archivos" de una ruta servida por HTTP). Se agregó este
  archivo índice, no contemplado explícitamente en el enunciado, como
  solución pragmática para que el menú "Lecciones" sepa qué carpetas
  existen. En Fase 2 este archivo se reemplaza por un endpoint REST
  que consulte la base de datos.

- **Configuración solo en memoria.** El tema y el tamaño de fuente se
  guardan en un objeto `estado` de JavaScript y se pierden al recargar
  la página, tal como exige el alcance de esta fase.

- **Botones Compilar/Ejecutar/Historial deshabilitados.** Están
  presentes visualmente (requisito de "interfaz gráfica general") pero
  con `disabled` y un `title` explicando que se activan en Fase 2,
  para que la interfaz no prometa una función que aún no existe.

- **Parser de Markdown propio y minimalista.** En vez de agregar una
  librería externa (que rompería el requisito de funcionar offline sin
  dependencias externas de red), se escribió un parser pequeño en
  `app.js` que cubre lo que usan las lecciones de ejemplo: encabezados,
  negrita/cursiva, código en línea y en bloque, listas, citas e
  imágenes. No es un parser completo de CommonMark.

## Pendiente para Fase 2 (no incluido a propósito)

- Persistencia en MariaDB (lecciones, historial, usuarios administradores).
- Integración real con `guali-dev.jar` para Compilar/Ejecutar.
- Panel de Historial de Sesión.
- Autenticación/autorización de Administrador + Spring Security.
- Módulos CRUD de administración y estadísticas de uso.
- Importar/exportar compendios de lecciones (zip).
