CREATE TABLE IF NOT EXISTS Products (
	id INTEGER PRIMARY KEY,
    slug TEXT not null,
    name TEXT not null,
    description TEXT not null,
    price decimal not null
);
