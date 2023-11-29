CREATE TABLE IF NOT EXISTS Users (
	id INTEGER PRIMARY KEY,
	password text not null,
	isAdmin boolean not null
);
