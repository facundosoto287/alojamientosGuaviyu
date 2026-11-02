-- DATOS DE PRUEBA de Alojamientos Guaviyú. NO crea alojamientos: usa los ids reales 1 a 8.
-- Todo queda marcado con documento 'PRUEBA-xxx' y correo '@prueba.test'. Para borrarlo: tools/borrar-datos-prueba.sql
-- Ejecutar con la conexión en UTF-8 (en HeidiSQL/Workbench ya es lo normal).
SET NAMES utf8mb4;
START TRANSACTION;

-- ===== 50 clientes (la contraseña es un valor inválido a propósito: no pueden iniciar sesión) =====
INSERT INTO cliente (nombre, documento, telefono, email, procedencia, localidad, contrasena, debe_cambiar_contrasena) VALUES
('Camila Olivera', 'PRUEBA-001', '092215279', 'cliente001@prueba.test', 'Montevideo', 'Montevideo', 'SIN-ACCESO-PRUEBA', 0),
('Santiago Núñez', 'PRUEBA-002', '099513358', 'cliente002@prueba.test', 'Buenos Aires (Argentina)', 'Buenos Aires', 'SIN-ACCESO-PRUEBA', 0),
('Valentina Píriz', 'PRUEBA-003', '095037655', 'cliente003@prueba.test', 'Paysandú', 'Paysandú', 'SIN-ACCESO-PRUEBA', 0),
('Sebastián González', 'PRUEBA-004', NULL, 'cliente004@prueba.test', 'Salto', 'Salto', 'SIN-ACCESO-PRUEBA', 0),
('Joaquín Rivero', 'PRUEBA-005', NULL, 'cliente005@prueba.test', 'Salto', 'Salto', 'SIN-ACCESO-PRUEBA', 0),
('Micaela González', 'PRUEBA-006', NULL, 'cliente006@prueba.test', 'Tacuarembó', 'Tacuarembó', 'SIN-ACCESO-PRUEBA', 0),
('Camila Cabrera', 'PRUEBA-007', '092976225', 'cliente007@prueba.test', 'Salto', 'Salto', 'SIN-ACCESO-PRUEBA', 0),
('Gonzalo López', 'PRUEBA-008', '094151952', 'cliente008@prueba.test', 'Tacuarembó', 'Tacuarembó', 'SIN-ACCESO-PRUEBA', 0),
('Gonzalo Pérez', 'PRUEBA-009', '094455413', 'cliente009@prueba.test', 'Montevideo', 'Montevideo', 'SIN-ACCESO-PRUEBA', 0),
('Gonzalo Píriz', 'PRUEBA-010', '098811503', 'cliente010@prueba.test', 'Artigas', 'Bella Unión', 'SIN-ACCESO-PRUEBA', 0),
('Carolina Núñez', 'PRUEBA-011', '094015985', 'cliente011@prueba.test', 'Tacuarembó', 'Tacuarembó', 'SIN-ACCESO-PRUEBA', 0),
('Joaquín Pérez', 'PRUEBA-012', '099811335', 'cliente012@prueba.test', 'Paysandú', 'Guichón', 'SIN-ACCESO-PRUEBA', 0),
('Agustina Ramos', 'PRUEBA-013', '092228106', 'cliente013@prueba.test', 'Artigas', 'Bella Unión', 'SIN-ACCESO-PRUEBA', 0),
('Sebastián López', 'PRUEBA-014', '093549877', 'cliente014@prueba.test', 'Salto', 'Salto', 'SIN-ACCESO-PRUEBA', 0),
('Sebastián González', 'PRUEBA-015', NULL, 'cliente015@prueba.test', 'Paysandú', 'Paysandu', 'SIN-ACCESO-PRUEBA', 0),
('Gonzalo Rivero', 'PRUEBA-016', '096263809', 'cliente016@prueba.test', 'Salto', 'Salto', 'SIN-ACCESO-PRUEBA', 0),
('Federico Benítez', 'PRUEBA-017', '098653855', 'cliente017@prueba.test', 'Montevideo', 'Montevideo', 'SIN-ACCESO-PRUEBA', 0),
('Valentina Techera', 'PRUEBA-018', '092090518', 'cliente018@prueba.test', 'Salto', 'Salto', 'SIN-ACCESO-PRUEBA', 0),
('Andrea Cabrera', 'PRUEBA-019', '098476611', 'cliente019@prueba.test', 'Salto', 'Salto', 'SIN-ACCESO-PRUEBA', 0),
('Micaela Núñez', 'PRUEBA-020', '098745961', 'cliente020@prueba.test', 'Montevideo', 'Montevideo', 'SIN-ACCESO-PRUEBA', 0),
('Pablo Fernández', 'PRUEBA-021', '094660918', 'cliente021@prueba.test', 'Montevideo', 'Montevideo', 'SIN-ACCESO-PRUEBA', 0),
('Camila Suárez', 'PRUEBA-022', '099330000', 'cliente022@prueba.test', 'Entre Ríos (Argentina)', 'Concordia', 'SIN-ACCESO-PRUEBA', 0),
('Carolina Olivera', 'PRUEBA-023', '093297239', 'cliente023@prueba.test', 'Salto', 'Salto', 'SIN-ACCESO-PRUEBA', 0),
('Gonzalo Techera', 'PRUEBA-024', '097019181', 'cliente024@prueba.test', 'Buenos Aires (Argentina)', 'Buenos Aires', 'SIN-ACCESO-PRUEBA', 0),
('Micaela Suárez', 'PRUEBA-025', '093956442', 'cliente025@prueba.test', 'Río Negro', 'Fray Bentos', 'SIN-ACCESO-PRUEBA', 0),
('Rodrigo Suárez', 'PRUEBA-026', '094059205', 'cliente026@prueba.test', 'Salto', 'Salto', 'SIN-ACCESO-PRUEBA', 0),
('Lucía Silva', 'PRUEBA-027', '097195046', 'cliente027@prueba.test', 'Montevideo', 'Montevideo', 'SIN-ACCESO-PRUEBA', 0),
('Agustina Silva', 'PRUEBA-028', '099648511', 'cliente028@prueba.test', 'Colonia', 'Colonia del Sacramento', 'SIN-ACCESO-PRUEBA', 0),
('Paula González', 'PRUEBA-029', '097583025', 'cliente029@prueba.test', 'Montevideo', 'Montevideo', 'SIN-ACCESO-PRUEBA', 0),
('Micaela Fernández', 'PRUEBA-030', '097718312', 'cliente030@prueba.test', 'Artigas', 'Artigas', 'SIN-ACCESO-PRUEBA', 0),
('Valentina Martínez', 'PRUEBA-031', '092844290', 'cliente031@prueba.test', 'Salto', 'Salto', 'SIN-ACCESO-PRUEBA', 0),
('Martín Fernández', 'PRUEBA-032', '093537804', 'cliente032@prueba.test', 'Montevideo', 'Montevideo', 'SIN-ACCESO-PRUEBA', 0),
('Federico Benítez', 'PRUEBA-033', '094488867', 'cliente033@prueba.test', 'Rivera', 'Rivera', 'SIN-ACCESO-PRUEBA', 0),
('Camila Techera', 'PRUEBA-034', NULL, 'cliente034@prueba.test', 'Colonia', 'Colonia del Sacramento', 'SIN-ACCESO-PRUEBA', 0),
('Diego Fernández', 'PRUEBA-035', '099188423', 'cliente035@prueba.test', 'Tacuarembó', 'Tacuarembó', 'SIN-ACCESO-PRUEBA', 0),
('Carolina Duarte', 'PRUEBA-036', '092440905', 'cliente036@prueba.test', 'Salto', NULL, 'SIN-ACCESO-PRUEBA', 0),
('Maximiliano Acosta', 'PRUEBA-037', '099029943', 'cliente037@prueba.test', 'Salto', 'Salto', 'SIN-ACCESO-PRUEBA', 0),
('Mateo Correa', 'PRUEBA-038', '099862688', 'cliente038@prueba.test', 'Buenos Aires (Argentina)', 'Buenos Aires', 'SIN-ACCESO-PRUEBA', 0),
('Andrea Machado', 'PRUEBA-039', NULL, 'cliente039@prueba.test', 'Montevideo', 'Montevideo', 'SIN-ACCESO-PRUEBA', 0),
('Nicolás Pérez', 'PRUEBA-040', '095380786', 'cliente040@prueba.test', 'Entre Ríos (Argentina)', 'Concordia', 'SIN-ACCESO-PRUEBA', 0),
('Mateo Núñez', 'PRUEBA-041', '099935417', 'cliente041@prueba.test', 'Rivera', 'Rivera', 'SIN-ACCESO-PRUEBA', 0),
('Natalia Acosta', 'PRUEBA-042', '094274007', 'cliente042@prueba.test', 'Rivera', 'Rivera', 'SIN-ACCESO-PRUEBA', 0),
('Micaela Suárez', 'PRUEBA-043', '099267507', 'cliente043@prueba.test', 'Entre Ríos (Argentina)', 'Concordia', 'SIN-ACCESO-PRUEBA', 0),
('Lucía Rodríguez', 'PRUEBA-044', '098922873', 'cliente044@prueba.test', 'Montevideo', 'Montevideo', 'SIN-ACCESO-PRUEBA', 0),
('Andrea Benítez', 'PRUEBA-045', NULL, 'cliente045@prueba.test', 'Montevideo', 'Montevideo', 'SIN-ACCESO-PRUEBA', 0),
('Maximiliano Núñez', 'PRUEBA-046', NULL, 'cliente046@prueba.test', 'Artigas', 'Artigas', 'SIN-ACCESO-PRUEBA', 0),
('Joaquín Fernández', 'PRUEBA-047', '094300181', 'cliente047@prueba.test', 'Montevideo', 'Montevideo', 'SIN-ACCESO-PRUEBA', 0),
('Diego Benítez', 'PRUEBA-048', NULL, 'cliente048@prueba.test', 'Montevideo', 'Montevideo', 'SIN-ACCESO-PRUEBA', 0),
('Lucía Duarte', 'PRUEBA-049', NULL, 'cliente049@prueba.test', 'Colonia', 'Colonia del Sacramento', 'SIN-ACCESO-PRUEBA', 0),
('Paula Pérez', 'PRUEBA-050', NULL, 'cliente050@prueba.test', 'Montevideo', 'Montevideo', 'SIN-ACCESO-PRUEBA', 0);

