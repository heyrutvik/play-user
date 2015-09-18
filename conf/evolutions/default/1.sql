# --- !Ups
CREATE TABLE user(
       `id` int primary key auto_increment,
       `username` varchar(255),
       `email` varchar(255),
       `password` varchar(255),
       `stamp` timestamp default current_timestamp
);

CREATE TABLE todo(
       `id` int primary key auto_increment,
       `user_id` int,
       `title` varchar(255),
       `desc` text,
       `stamp` timestamp default current_timestamp
)
# --- !Downs
DROP TABLE user;
DROP TABLE todo;
