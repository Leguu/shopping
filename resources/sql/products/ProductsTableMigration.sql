CREATE TABLE IF NOT EXISTS Products (
	id int key not null auto_increment,
    slug varchar(255) not null,
    name varchar(255) not null,
    description varchar(255) not null,
    price decimal not null,
    PRIMARY KEY (id)
);