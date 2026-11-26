-- El administrador puede registrar clientes solo con el nombre: documento y correo pasan a admitir vacío (NULL).
-- Siguen siendo únicos (MySQL permite varios NULL en un índice único) y no se modifica ningún dato.
-- Hay que ejecutarlo en cada base (pruebas y producción) ANTES de publicar el código que lo usa.
ALTER TABLE cliente
    MODIFY documento VARCHAR(20) NULL,
    MODIFY email VARCHAR(100) NULL;
