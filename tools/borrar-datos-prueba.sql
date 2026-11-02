-- Borra SOLO los datos de prueba creados por tools/datos-prueba.sql (clientes PRUEBA-xxx y todo lo que cuelga de ellos).
-- No toca alojamientos ni clientes reales.
START TRANSACTION;

CREATE TEMPORARY TABLE tmp_reservas_prueba AS
  SELECT DISTINCT cr.idReserva AS id
  FROM cliente_reserva cr JOIN cliente c ON c.id = cr.idCliente
  WHERE c.email LIKE '%@prueba.test';

DELETE FROM resena WHERE cliente_id IN (SELECT id FROM cliente WHERE email LIKE '%@prueba.test');
DELETE FROM sena WHERE reserva_id IN (SELECT id FROM tmp_reservas_prueba);
DELETE FROM cliente_reserva WHERE idReserva IN (SELECT id FROM tmp_reservas_prueba);
DELETE FROM reserva WHERE id IN (SELECT id FROM tmp_reservas_prueba);
DELETE FROM cliente WHERE email LIKE '%@prueba.test';

DROP TEMPORARY TABLE tmp_reservas_prueba;
COMMIT;
