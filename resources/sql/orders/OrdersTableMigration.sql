CREATE TABLE IF NOT EXISTS Orders (
    orderId int not null auto_increment,
    userId int not null,
    address varchar(255) not null,
    trackingId varchar(255),
    primary key (orderId),
    foreign key (userId) references users(id)
);

CREATE TABLE IF NOT EXISTS OrderProduct (
    orderId int not null,
    productId int not null,
    quantity int not null,
    primary key (orderId, productId),
    foreign key (orderId) references orders(orderId),
    foreign key (productId) references products(id)
);