-- ===== 96 reservas (8 alojamientos x 12, sin superponerse), con su cliente, seña y reseña =====
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-01-27','2025-02-01',2,'CONFIRMADA','2024-12-21',1,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente012@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-01-29','2025-02-04',2,'CONFIRMADA','2025-01-13',2,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente050@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 4500, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 2, 'Nos encantó, los niños disfrutaron mucho.', 3, '2025-02-13' FROM cliente WHERE email = 'cliente050@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-02-05','2025-02-08',4,'CONFIRMADA','2025-01-07',6,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente026@prueba.test';
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 6, 'Tranquilo y con mucho verde, lo recomiendo.', 4, '2025-02-14' FROM cliente WHERE email = 'cliente026@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-02-21','2025-02-26',4,'CANCELADA','2025-01-30',7,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente036@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-03-01','2025-03-06',3,'CONFIRMADA','2025-02-19',6,1);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente022@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 3900, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 6, 'Tranquilo y con mucho verde, lo recomiendo.', 4, '2025-03-08' FROM cliente WHERE email = 'cliente022@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-03-04','2025-03-08',1,'CONFIRMADA','2025-01-19',5,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente033@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-03-10','2025-03-14',5,'CONFIRMADA','2025-01-28',3,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente037@prueba.test';
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 3, 'Estuvo bien, aunque el aire acondicionado hacía algo de ruido.', 3, '2025-03-19' FROM cliente WHERE email = 'cliente037@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-03-12','2025-03-17',3,'CANCELADA','2025-02-12',4,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente045@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-03-19','2025-03-22',2,'CONFIRMADA','2025-03-09',8,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente001@prueba.test';
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 8, 'Nos encantó, los niños disfrutaron mucho.', 4, '2025-03-31' FROM cliente WHERE email = 'cliente001@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-03-20','2025-03-25',1,'CONFIRMADA','2025-02-06',2,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente040@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 3750, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-03-25','2025-03-29',4,'CONFIRMADA','2025-03-10',1,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente048@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 3590, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 1, 'Muy lindo lugar, volveríamos sin dudar.', 4, '2025-04-05' FROM cliente WHERE email = 'cliente048@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-03-25','2025-03-31',6,'CONFIRMADA','2025-02-27',5,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente005@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 6660, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-03-25','2025-03-27',1,'CONFIRMADA','2025-02-27',7,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente024@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 1800, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 7, 'Tranquilo y con mucho verde, lo recomiendo.', 4, '2025-03-29' FROM cliente WHERE email = 'cliente024@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-03-31','2025-04-05',3,'CONFIRMADA','2025-02-26',4,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente017@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 4050, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-04-07','2025-04-11',1,'CONFIRMADA','2025-02-11',6,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente013@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 3120, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-04-15','2025-04-17',2,'CONFIRMADA','2025-03-19',3,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente005@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-04-20','2025-04-23',1,'CANCELADA','2025-02-28',1,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente030@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-04-23','2025-04-27',1,'CONFIRMADA','2025-04-09',6,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente038@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 3120, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-04-27','2025-04-30',6,'CONFIRMADA','2025-03-24',5,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente032@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-04-30','2025-05-02',1,'CONFIRMADA','2025-04-16',3,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente015@prueba.test';
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 3, 'Todo limpio y cómodo, la atención fue excelente.', 4, '2025-05-06' FROM cliente WHERE email = 'cliente015@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-05-07','2025-05-11',3,'CANCELADA','2025-04-12',8,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente042@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-05-08','2025-05-14',1,'CONFIRMADA','2025-04-15',4,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente020@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-05-14','2025-05-20',4,'CONFIRMADA','2025-04-25',1,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente023@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 5380, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 1, 'Nos encantó, los niños disfrutaron mucho.', 4, '2025-05-27' FROM cliente WHERE email = 'cliente023@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-05-14','2025-05-20',3,'CONFIRMADA','2025-04-04',2,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente035@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 4500, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 2, 'Ideal para descansar con la familia, cerca de las piscinas.', 5, '2025-05-25' FROM cliente WHERE email = 'cliente035@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-05-14','2025-05-17',4,'CANCELADA','2025-04-01',7,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente048@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-05-16','2025-05-21',3,'CANCELADA','2025-03-27',5,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente040@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-05-30','2025-06-01',3,'CONFIRMADA','2025-05-03',3,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente027@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 1800, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 3, 'Todo limpio y cómodo, la atención fue excelente.', 5, '2025-06-07' FROM cliente WHERE email = 'cliente027@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-06-01','2025-06-06',1,'CONFIRMADA','2025-04-10',6,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente004@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 3900, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 6, 'Todo limpio y cómodo, la atención fue excelente.', 5, '2025-06-13' FROM cliente WHERE email = 'cliente004@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-06-11','2025-06-16',1,'CONFIRMADA','2025-05-09',7,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente037@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 4500, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-06-17','2025-06-20',4,'CONFIRMADA','2025-05-06',4,1);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente018@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 2430, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 4, 'Todo limpio y cómodo, la atención fue excelente.', 4, '2025-06-22' FROM cliente WHERE email = 'cliente018@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-06-22','2025-06-28',2,'CONFIRMADA','2025-04-28',8,1);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente027@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 6660, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 8, 'Nos encantó, los niños disfrutaron mucho.', 4, '2025-07-06' FROM cliente WHERE email = 'cliente027@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-07-03','2025-07-06',3,'CONFIRMADA','2025-05-26',2,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente013@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 2250, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-07-06','2025-07-12',1,'CONFIRMADA','2025-05-16',1,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente047@prueba.test';
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 1, 'Todo limpio y cómodo, la atención fue excelente.', 5, '2025-07-21' FROM cliente WHERE email = 'cliente047@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-07-06','2025-07-09',3,'CONFIRMADA','2025-06-14',6,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente021@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 2340, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 6, 'Tranquilo y con mucho verde, lo recomiendo.', 4, '2025-07-14' FROM cliente WHERE email = 'cliente021@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-07-06','2025-07-12',2,'CONFIRMADA','2025-06-02',7,1);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente016@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 5400, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 7, 'Buena relación precio-calidad.', 4, '2025-07-15' FROM cliente WHERE email = 'cliente016@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-07-13','2025-07-16',2,'CONFIRMADA','2025-05-17',5,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente042@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 3330, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-07-22','2025-07-25',2,'CONFIRMADA','2025-06-26',3,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente011@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 2700, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 3, 'Buena relación precio-calidad.', 5, '2025-07-30' FROM cliente WHERE email = 'cliente011@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-07-26','2025-07-31',4,'CONFIRMADA','2025-07-01',2,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente043@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 3750, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-07-30','2025-08-05',2,'CONFIRMADA','2025-07-07',1,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente013@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-08-09','2025-08-12',3,'CONFIRMADA','2025-06-12',3,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente034@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 2700, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-08-12','2025-08-15',1,'CONFIRMADA','2025-07-24',4,1);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente006@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-08-13','2025-08-18',6,'CANCELADA','2025-06-29',6,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente036@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-08-16','2025-08-22',1,'CONFIRMADA','2025-07-20',5,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente032@prueba.test';
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 5, 'Buena relación precio-calidad.', 3, '2025-08-27' FROM cliente WHERE email = 'cliente032@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-08-18','2025-08-22',5,'CONFIRMADA','2025-07-19',1,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente038@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 3590, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-08-18','2025-08-23',3,'CONFIRMADA','2025-07-31',7,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente002@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-08-20','2025-08-24',2,'CONFIRMADA','2025-06-30',8,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente044@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 4440, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-09-06','2025-09-10',2,'CONFIRMADA','2025-08-11',3,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente002@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 3600, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 3, 'Todo limpio y cómodo, la atención fue excelente.', 5, '2025-09-11' FROM cliente WHERE email = 'cliente002@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-09-06','2025-09-11',4,'CONFIRMADA','2025-08-27',7,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente032@prueba.test';
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 7, 'Tranquilo y con mucho verde, lo recomiendo.', 4, '2025-09-12' FROM cliente WHERE email = 'cliente032@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-09-08','2025-09-10',3,'CONFIRMADA','2025-08-20',2,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente050@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 1500, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-09-12','2025-09-14',6,'CONFIRMADA','2025-07-16',6,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente040@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 1560, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-09-19','2025-09-25',2,'CONFIRMADA','2025-08-18',1,1);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente048@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-09-21','2025-09-27',1,'CONFIRMADA','2025-08-28',4,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente041@prueba.test';
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 4, 'Ideal para descansar con la familia, cerca de las piscinas.', 5, '2025-10-03' FROM cliente WHERE email = 'cliente041@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-09-24','2025-09-26',5,'CONFIRMADA','2025-08-13',3,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente013@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 1800, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 3, 'Nos encantó, los niños disfrutaron mucho.', 3, '2025-10-04' FROM cliente WHERE email = 'cliente013@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-09-27','2025-10-02',4,'CANCELADA','2025-07-29',7,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente016@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-10-01','2025-10-03',6,'CONFIRMADA','2025-09-07',8,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente024@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 2220, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 8, 'Ideal para descansar con la familia, cerca de las piscinas.', 5, '2025-10-12' FROM cliente WHERE email = 'cliente024@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-10-04','2025-10-08',5,'CONFIRMADA','2025-08-29',6,1);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente031@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 3120, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 6, 'Estuvo bien, aunque el aire acondicionado hacía algo de ruido.', 5, '2025-10-13' FROM cliente WHERE email = 'cliente031@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-10-14','2025-10-20',2,'CANCELADA','2025-08-15',4,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente046@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-10-16','2025-10-18',6,'CONFIRMADA','2025-09-03',5,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente046@prueba.test';
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 5, 'La casa estaba muy bien equipada.', 5, '2025-10-19' FROM cliente WHERE email = 'cliente046@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-10-20','2025-10-23',1,'CONFIRMADA','2025-08-27',7,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente047@prueba.test';
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 7, 'Tranquilo y con mucho verde, lo recomiendo.', 5, '2025-10-24' FROM cliente WHERE email = 'cliente047@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-11-02','2025-11-06',2,'CONFIRMADA','2025-09-06',2,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente015@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 3000, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 2, 'Estuvo bien, aunque el aire acondicionado hacía algo de ruido.', 5, '2025-11-07' FROM cliente WHERE email = 'cliente015@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-11-05','2025-11-11',5,'CONFIRMADA','2025-10-18',1,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente033@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 5380, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-11-07','2025-11-10',1,'CANCELADA','2025-10-01',3,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente042@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-11-10','2025-11-14',6,'CONFIRMADA','2025-10-15',6,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente042@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 3120, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-11-15','2025-11-17',6,'CONFIRMADA','2025-10-11',8,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente044@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 2220, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 8, 'Muy lindo lugar, volveríamos sin dudar.', 5, '2025-11-18' FROM cliente WHERE email = 'cliente044@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-11-17','2025-11-22',1,'CONFIRMADA','2025-10-26',5,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente036@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-11-24','2025-11-29',2,'CANCELADA','2025-11-04',2,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente015@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-12-09','2025-12-14',4,'CONFIRMADA','2025-10-21',4,1);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente047@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 4050, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-12-11','2025-12-13',6,'CONFIRMADA','2025-11-18',8,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente005@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 2220, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-12-15','2025-12-20',1,'CONFIRMADA','2025-11-21',7,1);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente009@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 4500, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 7, 'Ideal para descansar con la familia, cerca de las piscinas.', 5, '2025-12-29' FROM cliente WHERE email = 'cliente009@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-12-21','2025-12-24',5,'CONFIRMADA','2025-12-01',6,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente008@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 2340, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-12-23','2025-12-25',1,'CANCELADA','2025-11-11',5,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente005@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2025-12-27','2025-12-30',1,'CONFIRMADA','2025-12-06',1,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente050@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-01-03','2026-01-08',4,'CONFIRMADA','2025-11-10',3,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente020@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 4500, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 3, 'Nos encantó, los niños disfrutaron mucho.', 4, '2026-01-09' FROM cliente WHERE email = 'cliente020@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-01-07','2026-01-13',4,'CONFIRMADA','2025-12-23',2,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente021@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-01-25','2026-01-28',7,'CONFIRMADA','2026-01-01',8,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente030@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 3330, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-02-02','2026-02-05',2,'CONFIRMADA','2026-01-20',3,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente023@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-02-03','2026-02-07',2,'CONFIRMADA','2026-01-15',5,1);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente006@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-02-05','2026-02-08',4,'CONFIRMADA','2026-01-18',4,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente033@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-02-06','2026-02-08',3,'CONFIRMADA','2026-01-11',7,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente041@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 1800, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-02-15','2026-02-18',5,'CONFIRMADA','2026-01-01',6,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente032@prueba.test';
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 6, 'Ideal para descansar con la familia, cerca de las piscinas.', 4, '2026-02-27' FROM cliente WHERE email = 'cliente032@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-02-17','2026-02-19',4,'CONFIRMADA','2026-01-14',2,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente002@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 1500, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 2, 'Muy lindo lugar, volveríamos sin dudar.', 5, '2026-02-23' FROM cliente WHERE email = 'cliente002@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-02-18','2026-02-20',2,'CONFIRMADA','2025-12-31',1,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente031@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-02-25','2026-02-27',3,'CONFIRMADA','2026-02-10',3,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente004@prueba.test';
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 3, 'Nos encantó, los niños disfrutaron mucho.', 5, '2026-03-06' FROM cliente WHERE email = 'cliente004@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-02-25','2026-03-01',5,'CONFIRMADA','2026-02-01',8,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente012@prueba.test';
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 8, 'La casa estaba muy bien equipada.', 5, '2026-03-08' FROM cliente WHERE email = 'cliente012@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-03-11','2026-03-17',5,'CONFIRMADA','2026-01-10',1,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente031@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 5380, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-03-24','2026-03-30',1,'CONFIRMADA','2026-03-08',2,1);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente015@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 4500, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 2, 'Muy lindo lugar, volveríamos sin dudar.', 5, '2026-03-31' FROM cliente WHERE email = 'cliente015@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-03-24','2026-03-28',2,'CANCELADA','2026-02-10',5,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente041@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-03-25','2026-03-31',1,'CONFIRMADA','2026-01-29',4,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente038@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 4860, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 4, 'Nos encantó, los niños disfrutaron mucho.', 5, '2026-04-10' FROM cliente WHERE email = 'cliente038@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-03-25','2026-03-30',1,'CONFIRMADA','2026-02-10',7,1);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente020@prueba.test';
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 7, 'Ideal para descansar con la familia, cerca de las piscinas.', 4, '2026-04-01' FROM cliente WHERE email = 'cliente020@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-04-13','2026-04-18',1,'CANCELADA','2026-03-09',8,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente010@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-04-16','2026-04-20',2,'CONFIRMADA','2026-03-10',2,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente009@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 3000, 1);
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-04-26','2026-04-28',2,'CONFIRMADA','2026-03-22',5,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente032@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-05-03','2026-05-06',5,'CONFIRMADA','2026-03-09',8,1);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente004@prueba.test';
INSERT INTO sena (reserva_id, importe, usada) VALUES (LAST_INSERT_ID(), 3330, 1);
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 8, 'Todo limpio y cómodo, la atención fue excelente.', 3, '2026-05-12' FROM cliente WHERE email = 'cliente004@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-05-21','2026-05-24',6,'CONFIRMADA','2026-05-04',8,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente047@prueba.test';
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 8, 'Muy lindo lugar, volveríamos sin dudar.', 5, '2026-05-27' FROM cliente WHERE email = 'cliente047@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-05-25','2026-05-28',1,'CONFIRMADA','2026-05-09',4,1);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente024@prueba.test';
INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id, medioDiaExtra) VALUES ('2026-07-03','2026-07-08',1,'CONFIRMADA','2026-06-08',4,0);
INSERT INTO cliente_reserva (idCliente, idReserva) SELECT id, LAST_INSERT_ID() FROM cliente WHERE email = 'cliente044@prueba.test';
INSERT INTO resena (cliente_id, alojamiento_id, comentario, puntuacion, fecha) SELECT id, 4, 'La casa estaba muy bien equipada.', 5, '2026-07-16' FROM cliente WHERE email = 'cliente044@prueba.test';

COMMIT;

-- Comprobación rápida:
-- SELECT COUNT(*) FROM cliente WHERE email LIKE '%@prueba.test';
