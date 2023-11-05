CREATE TABLE IF NOT EXISTS Users (
	id int key not null auto_increment,
	name varchar(255) not null,
	password varchar(255) not null,
	isAdmin boolean not null,
    PRIMARY KEY (id)
);