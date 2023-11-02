CREATE TABLE IF NOT EXISTS UserProductCart (
    userId int not null,
    productId int not null,
    quantity int not null,
    primary key (userId, productId),
    foreign key (userId) references users(id),
    foreign key (productId) references products(id)
);