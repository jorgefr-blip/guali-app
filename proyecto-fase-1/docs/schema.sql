-- Esquema de la base de datos "guali" (MariaDB 11.x)
-- Reconstruido a partir de las entidades JPA del proyecto (el proyecto usa ddl-auto=none:
-- Hibernate NO crea tablas, por eso el esquema se mantiene aqui).
-- Si ya tienes la base creada, compara con:  SHOW CREATE TABLE <tabla>;

CREATE DATABASE IF NOT EXISTS guali CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE guali;

CREATE TABLE IF NOT EXISTS leccion (
    id             BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    titulo         VARCHAR(255),                    -- nombre de la carpeta, ej. leccion-01-hola-mundo
    contenido_md   LONGTEXT,                        -- markdown de la leccion
    objetivo       VARCHAR(255),                    -- COMPILAR_CON_ERROR | COMPILAR_EXITOSO | EJECUTAR_CON_ERROR | EJECUTAR_EXITOSO
    creada_en      DATETIME(6),
    actualizada_en DATETIME(6)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS usuario_admin (
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(255),
    nombre        VARCHAR(255),
    password_hash VARCHAR(100),                     -- BCrypt (60 caracteres)
    activo        BIT(1),
    creado_en     DATETIME(6),
    UNIQUE KEY uk_usuario_admin_username (username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS intento_historial (
    id               BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    client_id        VARCHAR(255),
    leccion_id       BIGINT,                        -- referencia logica a leccion.id
    leccion_titulo   VARCHAR(255),
    objetivo         VARCHAR(255),
    estado           VARCHAR(255),                  -- PENDIENTE | COMPLETADA
    codigo_compilado LONGTEXT,
    fecha_inicio     DATETIME(6),
    fecha_final      DATETIME(6),
    ultima_actividad DATETIME(6)
) ENGINE=InnoDB;

-- Administrador inicial: la app convierte a BCrypt al arrancar cualquier contrasena en texto plano
-- (clase MigradorContrasenas), asi que basta con insertar:
-- INSERT INTO usuario_admin (username, nombre, password_hash, activo, creado_en)
-- VALUES ('admin', 'Administrador', 'admin123', 1, NOW());
