CREATE TABLE usuario(
    id BIGSERIAL PRIMARY KEY ,
    nombre VARCHAR(50) NOT NULL ,
    apellido VARCHAR(50) NOT NULL ,
    email VARCHAR(50) NOT NULL UNIQUE ,
    password VARCHAR(200) NOT NULL ,
    tipo VARCHAR(50) NOT NULL ,
    fecha_nacimiento DATE
);

CREATE TABLE categoria(
    id BIGSERIAL PRIMARY KEY ,
    nombre VARCHAR(50) NOT NULL UNIQUE ,
    descripcion VARCHAR(200) NOT NULL 
);

CREATE TABLE direccion(
    id BIGSERIAL PRIMARY KEY ,
    calle VARCHAR(150) NOT NULL ,
    ciudad VARCHAR(50) NOT NULL ,
    provincia VARCHAR(50) NOT NULL ,
    codigo_postal VARCHAR(10) NOT NULL ,
    pais VARCHAR(50) NOT NULL ,
    id_usuario BIGINT NOT NULL,
    constraint fk_direccion_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id)

);

CREATE TABLE producto(
    id BIGSERIAL primary key ,
    nombre VARCHAR(100) NOT NULL UNIQUE ,
    descripcion VARCHAR(500) NOT NULL,
    precio DECIMAL(10,2) NOT NULL ,
    stock INT NOT NULL DEFAULT 0,
    codigo_referencia VARCHAR(50) NOT NULL UNIQUE,
    fecha_registro timestamp,
    producto_activo BOOLEAN DEFAULT TRUE,
    fecha_baja timestamp


);

CREATE TABLE producto_categoria(
    producto_id BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    PRIMARY KEY (producto_id, categoria_id),
    CONSTRAINT fk_prod_cat_producto FOREIGN KEY (producto_id) REFERENCES producto(id),
    CONSTRAINT fk_prod_cat_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

CREATE TABLE pedido(
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_direccion_envio BIGINT NOT NULL,
    fecha_pedido TIMESTAMP,
    estado_pedido VARCHAR(50) NOT NULL,
    total DECIMAL(10,2),
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id),
    CONSTRAINT fk_pedido_direccion FOREIGN KEY (id_direccion_envio) REFERENCES direccion(id)
);

CREATE TABLE detalle_pedido(
    id BIGSERIAL PRIMARY KEY,
    id_pedido BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detalle_pedido FOREIGN KEY (id_pedido) REFERENCES pedido(id),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES producto(id)
);
