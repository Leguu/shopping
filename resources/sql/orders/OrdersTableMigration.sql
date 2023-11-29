CREATE TABLE IF NOT EXISTS Orders (
    orderId INTEGER PRIMARY KEY,
    userId INTEGER,
    address text not null,
    trackingId text
);

CREATE TABLE IF NOT EXISTS OrderProduct (
    orderId int not null,
    productId int not null,
    quantity int not null,
    primary key (orderId, productId)
);
