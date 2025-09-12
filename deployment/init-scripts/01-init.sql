CREATE EXTENSION IF NOT EXISTS "pgcrypto";
-- Roles
CREATE TABLE public.roles (
                              id_rol integer NOT NULL,
                              nombre character varying(255) NOT NULL,
                              descripcion character varying(255)
);

CREATE SEQUENCE public.roles_id_rol_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE ONLY public.roles
    ALTER COLUMN id_rol SET DEFAULT nextval('public.roles_id_rol_seq'::regclass);

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id_rol);

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_nombre_key UNIQUE (nombre);

-- Usuarios
CREATE TABLE public.usuarios (
                                 id_usuario uuid DEFAULT gen_random_uuid() NOT NULL,
                                 nombres character varying(100) NOT NULL,
                                 apellidos character varying(100) NOT NULL,
                                 fecha_nacimiento date,
                                 direccion character varying(255),
                                 telefono character varying(20),
                                 email character varying(150) NOT NULL,
                                 salario_base numeric(15,2) NOT NULL,
                                 numero_documento character varying,
                                 tipo_documento character varying,
                                 id_rol integer NOT NULL,
                                 password_hash character varying(255)
);

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT users_pkey PRIMARY KEY (id_usuario);

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT users_email_key UNIQUE (email);

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT documento_unique UNIQUE (tipo_documento, numero_documento);

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_id_rol_fkey FOREIGN KEY (id_rol) REFERENCES public.roles(id_rol);

-- ============================================================
-- Datos iniciales
-- ============================================================

-- Roles
INSERT INTO public.roles (id_rol, nombre, descripcion) VALUES
                                                           (1, 'ADMIN', 'administrador ppal de crediYa'),
                                                           (2, 'ASESOR', 'Equipo de asesores de crediYa a cargo del administrador'),
                                                           (3, 'CLIENTE', 'Nuestros valiosos clientes');

SELECT pg_catalog.setval('public.roles_id_rol_seq', 3, true);

-- Usuarios
INSERT INTO public.usuarios (id_usuario, nombres, apellidos, fecha_nacimiento, direccion, telefono, email, salario_base, numero_documento, tipo_documento, id_rol, password_hash) VALUES
                                                                                                                                                                                      ('28f279f3-1ad7-47a7-a7e4-d3c9473afdc1','Santiago','Velez Suarez','1955-10-10','cra 30 87 62','3052383416','santiagovelezsuarez@gmail.com',3750000.00,'12345678','CC',1,'$2a$10$VE0eqTwKvznoJxhHjYb5qOS.EPuEDCr9Yvt44zvccHnY5/XfLyKHG'),
                                                                                                                                                                                      ('4b3ea549-7bc5-4e60-8b35-3e00f7301ba6','Asesor A','... ...','1971-10-10','Av 10 30 50','3102558819','asesora@gmail.com',2750000.00,'123456782','CC',2,'$2a$10$VE0eqTwKvznoJxhHjYb5qOS.EPuEDCr9Yvt44zvccHnY5/XfLyKHG'),
                                                                                                                                                                                      ('66b4a6e1-1d4e-4126-8e92-0ad9bbbc7121','Cliente A','... ...','1973-11-19','Cl 19 # 31 - 54','3202887113','clientea@gmail.com',3850000.00,'123456783','CC',3,'$2a$10$VE0eqTwKvznoJxhHjYb5qOS.EPuEDCr9Yvt44zvccHnY5/XfLyKHG'),
                                                                                                                                                                                      ('ab677858-6ba6-4d9c-ade6-57a00357191a','Cliente B','... ...','1983-10-27','Cl 190 # 106 - 96','3015589631','clienteb@gmail.com',1950000.00,'123456784','CC',3,'$2a$10$VE0eqTwKvznoJxhHjYb5qOS.EPuEDCr9Yvt44zvccHnY5/XfLyKHG');