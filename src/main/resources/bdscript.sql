drop database ad_ev1_examen1;

create schema ad_ev1_examen1;

use ad_ev1_examen1;

create table if not exists clientes
(
    codigo_cli int         not null
    primary key,
    nombre_cli char(30)    not null,
    nif        varchar(9)  null,
    direccion  varchar(30) null,
    ciudad     varchar(20) null,
    telefono   varchar(11) null,
    constraint nif
    unique (nif)
    );


create table if not exists proyectos
(
    codigo_proyec  int            not null
    primary key,
    nombre_proyec  varchar(20)    null,
    precio         decimal(10, 2) null,
    codigo_cliente int            null,
    constraint proyectos_ibfk_1
    foreign key (codigo_cliente) references clientes (codigo_cli)
    );


create index fk_codigo_cliente
    on proyectos (codigo_cliente);
