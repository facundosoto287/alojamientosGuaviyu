-- El administrador puede fijar a mano el precio total de una reserva (por ejemplo, para clientes de años).
-- NULL = no se fijó ninguno y se usa el precio calculado. No se modifica ningún dato.
-- Hay que ejecutarlo en cada base (pruebas y producción) ANTES de publicar el código que lo usa.
ALTER TABLE reserva
    ADD COLUMN precio_acordado DECIMAL(10,2) NULL;
