create database lab2;

use lab2;

create table if not exists clients(
    id int primary key auto_increment unique ,
    name varchar(50),
    surname varchar(50),
    money double
);

create table if not exists orders(
     orderNumber int primary key auto_increment unique ,
     isApproved boolean,
     clientId int unique,
     FOREIGN KEY (clientId) references clients(id)
);

create table if not exists products(
    id int primary key auto_increment unique ,
    name varchar(50),
    price double,
    description varchar(200)
);

create table if not exists orders_products(
    orderId int,
    productId int,
    PRIMARY KEY (orderId, productId),
    FOREIGN KEY (orderId) references orders(orderNumber),
    FOREIGN KEY (productId) references products(id)
);

create table if not exists admins(
     id int primary key auto_increment unique,
     name varchar(50)
);

create table if not exists bills(
    id int primary key auto_increment unique
);

create table if not exists bills_orders(
    billId int,
    orderId int,
    PRIMARY KEY (billId, orderId),
    FOREIGN KEY (billId) references bills(id),
    FOREIGN KEY (orderId) references orders(orderNumber)
);